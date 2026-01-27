package presentation.web;

import domain.store.Cart;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;

public class PosCartClearServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUserId") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html");
            return;
        }

        Cart cart = SessionCart.getOrCreatePosCart(session);
        cart.clear();

        resp.sendRedirect(req.getContextPath() + "/cart/view");
    }
}
