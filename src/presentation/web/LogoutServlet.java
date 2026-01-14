package presentation.web;

import jakarta.servlet.http.*;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        performLogout(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        performLogout(req, resp);
    }

    private void performLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();

        // Redirect based on the URL pattern
        String requestURI = req.getRequestURI();
        if (requestURI.contains("/customer/logout")) {
            resp.sendRedirect(req.getContextPath() + "/customer-login.html");
        } else {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html");
        }
    }
}
