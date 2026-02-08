package presentation.web;

import application.CheckoutServiceImpl;
import domain.orders.PaymentMethod;
import infrastructure.concurrency.CheckoutQueue;
import infrastructure.jdbc.JdbcItemRepository;
import infrastructure.jdbc.JdbcOrderRepository;
import infrastructure.jdbc.JdbcStockRepository;
import ports.in.CheckoutService;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class PosCheckoutServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(PosCheckoutServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html");
            return;
        }

        Long staffUserId = (Long) session.getAttribute("staffUserId");
        String role = (String) session.getAttribute("staffRole");

        if (staffUserId == null || role == null) {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html");
            return;
        }

        // Optional safety: only CASHIER/MANAGER can POS checkout
        if (!("CASHIER".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role))) {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html");
            return;
        }

        // Use separate cart for POS to avoid conflict with customer carts
        domain.store.Cart storeCart = (domain.store.Cart) session.getAttribute("posCart");
        if (storeCart == null || storeCart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=empty_cart");
            return;
        }

        // ✅ Validate payment method parameter
        String paymentMethodParam = req.getParameter("paymentMethod");
        if (paymentMethodParam == null || paymentMethodParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=no_payment_method");
            return;
        }

        PaymentMethod pm;
        try {
            pm = PaymentMethod.valueOf(paymentMethodParam);
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=invalid_payment_method");
            return;
        }

        // Convert store cart -> domain.cart.Cart (same as online approach)
        domain.cart.Cart checkoutCart = new domain.cart.Cart();
        var itemRepo = new JdbcItemRepository();

        for (var entry : storeCart.items().entrySet()) {
            String itemCode = entry.getKey();
            int qty = entry.getValue();

            var itemOpt = itemRepo.findByCode(itemCode);
            if (itemOpt.isPresent()) {
                var item = itemOpt.get();
                checkoutCart.add(itemCode, item.name(), item.price().doubleValue(), qty);
            }
        }

        // ✅ NEW: Apply item-specific discounts
        var discountService = new application.DiscountServiceImpl(new infrastructure.jdbc.JdbcDiscountRepository());
        domain.cart.Cart discountedCart = discountService.applyItemDiscountsToCart(checkoutCart, itemRepo);

        // Calculate total discount amount for logging and database
        java.math.BigDecimal totalDiscountAmount = discountedCart.totalDiscountAmount();

        logger.info("POS Item discounts applied: Total savings LKR " + totalDiscountAmount + " by staff " + staffUserId);

        // Create POS checkout task as Callable with discount processing
        final java.math.BigDecimal finalDiscountAmount = totalDiscountAmount;

        Callable<Long> posCheckoutTask = () -> {
            String workerThread = Thread.currentThread().getName();
            logger.info("Processing POS checkout on thread: " + workerThread +
                       " for staff: " + staffUserId + " (role: " + role + ")" +
                       (finalDiscountAmount.compareTo(java.math.BigDecimal.ZERO) > 0 ?
                        " with item discounts totaling: LKR " + finalDiscountAmount : ""));

            CheckoutService checkout = new CheckoutServiceImpl(
                    new JdbcOrderRepository(),
                    new JdbcStockRepository()
            );

            // Use the discounted cart for checkout
            long orderId = checkout.placePosOrder(staffUserId, discountedCart, pm);

            // ✅ Record item-specific discount usage
            if (finalDiscountAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
                try {
                    var discountRepo = new infrastructure.jdbc.JdbcDiscountRepository();

                    // Record discount usage for each discounted item
                    for (var item : discountedCart.items()) {
                        if (item.hasDiscount()) {
                            // Find the discount by code to get ID
                            var discountOpt = discountRepo.findByCode(item.appliedDiscountCode());
                            if (discountOpt.isPresent()) {
                                var discount = discountOpt.get();

                                // Increment usage count
                                discountRepo.incrementUsageCount(discount.discountId());

                                // Record usage for this order
                                discountRepo.recordDiscountUsage((int) orderId, discount.discountId(), item.discountAmount());
                            }
                        }
                    }

                    // Update order with total discount amount
                    try (var conn = infrastructure.jdbc.Db.get();
                         var stmt = conn.prepareStatement("UPDATE orders SET discount_amount = ? WHERE order_id = ?")) {
                        stmt.setBigDecimal(1, finalDiscountAmount);
                        stmt.setLong(2, orderId);
                        stmt.executeUpdate();
                    }

                    logger.info("POS Item discounts recorded for order " + orderId +
                              " with total savings: LKR " + finalDiscountAmount);

                } catch (Exception e) {
                    logger.severe("Failed to record POS discount usage for order " + orderId + ": " + e.getMessage());
                    // Don't fail the entire order for discount recording issues
                }
            }

            logger.info("POS checkout completed on thread: " + workerThread +
                       " for staff: " + staffUserId + ", orderId: " + orderId +
                       (finalDiscountAmount.compareTo(java.math.BigDecimal.ZERO) > 0 ?
                        ", discount saved: LKR " + finalDiscountAmount : ""));

            return orderId;
        };

        CheckoutQueue queue = CheckoutQueue.getInstance();

        // Log before submission
        logger.info("Submitting POS checkout to queue - Thread: " +
                   Thread.currentThread().getName() +
                   ", Queue size: " + queue.getQueueSize() +
                   ", Active count: " + queue.getActiveCount() +
                   ", Staff: " + staffUserId + " (" + role + ")");

        try {
            // Submit to queue
            Future<Long> future = queue.submit(posCheckoutTask);

            // Wait for result with timeout
            long orderId = future.get(30, TimeUnit.SECONDS);

            // Log after completion
            logger.info("POS checkout completed - Thread: " +
                       Thread.currentThread().getName() +
                       ", Queue size: " + queue.getQueueSize() +
                       ", Active count: " + queue.getActiveCount() +
                       ", OrderId: " + orderId);

            storeCart.clear();
            session.setAttribute("posCart", storeCart);

            resp.sendRedirect(req.getContextPath() + "/pos-success.html?orderId=" + orderId);

        } catch (RejectedExecutionException ex) {
            logger.warning("POS checkout queue full - rejecting request for staff: " + staffUserId);
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("<!DOCTYPE html><html><body>");
            resp.getWriter().println("<h1>Server Busy</h1>");
            resp.getWriter().println("<p>Server busy. Please try again.</p>");
            resp.getWriter().println("<a href='" + req.getContextPath() + "/pos-checkout.html'>Back to POS Checkout</a>");
            resp.getWriter().println("</body></html>");

        } catch (TimeoutException ex) {
            logger.warning("POS checkout timeout for staff: " + staffUserId);
            resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=timeout");

        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            logger.severe("POS checkout execution error for staff: " + staffUserId + " - " + cause.getMessage());

            // Enhanced error handling for specific exception types
            if (cause instanceof domain.exception.CheckoutFailedException) {
                domain.exception.CheckoutFailedException checkoutEx = (domain.exception.CheckoutFailedException) cause;
                if (checkoutEx.getMessage().contains("empty cart")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=empty_cart");
                } else if (checkoutEx.getMessage().contains("stock depletion") || checkoutEx.getMessage().contains("insufficient")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=insufficient_shelf_stock");
                } else if (checkoutEx.getMessage().contains("payment")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=payment_validation_failed");
                } else if (checkoutEx.getMessage().contains("timeout")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=processing_timeout");
                } else if (checkoutEx.getMessage().contains("concurrent")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=concurrent_pos_checkout");
                } else if (checkoutEx.getMessage().contains("database")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=database_error");
                } else if (checkoutEx.getMessage().contains("invalid customer") || checkoutEx.getMessage().contains("cashier")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=invalid_cashier");
                } else if (checkoutEx.getMessage().contains("item")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=invalid_item");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_checkout_failed");
                }
            } else if (cause instanceof domain.exception.InvalidCartOperationException) {
                domain.exception.InvalidCartOperationException cartEx = (domain.exception.InvalidCartOperationException) cause;
                if (cartEx.getMessage().contains("empty")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=empty_pos_cart");
                } else if (cartEx.getMessage().contains("quantity")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=invalid_pos_quantities");
                } else if (cartEx.getMessage().contains("size limit")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_cart_too_large");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_cart_validation_failed");
                }
            } else if (cause instanceof IllegalStateException && cause.getMessage().contains("stock")) {
                // Stock availability error during POS checkout process
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=shelf_stock_depleted");
            } else if (cause instanceof IllegalArgumentException) {
                // Validation error (invalid payment method, etc.)
                if (cause.getMessage().contains("payment")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=invalid_pos_payment_method");
                } else if (cause.getMessage().contains("cashier") || cause.getMessage().contains("user")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=invalid_pos_user");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_validation_error");
                }
            } else if (cause instanceof java.sql.SQLException) {
                // Database related errors specific to POS
                if (cause.getMessage().contains("connection")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_database_connection");
                } else if (cause.getMessage().contains("constraint")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_data_constraint");
                } else if (cause.getMessage().contains("foreign key")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_invalid_reference");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_database_error");
                }
            } else if (cause instanceof java.util.concurrent.RejectedExecutionException) {
                // Secondary queue rejection
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_system_overload");
            } else if (cause instanceof SecurityException) {
                // Security violations in POS context
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_security_violation");
            } else {
                // Generic error with more specific categorization for POS
                String errorMsg = cause.getMessage().toLowerCase();
                if (errorMsg.contains("shelf") || errorMsg.contains("stock")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=shelf_stock_issue");
                } else if (errorMsg.contains("payment")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_payment_issue");
                } else if (errorMsg.contains("network") || errorMsg.contains("connection")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_network_error");
                } else if (errorMsg.contains("authorization") || errorMsg.contains("permission")) {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_authorization_failed");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_processing_error");
                }
            }

        } catch (InterruptedException ex) {
            logger.warning("POS checkout interrupted for staff: " + staffUserId);
            Thread.currentThread().interrupt();
            resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_interrupted");

        } catch (domain.exception.InvalidCartOperationException ex) {
            // Handle POS cart validation errors before submission
            logger.warning("POS cart validation failed before checkout for staff: " + staffUserId + " - " + ex.getMessage());
            if (ex.getMessage().contains("empty")) {
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=empty_pos_cart_validation");
            } else if (ex.getMessage().contains("quantity")) {
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=invalid_pos_cart_quantities");
            } else {
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_cart_validation_error");
            }

        } catch (IllegalArgumentException ex) {
            // Handle POS parameter validation errors
            logger.warning("POS parameter validation failed for staff: " + staffUserId + " - " + ex.getMessage());
            if (ex.getMessage().contains("payment")) {
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=invalid_pos_payment_selection");
            } else {
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=invalid_pos_parameters");
            }

        } catch (SecurityException ex) {
            // Handle POS security-related errors
            logger.severe("POS security error during checkout for staff: " + staffUserId + " - " + ex.getMessage());
            resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_security_violation");

        } catch (Exception ex) {
            // Catch-all for any other unexpected errors in POS context
            logger.severe("Unexpected error during POS checkout for staff: " + staffUserId + " - " + ex.getMessage());
            ex.printStackTrace();

            // Try to provide more specific error based on exception type
            if (ex instanceof java.io.IOException) {
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_io_error");
            } else if (ex instanceof RuntimeException && ex.getMessage() != null && ex.getMessage().contains("discount")) {
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_discount_error");
            } else {
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=pos_system_error");
            }
        }
    }

    // Helper method to calculate cart total
    private java.math.BigDecimal calculateCartTotal(domain.cart.Cart cart) {
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        for (var item : cart.items()) {
            // Use discounted line total if discount is applied
            if (item.hasDiscount()) {
                total = total.add(item.discountedLineTotal());
            } else {
                java.math.BigDecimal itemTotal = new java.math.BigDecimal(item.unitPrice())
                        .multiply(new java.math.BigDecimal(item.qty()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }
}
