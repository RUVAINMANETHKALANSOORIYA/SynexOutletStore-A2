package presentation.web;

import domain.store.Cart;
import domain.exception.InvalidCartOperationException;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.logging.Logger;

public class CartRemoveServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CartRemoveServlet.class.getName());

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

        try {
            // Validate parameters
            if (itemCode == null || itemCode.trim().isEmpty()) {
                throw InvalidCartOperationException.invalidItemCode(itemCode);
            }

            Cart cart = SessionCart.getOrCreate(session);

            // Check if cart is empty
            if (cart.isEmpty()) {
                redirectWithError(req, resp, "empty_cart", "Cart is already empty");
                return;
            }

            // Check if item exists in cart before removal
            if (!cart.containsItem(itemCode.trim())) {
                redirectWithError(req, resp, "item_not_in_cart", "Item not found in cart: " + itemCode);
                return;
            }

            int removedQty = cart.getQuantity(itemCode.trim());
            cart.remove(itemCode.trim());

            logger.info("Customer " + customerId + " removed " + removedQty + " units of " + itemCode + " from cart");

            resp.sendRedirect(req.getContextPath() + "/cart/view?success=item_removed");

        } catch (InvalidCartOperationException e) {
            logger.warning("Cart remove operation failed for customer " + customerId + ": " + e.getMessage());
            redirectWithError(req, resp, "cart_error", e.getMessage());

        } catch (Exception e) {
            logger.severe("Unexpected error removing item from cart for customer " + customerId + ": " + e.getMessage());
            e.printStackTrace();
            redirectWithError(req, resp, "system_error", "System error occurred. Please try again.");
        }
    }

    private void redirectWithError(HttpServletRequest req, HttpServletResponse resp,
                                 String errorCode, String errorMessage) throws IOException {
        logger.info("Redirecting with error - Code: " + errorCode + ", Message: " + errorMessage);
        resp.sendRedirect(req.getContextPath() + "/cart/view?error=" + errorCode + "&message=" +
                         java.net.URLEncoder.encode(errorMessage, "UTF-8"));
    }
}
