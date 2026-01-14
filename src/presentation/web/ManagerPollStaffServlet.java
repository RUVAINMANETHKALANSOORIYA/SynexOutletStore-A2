package presentation.web;

import domain.auth.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import infrastructure.jdbc.Db;

public class ManagerPollStaffServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Manager access control
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"Not authenticated\"}");
            return;
        }

        String role = (String) session.getAttribute("staffRole");
        if (!"MANAGER".equals(role)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"Manager access required\"}");
            return;
        }

        try {
            List<User> staffList = getAllStaffUsers();

            resp.setContentType("application/json;charset=UTF-8");
            PrintWriter out = resp.getWriter();

            out.print("{\"staff\":[");
            boolean first = true;
            for (User staff : staffList) {
                if (!first) out.print(",");
                out.print("{");
                out.print("\"userId\":" + staff.id() + ",");
                out.print("\"username\":\"" + escapeJson(staff.username()) + "\",");
                out.print("\"role\":\"" + escapeJson(staff.role()) + "\",");
                out.print("\"email\":\"" + escapeJson(staff.email() != null ? staff.email() : "") + "\",");
                out.print("\"status\":\"" + escapeJson(staff.status()) + "\"");
                out.print("}");
                first = false;
            }
            out.print("],\"timestamp\":" + System.currentTimeMillis() + "}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private List<User> getAllStaffUsers() throws SQLException {
        String sql = """
                SELECT user_id, username, role, email, status
                FROM users
                WHERE role IN ('MANAGER', 'CASHIER')
                ORDER BY role DESC, username
                """;

        List<User> staffList = new ArrayList<>();

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    staffList.add(new User(
                            rs.getLong("user_id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.getString("email"),
                            rs.getString("status")
                    ));
                }
            }
        }

        return staffList;
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
