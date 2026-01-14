package presentation.web;

import infrastructure.jdbc.JdbcUserAdminRepository;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.IOException;

public class ManagerStaffListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Manager-only guard
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffRole") == null) {
            resp.sendError(401, "Not logged in");
            return;
        }
        String role = (String) session.getAttribute("staffRole");
        if (!"MANAGER".equalsIgnoreCase(role)) {
            resp.sendError(403, "Manager only");
            return;
        }

        var repo = new JdbcUserAdminRepository();
        var staff = repo.listStaffUsers();

        resp.setContentType("text/html;charset=UTF-8");

        StringBuilder html = new StringBuilder();

        if (staff.isEmpty()) {
            html.append("<div class='text-center py-8 text-gray-500'>");
            html.append("<p>No staff members found.</p>");
            html.append("</div>");
        } else {
            html.append("<div class='overflow-x-auto'>");
            html.append("<table class='w-full'>");
            html.append("<thead class='bg-gradient-to-r from-indigo-600 to-purple-600 text-white'>");
            html.append("<tr>");
            html.append("<th class='px-4 py-3 text-left font-semibold'>ID</th>");
            html.append("<th class='px-4 py-3 text-left font-semibold'>Username</th>");
            html.append("<th class='px-4 py-3 text-left font-semibold'>Role</th>");
            html.append("<th class='px-4 py-3 text-left font-semibold'>Email</th>");
            html.append("<th class='px-4 py-3 text-left font-semibold'>Status</th>");
            html.append("</tr></thead>");
            html.append("<tbody class='divide-y divide-gray-200'>");

            for (var u : staff) {
                html.append("<tr class='hover:bg-gray-50 transition duration-200'>");
                html.append("<td class='px-4 py-3 font-mono text-sm text-gray-600'>").append(u.id()).append("</td>");
                html.append("<td class='px-4 py-3 font-semibold text-gray-800'>").append(escape(u.username())).append("</td>");

                // Role badge with color
                String roleClass = "MANAGER".equalsIgnoreCase(u.role())
                    ? "bg-purple-100 text-purple-800"
                    : "bg-blue-100 text-blue-800";
                html.append("<td class='px-4 py-3'>");
                html.append("<span class='inline-block px-3 py-1 rounded-full text-xs font-semibold ").append(roleClass).append("'>");
                html.append(escape(u.role())).append("</span></td>");

                html.append("<td class='px-4 py-3 text-gray-700'>").append(escape(u.email())).append("</td>");

                // Status badge with color
                String statusClass = "ACTIVE".equalsIgnoreCase(u.status())
                    ? "bg-green-100 text-green-800"
                    : "bg-red-100 text-red-800";
                html.append("<td class='px-4 py-3'>");
                html.append("<span class='inline-block px-3 py-1 rounded-full text-xs font-semibold ").append(statusClass).append("'>");
                html.append(escape(u.status())).append("</span></td>");

                html.append("</tr>");
            }

            html.append("</tbody></table>");
            html.append("</div>");
        }

        resp.getWriter().write(html.toString());
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
