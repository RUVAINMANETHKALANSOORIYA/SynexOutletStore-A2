package presentation.web;

import infrastructure.jdbc.JdbcUserAdminRepository;
import ports.in.CashierRegisterService;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;

public class ManagerRegisterCashierServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ✅ Authorization: only MANAGER can do this
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html?error=login");
            return;
        }

        String role = (String) session.getAttribute("staffRole");
        if (role == null || !"MANAGER".equalsIgnoreCase(role)) {
            resp.sendError(403, "Access denied (Manager only).");
            return;
        }

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email    = req.getParameter("email");

        var repo = new JdbcUserAdminRepository();
        var service = new CashierRegisterService(repo);

        try {
            service.registerCashier(username, password, email);
            resp.sendRedirect(req.getContextPath() + "/manager-dashboard.html?cashier=created");
        } catch (IllegalArgumentException ex) {
            resp.sendRedirect(req.getContextPath() + "/manager-register-cashier.html?error=1");
        }
    }
}
