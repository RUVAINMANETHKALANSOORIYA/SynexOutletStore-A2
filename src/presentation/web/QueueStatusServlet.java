package presentation.web;

import infrastructure.concurrency.CheckoutQueue;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

public class QueueStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Require staff authentication (both MANAGER and CASHIER can view)
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUserId") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"Not authenticated\"}");
            return;
        }

        try {
            CheckoutQueue checkoutQueue = CheckoutQueue.getInstance();

            int activeThreads = checkoutQueue.getActiveCount();
            int queueSize = checkoutQueue.getQueueSize();
            int poolSize = checkoutQueue.getPoolSize();

            resp.setContentType("application/json;charset=UTF-8");
            PrintWriter out = resp.getWriter();

            out.print("{");
            out.print("\"activeThreads\":" + activeThreads + ",");
            out.print("\"queueSize\":" + queueSize + ",");
            out.print("\"poolSize\":" + poolSize + ",");
            out.print("\"corePoolSize\":4,");  // From CheckoutQueue constants
            out.print("\"maxPoolSize\":4,");   // From CheckoutQueue constants
            out.print("\"queueCapacity\":100,"); // From CheckoutQueue constants
            out.print("\"timestamp\":" + System.currentTimeMillis());
            out.print("}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
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
