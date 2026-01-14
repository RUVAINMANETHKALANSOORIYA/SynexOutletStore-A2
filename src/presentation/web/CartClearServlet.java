package presentation.web;

import domain.store.Cart;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;

public class CartClearServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("customerId") == null) {
            resp.sendRedirect(req.getContextPath() + "/customer-login.html");
            return;
        }

        Cart cart = SessionCart.getOrCreate(session);
        cart.clear();

        resp.sendRedirect(req.getContextPath() + "/cart/view");
    }
}
