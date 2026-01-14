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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ManagerReportsServlet extends HttpServlet {

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

        String reportType = req.getParameter("type");
        String dateParam = req.getParameter("date");
        String startDateParam = req.getParameter("startDate");
        String endDateParam = req.getParameter("endDate");

        try {
            ManagerService managerService = new ManagerServiceImpl(new JdbcManagerRepository());

            resp.setContentType("text/html;charset=UTF-8");
            var writer = resp.getWriter();

            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<meta charset='utf-8'>");
            writer.println("<title>Manager Reports - SYOS</title>");
            writer.println("<script src='https://cdn.tailwindcss.com'></script>");
            writer.println("</head>");
            writer.println("<body class='bg-gray-50 min-h-screen'>");

            // Header
            writer.println("<header class='bg-white shadow-sm border-b'>");
            writer.println("<div class='container mx-auto px-4 py-4 flex justify-between items-center'>");
            writer.println("<h1 class='text-2xl font-bold text-gray-900'>📊 Manager Reports</h1>");
            writer.println("<div class='space-x-4'>");
            writer.println("<a href='" + req.getContextPath() + "/manager/dashboard' class='text-blue-600 hover:underline'>← Dashboard</a>");
            writer.println("<a href='" + req.getContextPath() + "/logout' class='text-red-600 hover:underline'>Logout</a>");
            writer.println("</div>");
            writer.println("</div>");
            writer.println("</header>");

            writer.println("<div class='container mx-auto px-4 py-8 max-w-7xl'>");

            // Report Navigation
            if (reportType == null) {
                writer.println("<div class='grid md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8'>");

                // Daily Sales Report Card
                writer.println("<div class='bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow'>");
                writer.println("<div class='flex items-center mb-4'>");
                writer.println("<div class='w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mr-4'>");
                writer.println("<span class='text-2xl'>📈</span>");
                writer.println("</div>");
                writer.println("<h3 class='text-lg font-semibold text-gray-900'>Daily Sales</h3>");
                writer.println("</div>");
                writer.println("<p class='text-gray-600 text-sm mb-4'>View sales report for a specific date</p>");
                writer.println("<form method='get' class='space-y-3'>");
                writer.println("<input type='hidden' name='type' value='sales'>");
                writer.println("<input type='date' name='date' value='" + LocalDate.now() + "' required class='w-full px-3 py-2 border rounded-lg text-sm'>");
                writer.println("<button type='submit' class='w-full bg-green-600 text-white py-2 rounded-lg hover:bg-green-700 text-sm'>Generate Report</button>");
                writer.println("</form>");
                writer.println("</div>");

                // Stock Report Card
                writer.println("<div class='bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow'>");
                writer.println("<div class='flex items-center mb-4'>");
                writer.println("<div class='w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mr-4'>");
                writer.println("<span class='text-2xl'>📦</span>");
                writer.println("</div>");
                writer.println("<h3 class='text-lg font-semibold text-gray-900'>Stock Report</h3>");
                writer.println("</div>");
                writer.println("<p class='text-gray-600 text-sm mb-4'>View current stock levels by batch</p>");
                writer.println("<a href='?type=stock' class='block w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 text-sm text-center'>View Stock</a>");
                writer.println("</div>");

                // Reorder Report Card
                writer.println("<div class='bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow'>");
                writer.println("<div class='flex items-center mb-4'>");
                writer.println("<div class='w-12 h-12 bg-red-100 rounded-full flex items-center justify-center mr-4'>");
                writer.println("<span class='text-2xl'>🚨</span>");
                writer.println("</div>");
                writer.println("<h3 class='text-lg font-semibold text-gray-900'>Reorder Alert</h3>");
                writer.println("</div>");
                writer.println("<p class='text-gray-600 text-sm mb-4'>Items with low stock levels</p>");
                writer.println("<a href='?type=reorder' class='block w-full bg-red-600 text-white py-2 rounded-lg hover:bg-red-700 text-sm text-center'>View Alerts</a>");
                writer.println("</div>");

                // Bill Report Card
                writer.println("<div class='bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow'>");
                writer.println("<div class='flex items-center mb-4'>");
                writer.println("<div class='w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center mr-4'>");
                writer.println("<span class='text-2xl'>🧾</span>");
                writer.println("</div>");
                writer.println("<h3 class='text-lg font-semibold text-gray-900'>Bill Report</h3>");
                writer.println("</div>");
                writer.println("<p class='text-gray-600 text-sm mb-4'>View all transactions in date range</p>");
                writer.println("<form method='get' class='space-y-2'>");
                writer.println("<input type='hidden' name='type' value='bills'>");
                writer.println("<input type='date' name='startDate' value='" + LocalDate.now().minusDays(7) + "' required class='w-full px-3 py-2 border rounded-lg text-xs'>");
                writer.println("<input type='date' name='endDate' value='" + LocalDate.now() + "' required class='w-full px-3 py-2 border rounded-lg text-xs'>");
                writer.println("<button type='submit' class='w-full bg-purple-600 text-white py-2 rounded-lg hover:bg-purple-700 text-sm'>Generate Report</button>");
                writer.println("</form>");
                writer.println("</div>");

                writer.println("</div>");
            }

            // Generate specific reports
            if ("sales".equals(reportType)) {
                LocalDate reportDate = dateParam != null ? LocalDate.parse(dateParam) : LocalDate.now();
                var salesReports = managerService.generateDailySalesReport(reportDate);

                writer.println("<div class='bg-white rounded-lg shadow-sm overflow-hidden'>");
                writer.println("<div class='px-6 py-4 bg-gray-50 border-b'>");
                writer.println("<h3 class='text-xl font-semibold text-gray-900'>📈 Daily Sales Report - " + reportDate + "</h3>");
                writer.println("</div>");

                if (salesReports.isEmpty()) {
                    writer.println("<div class='p-8 text-center text-gray-500'>");
                    writer.println("No sales found for " + reportDate);
                    writer.println("</div>");
                } else {
                    BigDecimal totalRevenue = salesReports.stream()
                        .map(r -> r.totalRevenue())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                    writer.println("<div class='p-4 bg-green-50 border-b'>");
                    writer.println("<p class='text-green-800 font-semibold'>Total Revenue: LKR " + String.format("%.2f", totalRevenue.doubleValue()) + "</p>");
                    writer.println("</div>");

                    writer.println("<div class='overflow-x-auto'>");
                    writer.println("<table class='w-full'>");
                    writer.println("<thead class='bg-gray-50'>");
                    writer.println("<tr class='text-left text-sm font-medium text-gray-500 uppercase tracking-wider'>");
                    writer.println("<th class='px-6 py-3'>Item Code</th>");
                    writer.println("<th class='px-6 py-3'>Item Name</th>");
                    writer.println("<th class='px-6 py-3'>Quantity Sold</th>");
                    writer.println("<th class='px-6 py-3'>Unit Price</th>");
                    writer.println("<th class='px-6 py-3'>Total Revenue</th>");
                    writer.println("</tr>");
                    writer.println("</thead>");
                    writer.println("<tbody class='divide-y divide-gray-200'>");

                    for (var report : salesReports) {
                        writer.println("<tr class='hover:bg-gray-50'>");
                        writer.println("<td class='px-6 py-4 font-mono text-sm'>" + report.itemCode() + "</td>");
                        writer.println("<td class='px-6 py-4 font-medium'>" + report.itemName() + "</td>");
                        writer.println("<td class='px-6 py-4 text-center'>" + report.totalQuantity() + "</td>");
                        writer.println("<td class='px-6 py-4'>LKR " + String.format("%.2f", report.unitPrice().doubleValue()) + "</td>");
                        writer.println("<td class='px-6 py-4 font-semibold text-green-600'>LKR " + String.format("%.2f", report.totalRevenue().doubleValue()) + "</td>");
                        writer.println("</tr>");
                    }

                    writer.println("</tbody>");
                    writer.println("</table>");
                    writer.println("</div>");
                }
                writer.println("</div>");
            }

            if ("stock".equals(reportType)) {
                var stockReports = managerService.generateStockReport();

                writer.println("<div class='bg-white rounded-lg shadow-sm overflow-hidden'>");
                writer.println("<div class='px-6 py-4 bg-gray-50 border-b'>");
                writer.println("<h3 class='text-xl font-semibold text-gray-900'>📦 Stock Report</h3>");
                writer.println("</div>");

                if (stockReports.isEmpty()) {
                    writer.println("<div class='p-8 text-center text-gray-500'>");
                    writer.println("No stock data available");
                    writer.println("</div>");
                } else {
                    writer.println("<div class='overflow-x-auto'>");
                    writer.println("<table class='w-full text-sm'>");
                    writer.println("<thead class='bg-gray-50'>");
                    writer.println("<tr class='text-left text-xs font-medium text-gray-500 uppercase tracking-wider'>");
                    writer.println("<th class='px-4 py-3'>Item Code</th>");
                    writer.println("<th class='px-4 py-3'>Item Name</th>");
                    writer.println("<th class='px-4 py-3'>Batch</th>");
                    writer.println("<th class='px-4 py-3'>Purchase Date</th>");
                    writer.println("<th class='px-4 py-3'>Expiry Date</th>");
                    writer.println("<th class='px-4 py-3'>Shelf Qty</th>");
                    writer.println("<th class='px-4 py-3'>Web Qty</th>");
                    writer.println("<th class='px-4 py-3'>Total</th>");
                    writer.println("<th class='px-4 py-3'>Value</th>");
                    writer.println("</tr>");
                    writer.println("</thead>");
                    writer.println("<tbody class='divide-y divide-gray-200'>");

                    for (var report : stockReports) {
                        String expiryClass = report.expiryDate().isBefore(LocalDate.now().plusDays(7)) ? "text-red-600" : "";
                        BigDecimal totalValue = report.unitPrice().multiply(BigDecimal.valueOf(report.totalQty()));

                        writer.println("<tr class='hover:bg-gray-50'>");
                        writer.println("<td class='px-4 py-3 font-mono'>" + report.itemCode() + "</td>");
                        writer.println("<td class='px-4 py-3 font-medium'>" + report.itemName() + "</td>");
                        writer.println("<td class='px-4 py-3'>" + report.batchCode() + "</td>");
                        writer.println("<td class='px-4 py-3'>" + (report.purchaseDate() != null ? report.purchaseDate() : "-") + "</td>");
                        writer.println("<td class='px-4 py-3 " + expiryClass + "'>" + report.expiryDate() + "</td>");
                        writer.println("<td class='px-4 py-3 text-center'>" + report.shelfQty() + "</td>");
                        writer.println("<td class='px-4 py-3 text-center'>" + report.webQty() + "</td>");
                        writer.println("<td class='px-4 py-3 text-center font-semibold'>" + report.totalQty() + "</td>");
                        writer.println("<td class='px-4 py-3 font-semibold'>LKR " + String.format("%.2f", totalValue.doubleValue()) + "</td>");
                        writer.println("</tr>");
                    }

                    writer.println("</tbody>");
                    writer.println("</table>");
                    writer.println("</div>");
                }
                writer.println("</div>");
            }

            if ("reorder".equals(reportType)) {
                var reorderItems = managerService.generateReorderReport();

                writer.println("<div class='bg-white rounded-lg shadow-sm overflow-hidden'>");
                writer.println("<div class='px-6 py-4 bg-red-50 border-b'>");
                writer.println("<h3 class='text-xl font-semibold text-red-900'>🚨 Reorder Alert Report</h3>");
                writer.println("<p class='text-red-700 text-sm mt-1'>Items with stock levels ≤ 50 units</p>");
                writer.println("</div>");

                if (reorderItems.isEmpty()) {
                    writer.println("<div class='p-8 text-center text-gray-500'>");
                    writer.println("✅ All items are adequately stocked");
                    writer.println("</div>");
                } else {
                    writer.println("<div class='p-6'>");
                    writer.println("<ul class='space-y-2'>");
                    for (String item : reorderItems) {
                        writer.println("<li class='flex items-center p-3 bg-red-50 rounded-lg'>");
                        writer.println("<span class='text-red-600 mr-3'>⚠️</span>");
                        writer.println("<span class='text-red-800 font-medium'>" + item + "</span>");
                        writer.println("</li>");
                    }
                    writer.println("</ul>");
                    writer.println("</div>");
                }
                writer.println("</div>");
            }

            if ("bills".equals(reportType)) {
                LocalDate startDate = startDateParam != null ? LocalDate.parse(startDateParam) : LocalDate.now().minusDays(7);
                LocalDate endDate = endDateParam != null ? LocalDate.parse(endDateParam) : LocalDate.now();
                var billReports = managerService.generateBillReport(startDate, endDate);

                writer.println("<div class='bg-white rounded-lg shadow-sm overflow-hidden'>");
                writer.println("<div class='px-6 py-4 bg-gray-50 border-b'>");
                writer.println("<h3 class='text-xl font-semibold text-gray-900'>🧾 Bill Report</h3>");
                writer.println("<p class='text-gray-600 text-sm mt-1'>" + startDate + " to " + endDate + "</p>");
                writer.println("</div>");

                if (billReports.isEmpty()) {
                    writer.println("<div class='p-8 text-center text-gray-500'>");
                    writer.println("No transactions found in the specified date range");
                    writer.println("</div>");
                } else {
                    BigDecimal totalAmount = billReports.stream()
                        .map(r -> r.totalAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                    writer.println("<div class='p-4 bg-blue-50 border-b'>");
                    writer.println("<div class='grid grid-cols-2 gap-4'>");
                    writer.println("<p class='text-blue-800 font-semibold'>Total Orders: " + billReports.size() + "</p>");
                    writer.println("<p class='text-blue-800 font-semibold'>Total Revenue: LKR " + String.format("%.2f", totalAmount.doubleValue()) + "</p>");
                    writer.println("</div>");
                    writer.println("</div>");

                    writer.println("<div class='overflow-x-auto'>");
                    writer.println("<table class='w-full'>");
                    writer.println("<thead class='bg-gray-50'>");
                    writer.println("<tr class='text-left text-sm font-medium text-gray-500 uppercase tracking-wider'>");
                    writer.println("<th class='px-6 py-3'>Order ID</th>");
                    writer.println("<th class='px-6 py-3'>Type</th>");
                    writer.println("<th class='px-6 py-3'>Customer</th>");
                    writer.println("<th class='px-6 py-3'>Payment</th>");
                    writer.println("<th class='px-6 py-3'>Total</th>");
                    writer.println("<th class='px-6 py-3'>Date</th>");
                    writer.println("</tr>");
                    writer.println("</thead>");
                    writer.println("<tbody class='divide-y divide-gray-200'>");

                    for (var report : billReports) {
                        String typeBadge = "PHYSICAL".equals(report.orderType()) ?
                            "<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-orange-100 text-orange-800'>POS</span>" :
                            "<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800'>Online</span>";

                        String paymentBadge = "CASH".equals(report.paymentMethod()) ?
                            "<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800'>Cash</span>" :
                            "<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-purple-100 text-purple-800'>Card</span>";

                        writer.println("<tr class='hover:bg-gray-50'>");
                        writer.println("<td class='px-6 py-4 font-mono text-sm'>#" + report.orderId() + "</td>");
                        writer.println("<td class='px-6 py-4'>" + typeBadge + "</td>");
                        writer.println("<td class='px-6 py-4'>" + report.customerInfo() + "</td>");
                        writer.println("<td class='px-6 py-4'>" + paymentBadge + "</td>");
                        writer.println("<td class='px-6 py-4 font-semibold'>LKR " + String.format("%.2f", report.totalAmount().doubleValue()) + "</td>");
                        writer.println("<td class='px-6 py-4 text-sm'>" + report.createdAt().toLocalDate() + " " + report.createdAt().toLocalTime() + "</td>");
                        writer.println("</tr>");
                    }

                    writer.println("</tbody>");
                    writer.println("</table>");
                    writer.println("</div>");
                }
                writer.println("</div>");
            }

            writer.println("</div>");

            writer.println("</body>");
            writer.println("</html>");

        } catch (DateTimeParseException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date format");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating report: " + e.getMessage());
        }
    }
}
