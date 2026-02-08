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

public class ManagerStockTransferServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Manager access control
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html");
            return;
        }

        String role = (String) session.getAttribute("staffRole");
        if (!"MANAGER".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Manager access required");
            return;
        }

        try {
            ManagerService managerService = new ManagerServiceImpl(new JdbcManagerRepository());
            var activeItems = managerService.getActiveItems();
            var todayTransfers = managerService.getTodayTransfers();
            var lowStockAlerts = managerService.getLowStockAlerts();

            resp.setContentType("text/html;charset=UTF-8");
            var writer = resp.getWriter();

            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<meta charset='utf-8'>");
            writer.println("<title>Stock Transfer - SYOS</title>");
            writer.println("<script src='https://cdn.tailwindcss.com'></script>");
            writer.println("</head>");
            writer.println("<body class='bg-gray-50 min-h-screen'>");

            // Header
            writer.println("<header class='bg-white shadow-sm border-b'>");
            writer.println("<div class='container mx-auto px-4 py-4 flex justify-between items-center'>");
            writer.println("<h1 class='text-2xl font-bold text-gray-900'>📦➡️🌐 Stock Transfer</h1>");
            writer.println("<div class='space-x-4'>");
            writer.println("<a href='" + req.getContextPath() + "/manager/dashboard' class='text-blue-600 hover:underline'>← Dashboard</a>");
            writer.println("<a href='" + req.getContextPath() + "/logout' class='text-red-600 hover:underline'>Logout</a>");
            writer.println("</div>");
            writer.println("</div>");
            writer.println("</header>");

            writer.println("<div class='container mx-auto px-4 py-8 max-w-6xl'>");

            // Low Stock Alerts
            if (!lowStockAlerts.isEmpty()) {
                writer.println("<div class='bg-red-50 border border-red-200 rounded-lg p-4 mb-6'>");
                writer.println("<h3 class='text-red-800 font-semibold text-lg mb-2'>🚨 Reorder Alerts</h3>");
                writer.println("<ul class='text-red-700'>");
                for (String alert : lowStockAlerts) {
                    writer.println("<li class='mb-1'>• " + alert + "</li>");
                }
                writer.println("</ul>");
                writer.println("</div>");
            }

            // Transfer Form
            writer.println("<div class='bg-white rounded-lg shadow-sm p-6 mb-8'>");
            writer.println("<h3 class='text-xl font-semibold text-gray-900 mb-4'>📦➡️🌐 SHELF to WEB Transfer</h3>");
            writer.println("<p class='text-gray-600 mb-4'>Transfer stock from physical shelf to online web inventory.</p>");

            writer.println("<form method='post' action='" + req.getContextPath() + "/manager/stock/transfer' class='grid grid-cols-1 md:grid-cols-4 gap-4'>");
            writer.println("<div>");
            writer.println("<label class='block text-sm font-medium text-gray-700 mb-1'>Item</label>");
            writer.println("<select name='itemCode' required class='w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none'>");
            writer.println("<option value=''>Select Item...</option>");
            for (var item : activeItems) {
                writer.println("<option value='" + item.code() + "'>" + item.code() + " - " + item.name() + "</option>");
            }
            writer.println("</select>");
            writer.println("</div>");

            writer.println("<div>");
            writer.println("<label class='block text-sm font-medium text-gray-700 mb-1'>Quantity</label>");
            writer.println("<input type='number' name='quantity' min='1' placeholder='Enter quantity' required class='w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none'>");
            writer.println("</div>");

            writer.println("<div>");
            writer.println("<label class='block text-sm font-medium text-gray-700 mb-1'>Notes (Optional)</label>");
            writer.println("<input type='text' name='notes' placeholder='Transfer notes' class='w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none'>");
            writer.println("</div>");

            writer.println("<div class='flex items-end'>");
            writer.println("<button type='submit' class='w-full bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 font-medium'>Transfer Stock</button>");
            writer.println("</div>");
            writer.println("</form>");
            writer.println("</div>");

            // Today's Transfers
            writer.println("<div class='bg-white rounded-lg shadow-sm overflow-hidden'>");
            writer.println("<div class='px-6 py-4 bg-gray-50 border-b'>");
            writer.println("<h3 class='text-xl font-semibold text-gray-900'>📋 Today's Transfers</h3>");
            writer.println("</div>");

            if (todayTransfers.isEmpty()) {
                writer.println("<div class='p-8 text-center text-gray-500'>");
                writer.println("No transfers made today.");
                writer.println("</div>");
            } else {
                writer.println("<div class='overflow-x-auto'>");
                writer.println("<table class='w-full'>");
                writer.println("<thead class='bg-gray-50'>");
                writer.println("<tr class='text-left text-sm font-medium text-gray-500 uppercase tracking-wider'>");
                writer.println("<th class='px-6 py-3'>Time</th>");
                writer.println("<th class='px-6 py-3'>Item Code</th>");
                writer.println("<th class='px-6 py-3'>Transfer Type</th>");
                writer.println("<th class='px-6 py-3'>Quantity</th>");
                writer.println("<th class='px-6 py-3'>Notes</th>");
                writer.println("</tr>");
                writer.println("</thead>");
                writer.println("<tbody class='divide-y divide-gray-200'>");

                for (var transfer : todayTransfers) {
                    writer.println("<tr class='hover:bg-gray-50'>");
                    writer.println("<td class='px-6 py-4 text-sm'>" + transfer.transferDate().toLocalTime().toString() + "</td>");
                    writer.println("<td class='px-6 py-4 font-mono text-sm'>" + transfer.itemCode() + "</td>");
                    writer.println("<td class='px-6 py-4'>");
                    writer.println("<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800'>");
                    writer.println(transfer.transferType().toString().replace("_", " "));
                    writer.println("</span>");
                    writer.println("</td>");
                    writer.println("<td class='px-6 py-4 font-medium'>" + transfer.quantity() + "</td>");
                    writer.println("<td class='px-6 py-4 text-sm'>" + (transfer.notes() != null ? transfer.notes() : "-") + "</td>");
                    writer.println("</tr>");
                }

                writer.println("</tbody>");
                writer.println("</table>");
                writer.println("</div>");
            }

            writer.println("</div>");
            writer.println("</div>");

            writer.println("</body>");
            writer.println("</html>");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading stock transfer page: " + e.getMessage());
        }
    }

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
            String quantityStr = req.getParameter("quantity");
            String notes = req.getParameter("notes");

            // Enhanced input validation
            if (itemCode == null || itemCode.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=missing_item_code");
                return;
            }

            if (quantityStr == null || quantityStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=missing_quantity");
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr.trim());
                if (quantity <= 0) {
                    resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=invalid_quantity_must_be_positive");
                    return;
                }
                if (quantity > 9999) {
                    resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=quantity_too_large");
                    return;
                }
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=invalid_quantity_format");
                return;
            }

            // Validate and sanitize inputs
            String sanitizedItemCode = itemCode.trim().toUpperCase();
            String sanitizedNotes = notes != null ? notes.trim() : null;
            if (sanitizedNotes != null && sanitizedNotes.length() > 255) {
                sanitizedNotes = sanitizedNotes.substring(0, 255); // Truncate if too long
            }

            ManagerService managerService = new ManagerServiceImpl(new JdbcManagerRepository());
            managerService.transferStockShelfToWeb(sanitizedItemCode, quantity, managerId, sanitizedNotes);

            resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?success=transfer_completed&item=" +
                             sanitizedItemCode + "&qty=" + quantity);

        } catch (domain.exception.StockTransferException e) {
            // Handle specific stock transfer exceptions
            String errorMessage = e.getMessage().toLowerCase().replace(" ", "_").replace(":", "");
            if (e.getMessage().contains("insufficient stock")) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=insufficient_shelf_stock");
            } else if (e.getMessage().contains("not found")) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=item_not_found");
            } else if (e.getMessage().contains("inactive")) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=item_inactive");
            } else if (e.getMessage().contains("invalid quantity")) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=invalid_quantity");
            } else if (e.getMessage().contains("concurrent")) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=concurrent_modification");
            } else {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=transfer_validation_failed");
            }

            // Log the specific error for debugging
            java.util.logging.Logger.getLogger(this.getClass().getName())
                .warning("Stock transfer validation failed: " + e.getMessage());

        } catch (IllegalArgumentException e) {
            // Handle validation errors from service layer
            if (e.getMessage().contains("empty")) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=missing_item_code");
            } else if (e.getMessage().contains("positive")) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=invalid_quantity_must_be_positive");
            } else if (e.getMessage().contains("not found")) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=item_not_found");
            } else if (e.getMessage().contains("inactive")) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=item_inactive");
            } else {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=validation_failed");
            }

            java.util.logging.Logger.getLogger(this.getClass().getName())
                .warning("Stock transfer argument validation failed: " + e.getMessage());

        } catch (RuntimeException e) {
            // Handle database and other runtime errors
            if (e.getMessage().contains("Database")) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=database_error");
            } else if (e.getMessage().contains("connection")) {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=connection_error");
            } else {
                resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=system_error");
            }

            java.util.logging.Logger.getLogger(this.getClass().getName())
                .severe("Stock transfer runtime error: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            // Catch-all for any unexpected exceptions
            resp.sendRedirect(req.getContextPath() + "/manager/stock/transfer?error=unexpected_error");

            java.util.logging.Logger.getLogger(this.getClass().getName())
                .severe("Unexpected error during stock transfer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
