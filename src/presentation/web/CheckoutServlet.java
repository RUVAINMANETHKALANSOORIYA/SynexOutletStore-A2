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

        // ✅ FIX 1: get customerId from session
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

        // ✅ Validate payment method parameter
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


        // Create checkout task as Callable
        Callable<Long> checkoutTask = () -> {
            String workerThread = Thread.currentThread().getName();
            logger.info("Processing online checkout on thread: " + workerThread +
                       " for customer: " + customerId);

            CheckoutService checkout = new CheckoutServiceImpl(
                    new JdbcOrderRepository(),
                    new JdbcStockRepository()
            );

            long orderId = checkout.placeOnlineOrder(customerId, checkoutCart, pm);

            logger.info("Online checkout completed on thread: " + workerThread +
                       " for customer: " + customerId + ", orderId: " + orderId);

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
            resp.getWriter().println("<p>Server busy. Please try again.</p>");
            resp.getWriter().println("<a href='" + req.getContextPath() + "/checkout.html'>Back to Checkout</a>");
            resp.getWriter().println("</body></html>");

        } catch (TimeoutException ex) {
            logger.warning("Checkout timeout for customer: " + customerId);
            resp.sendRedirect(req.getContextPath() + "/checkout.html?error=timeout");

        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            logger.severe("Checkout execution error for customer: " + customerId + " - " + cause.getMessage());

            // ✅ Handle specific error types
            if (cause instanceof IllegalStateException && cause.getMessage().contains("stock")) {
                // Stock availability error
                resp.sendRedirect(req.getContextPath() + "/checkout.html?error=insufficient_stock");
            } else if (cause instanceof IllegalArgumentException) {
                // Validation error (empty cart, invalid payment method)
                resp.sendRedirect(req.getContextPath() + "/checkout.html?error=validation");
            } else {
                // Generic error
                resp.sendRedirect(req.getContextPath() + "/checkout.html?error=processing");
            }

        } catch (InterruptedException ex) {
            logger.warning("Checkout interrupted for customer: " + customerId);
            Thread.currentThread().interrupt();
            resp.sendRedirect(req.getContextPath() + "/checkout.html?error=interrupted");

        } catch (Exception ex) {
            logger.severe("Unexpected error during checkout for customer: " + customerId + " - " + ex.getMessage());
            resp.sendRedirect(req.getContextPath() + "/checkout.html?error=unexpected");
        }
    }
}
