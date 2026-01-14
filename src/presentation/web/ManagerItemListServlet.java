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

public class ManagerItemListServlet extends HttpServlet {

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
            var items = managerService.getAllItems();
            var lowStockAlerts = managerService.getLowStockAlerts();

            resp.setContentType("text/html;charset=UTF-8");
            var writer = resp.getWriter();

            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<meta charset='utf-8'>");
            writer.println("<title>Item Management - SYOS</title>");
            writer.println("<script src='https://cdn.tailwindcss.com'></script>");
            writer.println("</head>");
            writer.println("<body class='bg-gray-50 min-h-screen'>");

            // Header
            writer.println("<header class='bg-white shadow-sm border-b'>");
            writer.println("<div class='container mx-auto px-4 py-4 flex justify-between items-center'>");
            writer.println("<h1 class='text-2xl font-bold text-gray-900'>📦 Item Management</h1>");
            writer.println("<div class='space-x-4'>");
            writer.println("<a href='" + req.getContextPath() + "/manager/dashboard' class='text-blue-600 hover:underline'>← Dashboard</a>");
            writer.println("<a href='" + req.getContextPath() + "/logout' class='text-red-600 hover:underline'>Logout</a>");
            writer.println("</div>");
            writer.println("</div>");
            writer.println("</header>");

            writer.println("<div class='container mx-auto px-4 py-8 max-w-7xl'>");

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

            // Add New Item Form
            writer.println("<div class='bg-white rounded-lg shadow-sm p-6 mb-8'>");
            writer.println("<h3 class='text-xl font-semibold text-gray-900 mb-4'>➕ Add New Item</h3>");
            writer.println("<form method='post' action='" + req.getContextPath() + "/manager/items/create' class='grid grid-cols-1 md:grid-cols-4 gap-4'>");
            writer.println("<input type='text' name='itemCode' placeholder='Item Code (e.g., ITM013)' required class='px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none'>");
            writer.println("<input type='text' name='name' placeholder='Item Name' required class='px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none'>");
            writer.println("<input type='number' name='price' step='0.01' min='0.01' placeholder='Price' required class='px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none'>");
            writer.println("<button type='submit' class='bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 font-medium'>Create Item</button>");
            writer.println("</form>");
            writer.println("</div>");

            // Items Table
            writer.println("<div class='bg-white rounded-lg shadow-sm overflow-hidden'>");
            writer.println("<div class='px-6 py-4 bg-gray-50 border-b'>");
            writer.println("<h3 class='text-xl font-semibold text-gray-900'>📋 All Items</h3>");
            writer.println("</div>");

            if (items.isEmpty()) {
                writer.println("<div class='p-8 text-center text-gray-500'>");
                writer.println("No items found. Create your first item above.");
                writer.println("</div>");
            } else {
                writer.println("<div class='overflow-x-auto'>");
                writer.println("<table class='w-full'>");
                writer.println("<thead class='bg-gray-50'>");
                writer.println("<tr class='text-left text-sm font-medium text-gray-500 uppercase tracking-wider'>");
                writer.println("<th class='px-6 py-3'>Item Code</th>");
                writer.println("<th class='px-6 py-3'>Name</th>");
                writer.println("<th class='px-6 py-3'>Price</th>");
                writer.println("<th class='px-6 py-3'>Status</th>");
                writer.println("<th class='px-6 py-3'>Actions</th>");
                writer.println("</tr>");
                writer.println("</thead>");
                writer.println("<tbody class='divide-y divide-gray-200'>");

                for (var item : items) {
                    String statusBadge = item.isActive() ?
                        "<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800'>Active</span>" :
                        "<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800'>Inactive</span>";

                    writer.println("<tr class='hover:bg-gray-50'>");
                    writer.println("<td class='px-6 py-4 font-mono text-sm'>" + item.code() + "</td>");
                    writer.println("<td class='px-6 py-4 font-medium'>" + item.name() + "</td>");
                    writer.println("<td class='px-6 py-4'>LKR " + String.format("%.2f", item.price().doubleValue()) + "</td>");
                    writer.println("<td class='px-6 py-4'>" + statusBadge + "</td>");
                    writer.println("<td class='px-6 py-4 space-x-2'>");

                    // Edit button
                    writer.println("<button onclick=\"editItem('" + item.code() + "', '" + item.name() + "', " + item.price().doubleValue() + ", " + item.isActive() + ")\" class='text-blue-600 hover:text-blue-900 text-sm font-medium'>Edit</button>");

                    if (item.isActive()) {
                        // Deactivate button
                        writer.println("<form method='post' action='" + req.getContextPath() + "/manager/items/deactivate' class='inline'>");
                        writer.println("<input type='hidden' name='itemCode' value='" + item.code() + "'>");
                        writer.println("<button type='submit' onclick='return confirm(\"Deactivate " + item.name() + "?\")' class='text-red-600 hover:text-red-900 text-sm font-medium'>Deactivate</button>");
                        writer.println("</form>");
                    }

                    writer.println("</td>");
                    writer.println("</tr>");
                }

                writer.println("</tbody>");
                writer.println("</table>");
                writer.println("</div>");
            }

            writer.println("</div>");
            writer.println("</div>");

            // Edit Modal
            writer.println("<div id='editModal' class='fixed inset-0 bg-black bg-opacity-50 hidden items-center justify-center z-50'>");
            writer.println("<div class='bg-white rounded-lg p-6 w-96 mx-4'>");
            writer.println("<h3 class='text-lg font-semibold mb-4'>Edit Item</h3>");
            writer.println("<form method='post' action='" + req.getContextPath() + "/manager/items/update' class='space-y-4'>");
            writer.println("<input type='hidden' name='itemCode' id='editItemCode'>");
            writer.println("<div>");
            writer.println("<label class='block text-sm font-medium text-gray-700 mb-1'>Name</label>");
            writer.println("<input type='text' name='name' id='editName' required class='w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none'>");
            writer.println("</div>");
            writer.println("<div>");
            writer.println("<label class='block text-sm font-medium text-gray-700 mb-1'>Price</label>");
            writer.println("<input type='number' name='price' id='editPrice' step='0.01' min='0.01' required class='w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none'>");
            writer.println("</div>");
            writer.println("<div>");
            writer.println("<label class='flex items-center space-x-2'>");
            writer.println("<input type='checkbox' name='isActive' id='editIsActive' class='rounded'>");
            writer.println("<span class='text-sm font-medium text-gray-700'>Active</span>");
            writer.println("</label>");
            writer.println("</div>");
            writer.println("<div class='flex space-x-3 pt-4'>");
            writer.println("<button type='submit' class='flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 font-medium'>Update</button>");
            writer.println("<button type='button' onclick='closeEditModal()' class='flex-1 bg-gray-300 text-gray-700 py-2 rounded-lg hover:bg-gray-400 font-medium'>Cancel</button>");
            writer.println("</div>");
            writer.println("</form>");
            writer.println("</div>");
            writer.println("</div>");

            // JavaScript
            writer.println("<script>");
            writer.println("function editItem(code, name, price, isActive) {");
            writer.println("  document.getElementById('editItemCode').value = code;");
            writer.println("  document.getElementById('editName').value = name;");
            writer.println("  document.getElementById('editPrice').value = price;");
            writer.println("  document.getElementById('editIsActive').checked = isActive;");
            writer.println("  document.getElementById('editModal').classList.remove('hidden');");
            writer.println("  document.getElementById('editModal').classList.add('flex');");
            writer.println("}");
            writer.println("function closeEditModal() {");
            writer.println("  document.getElementById('editModal').classList.add('hidden');");
            writer.println("  document.getElementById('editModal').classList.remove('flex');");
            writer.println("}");
            writer.println("</script>");

            writer.println("</body>");
            writer.println("</html>");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading items: " + e.getMessage());
        }
    }
}
