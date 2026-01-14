package presentation.web;

import infrastructure.jdbc.JdbcUserRepository;
import ports.in.AuthService;
import domain.auth.User;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;

public class StaffLoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        var userRepo = new JdbcUserRepository();
        var auth = new AuthService(userRepo);

        Optional<User> user;

        try {
            user = auth.login(username, password);
        } catch (IllegalArgumentException ex) {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html?error=invalid");
            return;
        }

        if (user.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html?error=invalid");
            return;
        }

        // Session (per-user, thread-safe)
        HttpSession session = req.getSession(true);
        session.setAttribute("staffUserId", user.get().id());
        session.setAttribute("staffRole", user.get().role());
        session.setAttribute("staffUsername", user.get().username());

        // Role-based routing
        switch (user.get().role().toUpperCase()) {
            case "MANAGER" ->
                    resp.sendRedirect(req.getContextPath() + "/manager-dashboard.html");
            case "CASHIER" ->
                    resp.sendRedirect(req.getContextPath() + "/cashier-dashboard.html");
            default ->
                    resp.sendRedirect(req.getContextPath() + "/staff-login.html?error=role");
        }
    }
}
