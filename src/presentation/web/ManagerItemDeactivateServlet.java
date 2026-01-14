package presentation.web;

import application.ManagerServiceImpl;
import infrastructure.jdbc.JdbcManagerRepository;
import ports.in.ManagerService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class ManagerItemDeactivateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Manager access control
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html");
            return;
        }

        String role = (String) session.getAttribute("staffRole");
        Long managerId = (Long) session.getAttribute("staffUserId");
        if (!"MANAGER".equals(role) || managerId == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Manager access required");
            return;
        }

        try {
            String itemCode = req.getParameter("itemCode");

            if (itemCode == null || itemCode.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/manager/items?error=missing_item_code");
                return;
            }

            ManagerService managerService = new ManagerServiceImpl(new JdbcManagerRepository());
            managerService.deactivateItem(itemCode.trim(), managerId);

            resp.sendRedirect(req.getContextPath() + "/manager/items?success=item_deactivated");

        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/manager/items?error=" + e.getMessage().replace(" ", "_"));
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/manager/items?error=deactivation_failed");
        }
    }
}
