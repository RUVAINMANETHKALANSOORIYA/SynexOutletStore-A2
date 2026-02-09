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

public class CheckoutServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CheckoutServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/customer-login.html");
            return;
        }

        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            resp.sendRedirect(req.getContextPath() + "/customer-login.html");
            return;
        }

        // Get the session cart (domain.store.Cart)
        domain.store.Cart storeCart = (domain.store.Cart) session.getAttribute("cart");
        if (storeCart == null || storeCart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/checkout.html?error=empty_cart");
            return;
        }

        String paymentMethodParam = req.getParameter("paymentMethod");
        if (paymentMethodParam == null || paymentMethodParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/checkout.html?error=no_payment_method");
            return;
        }

        PaymentMethod pm;
        try {
            pm = PaymentMethod.valueOf(paymentMethodParam);
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/checkout.html?error=invalid_payment_method");
            return;
        }

        // Convert domain.store.Cart to domain.cart.Cart for checkout
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

        var discountService = new application.DiscountServiceImpl(new infrastructure.jdbc.JdbcDiscountRepository());

        // Check if discount code was entered
        String discountCodeParam = req.getParameter("discountCode");
        domain.cart.Cart discountedCart;

        if (discountCodeParam != null && !discountCodeParam.trim().isEmpty()) {
            // Apply specific discount code
            discountedCart = applyDiscountCode(checkoutCart, discountCodeParam.trim(), discountService, itemRepo);
        } else {
            // Auto-apply best available item discounts
            discountedCart = discountService.applyItemDiscountsToCart(checkoutCart, itemRepo);
        }

        // Calculate total discount amount for logging and database
        java.math.BigDecimal totalDiscountAmount = discountedCart.totalDiscountAmount();

        logger.info("Customer checkout discounts applied: Total savings LKR " + totalDiscountAmount + " for customer " + customerId);


        // Create checkout task as Callable with item-specific discount processing
        final java.math.BigDecimal finalTotalDiscountAmount = totalDiscountAmount;

        Callable<Long> checkoutTask = () -> {
            String workerThread = Thread.currentThread().getName();
            logger.info("Processing online checkout on thread: " + workerThread +
                       " for customer: " + customerId +
                       (finalTotalDiscountAmount.compareTo(java.math.BigDecimal.ZERO) > 0 ?
                        " with item discounts totaling: LKR " + finalTotalDiscountAmount : ""));

            CheckoutService checkout = new CheckoutServiceImpl(
                    new JdbcOrderRepository(),
                    new JdbcStockRepository()
            );

            // Use the discounted cart for checkout
            long orderId = checkout.placeOnlineOrder(customerId, discountedCart, pm);

            if (finalTotalDiscountAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
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
                        stmt.setBigDecimal(1, finalTotalDiscountAmount);
                        stmt.setLong(2, orderId);
                        stmt.executeUpdate();
                    }

                    logger.info("Item discounts recorded for order " + orderId +
                              " with total savings: LKR " + finalTotalDiscountAmount);

                } catch (Exception e) {
                    logger.severe("Failed to record discount usage for order " + orderId + ": " + e.getMessage());
                    // Don't fail the entire order for discount recording issues
                }
            }

            logger.info("Online checkout completed on thread: " + workerThread +
                       " for customer: " + customerId + ", orderId: " + orderId +
                       (finalTotalDiscountAmount.compareTo(java.math.BigDecimal.ZERO) > 0 ?
                        ", discount saved: LKR " + finalTotalDiscountAmount : ""));

            return orderId;
        };

        CheckoutQueue queue = CheckoutQueue.getInstance();

        // Log before submission
        logger.info("Submitting online checkout to queue - Thread: " +
                   Thread.currentThread().getName() +
                   ", Queue size: " + queue.getQueueSize() +
                   ", Active count: " + queue.getActiveCount() +
                   ", Customer: " + customerId);

        try {
            // Submit to queue
            Future<Long> future = queue.submit(checkoutTask);

            // Wait for result with timeout
            long orderId = future.get(30, TimeUnit.SECONDS);

            // Log after completion
            logger.info("Online checkout completed - Thread: " +
                       Thread.currentThread().getName() +
                       ", Queue size: " + queue.getQueueSize() +
                       ", Active count: " + queue.getActiveCount() +
                       ", OrderId: " + orderId);

            storeCart.clear();
            session.setAttribute("cart", storeCart);

            resp.sendRedirect(req.getContextPath() + "/checkout-success.html?orderId=" + orderId);

        } catch (RejectedExecutionException ex) {
            logger.warning("Checkout queue full - rejecting request for customer: " + customerId);
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("<!DOCTYPE html><html><body>");
            resp.getWriter().println("<h1>Server Busy</h1>");
            resp.getWriter().println("<p>Server busy. Please try again in a moment.</p>");
            resp.getWriter().println("<a href='" + req.getContextPath() + "/checkout.html'>Back to Checkout</a>");
            resp.getWriter().println("</body></html>");

        } catch (TimeoutException ex) {
            logger.warning("Checkout timeout for customer: " + customerId);
            resp.sendRedirect(req.getContextPath() + "/checkout.html?error=checkout_timeout");

        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            logger.severe("Checkout execution error for customer: " + customerId + " - " + cause.getMessage());

            // Enhanced error handling for specific exception types
            if (cause instanceof domain.exception.CheckoutFailedException) {
                domain.exception.CheckoutFailedException checkoutEx = (domain.exception.CheckoutFailedException) cause;
                if (checkoutEx.getMessage().contains("empty cart")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=empty_cart");
                } else if (checkoutEx.getMessage().contains("stock depletion")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=stock_depleted");
                } else if (checkoutEx.getMessage().contains("payment processing")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=payment_failed");
                } else if (checkoutEx.getMessage().contains("timeout")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=processing_timeout");
                } else if (checkoutEx.getMessage().contains("concurrent")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=concurrent_checkout");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=checkout_failed");
                }
            } else if (cause instanceof domain.exception.InvalidCartOperationException) {
                domain.exception.InvalidCartOperationException cartEx = (domain.exception.InvalidCartOperationException) cause;
                if (cartEx.getMessage().contains("empty")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=empty_cart");
                } else if (cartEx.getMessage().contains("quantity")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=invalid_cart_quantities");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=cart_validation_failed");
                }
            } else if (cause instanceof IllegalStateException && cause.getMessage().contains("stock")) {
                // Stock availability error during checkout process
                resp.sendRedirect(req.getContextPath() + "/checkout.html?error=insufficient_stock_checkout");
            } else if (cause instanceof IllegalArgumentException) {
                // Validation error (invalid payment method, etc.)
                if (cause.getMessage().contains("payment")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=invalid_payment_method");
                } else if (cause.getMessage().contains("customer")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=invalid_customer");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=validation_error");
                }
            } else if (cause instanceof java.sql.SQLException) {
                // Database related errors
                if (cause.getMessage().contains("connection")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=database_connection");
                } else if (cause.getMessage().contains("constraint")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=data_constraint_violation");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=database_error");
                }
            } else if (cause instanceof java.util.concurrent.RejectedExecutionException) {
                // Secondary queue rejection
                resp.sendRedirect(req.getContextPath() + "/checkout.html?error=system_overload");
            } else {
                // Generic error with more specific categorization
                String errorMsg = cause.getMessage().toLowerCase();
                if (errorMsg.contains("stock")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=stock_issue");
                } else if (errorMsg.contains("payment")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=payment_issue");
                } else if (errorMsg.contains("network") || errorMsg.contains("connection")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=network_error");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/checkout.html?error=processing_error");
                }
            }

        } catch (InterruptedException ex) {
            logger.warning("Checkout interrupted for customer: " + customerId);
            Thread.currentThread().interrupt();
            resp.sendRedirect(req.getContextPath() + "/checkout.html?error=checkout_interrupted");

        } catch (domain.exception.InvalidCartOperationException ex) {
            // Handle cart validation errors before submission
            logger.warning("Cart validation failed before checkout for customer: " + customerId + " - " + ex.getMessage());
            if (ex.getMessage().contains("empty")) {
                resp.sendRedirect(req.getContextPath() + "/checkout.html?error=empty_cart_validation");
            } else if (ex.getMessage().contains("quantity")) {
                resp.sendRedirect(req.getContextPath() + "/checkout.html?error=invalid_quantities");
            } else {
                resp.sendRedirect(req.getContextPath() + "/checkout.html?error=cart_validation_error");
            }

        } catch (IllegalArgumentException ex) {
            // Handle parameter validation errors
            logger.warning("Parameter validation failed for customer: " + customerId + " - " + ex.getMessage());
            if (ex.getMessage().contains("payment")) {
                resp.sendRedirect(req.getContextPath() + "/checkout.html?error=invalid_payment_selection");
            } else {
                resp.sendRedirect(req.getContextPath() + "/checkout.html?error=invalid_parameters");
            }

        } catch (SecurityException ex) {
            // Handle security-related errors
            logger.severe("Security error during checkout for customer: " + customerId + " - " + ex.getMessage());
            resp.sendRedirect(req.getContextPath() + "/checkout.html?error=security_violation");

        } catch (Exception ex) {
            // Catch-all for any other unexpected errors
            logger.severe("Unexpected error during checkout for customer: " + customerId + " - " + ex.getMessage());
            ex.printStackTrace();

            // Try to provide more specific error based on exception type
            if (ex instanceof java.io.IOException) {
                resp.sendRedirect(req.getContextPath() + "/checkout.html?error=io_error");
            } else {
                resp.sendRedirect(req.getContextPath() + "/checkout.html?error=system_error");
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

    private domain.cart.Cart applyDiscountCode(domain.cart.Cart originalCart, String discountCode,
                                              application.DiscountServiceImpl discountService,
                                              infrastructure.jdbc.JdbcItemRepository itemRepo) {
        try {
            var discountOpt = discountService.validateDiscountCode(discountCode);
            if (discountOpt.isEmpty()) {
                return originalCart; // Return original cart if code is invalid
            }

            var discount = discountOpt.get();
            domain.cart.Cart discountedCart = new domain.cart.Cart();

            for (var item : originalCart.items()) {
                // Get item category for checking if discount applies
                var itemOpt = itemRepo.findByCode(item.itemCode());
                String itemCategory = itemOpt.isPresent() ? itemOpt.get().category() : null;

                if (discount.appliesTo(item.itemCode(), itemCategory)) {
                    // Apply discount to this item
                    java.math.BigDecimal discountAmount = discount.calculateItemDiscount(
                        new java.math.BigDecimal(item.unitPrice()), item.qty());

                    discountedCart.addWithDiscount(item.itemCode(), item.name(), item.unitPrice(),
                                                  item.qty(), discountAmount, discountCode);
                } else {
                    // No discount for this item
                    discountedCart.add(item.itemCode(), item.name(), item.unitPrice(), item.qty());
                }
            }

            return discountedCart;
        } catch (Exception e) {
            logger.warning("Failed to apply discount code '" + discountCode + "': " + e.getMessage());
            return originalCart; // Return original cart on error
        }
    }
}
