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

public class ManagerItemUpdateServlet extends HttpServlet {

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
            String name = req.getParameter("name");
            String priceStr = req.getParameter("price");
            boolean isActive = req.getParameter("isActive") != null;

            if (itemCode == null || name == null || priceStr == null) {
                resp.sendRedirect(req.getContextPath() + "/manager/items?error=missing_fields");
                return;
            }

            double price = Double.parseDouble(priceStr);

            ManagerService managerService = new ManagerServiceImpl(new JdbcManagerRepository());
            managerService.updateItem(itemCode.trim(), name.trim(), price, isActive, managerId);

            resp.sendRedirect(req.getContextPath() + "/manager/items?success=item_updated");

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/manager/items?error=invalid_price");
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/manager/items?error=" + e.getMessage().replace(" ", "_"));
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/manager/items?error=update_failed");
        }
    }
}
