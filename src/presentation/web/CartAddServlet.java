package presentation.web;

import domain.store.Cart;
import domain.exception.InvalidCartOperationException;
import infrastructure.jdbc.JdbcItemRepository;
import infrastructure.jdbc.JdbcStockRepository;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.logging.Logger;

public class CartAddServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CartAddServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("customerId") == null) {
            resp.sendRedirect(req.getContextPath() + "/customer-login.html");
            return;
        }

        Long customerId = (Long) session.getAttribute("customerId");
        String itemCode = req.getParameter("itemCode");
        String qtyParam = req.getParameter("qty");

        try {
            // Enhanced parameter validation
            validateParameters(itemCode, qtyParam);

            // Sanitize item code
            String sanitizedItemCode = itemCode.trim().toUpperCase();

            // Additional item code format validation
            if (sanitizedItemCode.length() > 20) {
                redirectWithError(req, resp, "item_code_too_long", "Item code too long: " + sanitizedItemCode);
                return;
            }

            if (!sanitizedItemCode.matches("^[A-Z0-9_-]+$")) {
                redirectWithError(req, resp, "invalid_item_code_format", "Invalid item code format: " + sanitizedItemCode);
                return;
            }

            int qty = parseInt(qtyParam, 1);

            // Enhanced quantity validation
            if (qty <= 0) {
                redirectWithError(req, resp, "invalid_quantity", "Quantity must be positive: " + qty);
                return;
            }

            if (qty > 100) { // Reasonable upper limit for web cart
                redirectWithError(req, resp, "quantity_too_large", "Quantity too large: " + qty + ". Maximum allowed: 100");
                return;
            }

            // Validate item exists and is active
            var itemRepo = new JdbcItemRepository();
            var itemOpt = itemRepo.findByCode(sanitizedItemCode);
            if (itemOpt.isEmpty()) {
                redirectWithError(req, resp, "item_not_found", "Item not found: " + sanitizedItemCode);
                return;
            }

            var item = itemOpt.get();
            if (!item.isActive()) {
                redirectWithError(req, resp, "item_inactive", "Item is no longer available: " + sanitizedItemCode);
                return;
            }

            // Check stock availability with enhanced error handling
            var stockRepo = new JdbcStockRepository();
            int availableStock;
            try {
                availableStock = stockRepo.getAvailableWebStock(sanitizedItemCode);
            } catch (Exception e) {
                logger.severe("Failed to check stock for item " + sanitizedItemCode + ": " + e.getMessage());
                redirectWithError(req, resp, "stock_check_failed", "Unable to verify stock availability");
                return;
            }

            if (availableStock <= 0) {
                redirectWithError(req, resp, "out_of_stock", "Item is currently out of stock: " + sanitizedItemCode);
                return;
            }

            Cart cart = SessionCart.getOrCreate(session);

            // Check current cart state
            if (cart.uniqueItemCount() >= 50) { // Cart size limit
                redirectWithError(req, resp, "cart_full", "Cart is full. Cannot add more items.");
                return;
            }

            int currentCartQty = cart.getQuantity(sanitizedItemCode);
            int totalRequested = currentCartQty + qty;

            // Prevent integer overflow
            if (currentCartQty > Integer.MAX_VALUE - qty) {
                redirectWithError(req, resp, "quantity_overflow", "Quantity calculation overflow detected");
                return;
            }

            if (totalRequested > availableStock) {
                String message = String.format("Insufficient stock for item %s. Available: %d, In cart: %d, Requested: %d",
                    sanitizedItemCode, availableStock, currentCartQty, qty);
                redirectWithError(req, resp, "insufficient_stock", message);
                return;
            }

            // Additional business rule: prevent adding same item too frequently (simple spam protection)
            String lastAddedItem = (String) session.getAttribute("lastAddedItem");
            Long lastAddedTime = (Long) session.getAttribute("lastAddedTime");
            long currentTime = System.currentTimeMillis();

            if (sanitizedItemCode.equals(lastAddedItem) && lastAddedTime != null &&
                (currentTime - lastAddedTime) < 1000) { // 1 second cooldown
                redirectWithError(req, resp, "add_too_fast", "Please wait before adding the same item again");
                return;
            }

            // Add to cart with validation
            cart.add(sanitizedItemCode, qty);

            // Update session tracking
            session.setAttribute("lastAddedItem", sanitizedItemCode);
            session.setAttribute("lastAddedTime", currentTime);

            logger.info("Customer " + customerId + " added " + qty + " units of " + sanitizedItemCode + " to cart. " +
                       "Total in cart: " + cart.getQuantity(sanitizedItemCode) +
                       ", Total unique items: " + cart.uniqueItemCount());

            resp.sendRedirect(req.getContextPath() + "/cart/view?success=item_added&item=" + sanitizedItemCode);

        } catch (InvalidCartOperationException e) {
            logger.warning("Cart operation failed for customer " + customerId + ": " + e.getMessage());

            // Categorize cart operation errors for better UX
            if (e.getMessage().contains("maximum quantity")) {
                redirectWithError(req, resp, "max_quantity_exceeded", e.getMessage());
            } else if (e.getMessage().contains("cart size limit")) {
                redirectWithError(req, resp, "cart_size_limit", e.getMessage());
            } else if (e.getMessage().contains("overflow")) {
                redirectWithError(req, resp, "quantity_overflow", e.getMessage());
            } else {
                redirectWithError(req, resp, "cart_error", e.getMessage());
            }

        } catch (NumberFormatException e) {
            logger.warning("Invalid quantity format for customer " + customerId + ": " + qtyParam);
            redirectWithError(req, resp, "invalid_quantity_format", "Invalid quantity format: " + qtyParam);

        } catch (RuntimeException e) {
            logger.severe("Runtime error adding item to cart for customer " + customerId + ": " + e.getMessage());

            if (e.getMessage().contains("database") || e.getMessage().contains("connection")) {
                redirectWithError(req, resp, "database_error", "Database connection error. Please try again.");
            } else {
                redirectWithError(req, resp, "system_error", "System error occurred. Please try again.");
            }

        } catch (Exception e) {
            logger.severe("Unexpected error adding item to cart for customer " + customerId + ": " + e.getMessage());
            e.printStackTrace();
            redirectWithError(req, resp, "system_error", "System error occurred. Please try again.");
        }
    }

    private void validateParameters(String itemCode, String qtyParam) throws InvalidCartOperationException {
        if (itemCode == null || itemCode.trim().isEmpty()) {
            throw InvalidCartOperationException.invalidItemCode(itemCode);
        }

        if (qtyParam == null || qtyParam.trim().isEmpty()) {
            throw new InvalidCartOperationException("Quantity parameter is required");
        }
    }

    private void redirectWithError(HttpServletRequest req, HttpServletResponse resp,
                                 String errorCode, String errorMessage) throws IOException {
        logger.info("Redirecting with error - Code: " + errorCode + ", Message: " + errorMessage);
        resp.sendRedirect(req.getContextPath() + "/cart/view?error=" + errorCode + "&message=" +
                         java.net.URLEncoder.encode(errorMessage, "UTF-8"));
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }
}
