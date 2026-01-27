package presentation.web;

import domain.store.Cart;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;

public class PosCartAddServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUserId") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html");
            return;
        }

        String itemCode = req.getParameter("itemCode");
        int qty = parseInt(req.getParameter("qty"), 1);

        Cart cart = SessionCart.getOrCreatePosCart(session);
        cart.add(itemCode, qty);

        resp.sendRedirect(req.getContextPath() + "/pos/home");
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
