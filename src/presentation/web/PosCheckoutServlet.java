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

        // Session cart you already use in UI: domain.store.Cart
        domain.store.Cart storeCart = (domain.store.Cart) session.getAttribute("cart");
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


        // Create POS checkout task as Callable
        Callable<Long> posCheckoutTask = () -> {
            String workerThread = Thread.currentThread().getName();
            logger.info("Processing POS checkout on thread: " + workerThread +
                       " for staff: " + staffUserId + " (role: " + role + ")");

            CheckoutService checkout = new CheckoutServiceImpl(
                    new JdbcOrderRepository(),
                    new JdbcStockRepository()
            );

            long orderId = checkout.placePosOrder(staffUserId, checkoutCart, pm);

            logger.info("POS checkout completed on thread: " + workerThread +
                       " for staff: " + staffUserId + ", orderId: " + orderId);

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
            session.setAttribute("cart", storeCart);

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

            // ✅ Handle specific error types
            if (cause instanceof IllegalStateException && cause.getMessage().contains("stock")) {
                // Stock availability error
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=insufficient_stock");
            } else if (cause instanceof IllegalArgumentException) {
                // Validation error (empty cart, invalid payment method)
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=validation");
            } else {
                // Generic error
                resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=processing");
            }

        } catch (InterruptedException ex) {
            logger.warning("POS checkout interrupted for staff: " + staffUserId);
            Thread.currentThread().interrupt();
            resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=interrupted");

        } catch (Exception ex) {
            logger.severe("Unexpected error during POS checkout for staff: " + staffUserId + " - " + ex.getMessage());
            resp.sendRedirect(req.getContextPath() + "/pos-checkout.html?error=unexpected");
        }
    }
}
