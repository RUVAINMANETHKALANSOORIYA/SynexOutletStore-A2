package presentation.web;

import application.DiscountServiceImpl;
import domain.manager.Discount;
import domain.manager.Discount.DiscountType;
import infrastructure.jdbc.JdbcDiscountRepository;
import ports.in.DiscountService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DiscountManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if user is manager
        HttpSession session = request.getSession(false);
        if (session == null || !"MANAGER".equals(session.getAttribute("staffRole"))) {
            response.sendRedirect(request.getContextPath() + "/staff-login.html");
            return;
        }

        String action = request.getParameter("action");

        try {
            DiscountService discountService = new DiscountServiceImpl(new JdbcDiscountRepository());

            if ("view".equals(action)) {
                int discountId = Integer.parseInt(request.getParameter("discountId"));
                Discount discount = discountService.getDiscountById(discountId);
                generateDiscountDetailsHTML(request, response, discount);
            } else {
                // Default: show all discounts
                List<Discount> discounts = discountService.getAllDiscounts();
                int totalActive = discountService.getTotalActiveDiscounts();
                BigDecimal totalSaved = discountService.getTotalDiscountsSaved();

                generateDiscountManagementHTML(request, response, discounts, totalActive, totalSaved);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/manager-dashboard.html?error=load_discounts");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if user is manager
        HttpSession session = request.getSession(false);
        if (session == null || !"MANAGER".equals(session.getAttribute("staffRole"))) {
            response.sendRedirect(request.getContextPath() + "/staff-login.html");
            return;
        }

        String action = request.getParameter("action");
        Long userId = (Long) session.getAttribute("staffUserId");

        try {
            DiscountService discountService = new DiscountServiceImpl(new JdbcDiscountRepository());

            switch (action) {
                case "create":
                    createDiscount(request, discountService, userId);
                    response.sendRedirect(request.getContextPath() + "/manager/discounts?success=create");
                    break;
                case "update":
                    updateDiscount(request, discountService);
                    response.sendRedirect(request.getContextPath() + "/manager/discounts?success=update");
                    break;
                case "delete":
                    int deleteId = Integer.parseInt(request.getParameter("discountId"));
                    discountService.deleteDiscount(deleteId);
                    response.sendRedirect(request.getContextPath() + "/manager/discounts?success=delete");
                    break;
                case "toggle":
                    int toggleId = Integer.parseInt(request.getParameter("discountId"));
                    discountService.toggleDiscountStatus(toggleId);
                    response.sendRedirect(request.getContextPath() + "/manager/discounts?success=toggle");
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/manager/discounts");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/manager/discounts?error=" + action + "&message=" + e.getMessage());
        }
    }

    private void createDiscount(HttpServletRequest request, DiscountService service, Long userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        String discountCode = request.getParameter("discountCode").trim().toUpperCase();
        String description = request.getParameter("description").trim();
        DiscountType discountType = DiscountType.valueOf(request.getParameter("discountType"));
        BigDecimal discountValue = new BigDecimal(request.getParameter("discountValue"));
        BigDecimal minPurchaseAmount = new BigDecimal(request.getParameter("minPurchaseAmount"));

        BigDecimal maxDiscountAmount = null;
        String maxDiscountStr = request.getParameter("maxDiscountAmount");
        if (maxDiscountStr != null && !maxDiscountStr.trim().isEmpty()) {
            maxDiscountAmount = new BigDecimal(maxDiscountStr);
        }

        LocalDateTime startDate = LocalDateTime.parse(request.getParameter("startDate"), formatter);
        LocalDateTime endDate = LocalDateTime.parse(request.getParameter("endDate"), formatter);

        Integer usageLimit = null;
        String usageLimitStr = request.getParameter("usageLimit");
        if (usageLimitStr != null && !usageLimitStr.trim().isEmpty()) {
            usageLimit = Integer.parseInt(usageLimitStr);
        }

        Discount discount = new Discount(
            discountCode, description, discountType, discountValue,
            minPurchaseAmount, maxDiscountAmount, startDate, endDate,
            usageLimit, userId
        );

        service.createDiscount(discount);
    }

    private void updateDiscount(HttpServletRequest request, DiscountService service) {
        int discountId = Integer.parseInt(request.getParameter("discountId"));
        Discount existingDiscount = service.getDiscountById(discountId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        String discountCode = request.getParameter("discountCode").trim().toUpperCase();
        String description = request.getParameter("description").trim();
        DiscountType discountType = DiscountType.valueOf(request.getParameter("discountType"));
        BigDecimal discountValue = new BigDecimal(request.getParameter("discountValue"));
        BigDecimal minPurchaseAmount = new BigDecimal(request.getParameter("minPurchaseAmount"));

        BigDecimal maxDiscountAmount = null;
        String maxDiscountStr = request.getParameter("maxDiscountAmount");
        if (maxDiscountStr != null && !maxDiscountStr.trim().isEmpty()) {
            maxDiscountAmount = new BigDecimal(maxDiscountStr);
        }

        LocalDateTime startDate = LocalDateTime.parse(request.getParameter("startDate"), formatter);
        LocalDateTime endDate = LocalDateTime.parse(request.getParameter("endDate"), formatter);

        Integer usageLimit = null;
        String usageLimitStr = request.getParameter("usageLimit");
        if (usageLimitStr != null && !usageLimitStr.trim().isEmpty()) {
            usageLimit = Integer.parseInt(usageLimitStr);
        }

        boolean isActive = Boolean.parseBoolean(request.getParameter("isActive"));

        Discount updatedDiscount = new Discount(
            discountId, discountCode, description, discountType, discountValue,
            minPurchaseAmount, maxDiscountAmount, startDate, endDate,
            isActive, usageLimit, existingDiscount.timesUsed(),
            existingDiscount.createdBy(), existingDiscount.createdAt(), existingDiscount.updatedAt(),
            existingDiscount.itemCode(), existingDiscount.category()
        );

        service.updateDiscount(updatedDiscount);
    }

    private void generateDiscountManagementHTML(HttpServletRequest request, HttpServletResponse response,
                                              List<Discount> discounts, int totalActive, BigDecimal totalSaved)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        var writer = response.getWriter();

        writer.println("<!DOCTYPE html>");
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<meta charset='utf-8'>");
        writer.println("<title>Discount Management - SYOS</title>");
        writer.println("<script src='https://cdn.tailwindcss.com'></script>");
        writer.println("</head>");
        writer.println("<body class='bg-gray-50 min-h-screen'>");

        // Header
        writer.println("<header class='bg-white shadow-sm border-b'>");
        writer.println("<div class='container mx-auto px-4 py-4 flex justify-between items-center'>");
        writer.println("<h1 class='text-2xl font-bold text-gray-900'>🎯 Discount Management</h1>");
        writer.println("<div class='space-x-4'>");
        writer.println("<a href='" + request.getContextPath() + "/manager-dashboard.html' class='text-blue-600 hover:underline'>← Dashboard</a>");
        writer.println("<a href='" + request.getContextPath() + "/staff/logout' class='text-red-600 hover:underline'>Logout</a>");
        writer.println("</div>");
        writer.println("</div>");
        writer.println("</header>");

        writer.println("<div class='container mx-auto px-4 py-8 max-w-7xl'>");

        // Success/Error Messages
        String success = request.getParameter("success");
        String error = request.getParameter("error");
        String message = request.getParameter("message");

        if (success != null) {
            writer.println("<div class='mb-6 p-4 bg-green-50 border-l-4 border-green-500 rounded-lg'>");
            writer.println("<p class='text-green-800 font-medium'>✅ " + getSuccessMessage(success) + "</p>");
            writer.println("</div>");
        }

        if (error != null) {
            writer.println("<div class='mb-6 p-4 bg-red-50 border-l-4 border-red-500 rounded-lg'>");
            writer.println("<p class='text-red-800 font-medium'>❌ " + getErrorMessage(error) + (message != null ? ": " + message : "") + "</p>");
            writer.println("</div>");
        }

        // Statistics Cards
        writer.println("<div class='grid grid-cols-1 md:grid-cols-3 gap-6 mb-8'>");

        writer.println("<div class='bg-white rounded-lg shadow-sm p-6'>");
        writer.println("<div class='flex items-center'>");
        writer.println("<div class='w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mr-4'>");
        writer.println("<span class='text-2xl'>🎯</span>");
        writer.println("</div>");
        writer.println("<div>");
        writer.println("<h3 class='text-sm font-medium text-gray-500'>Active Discounts</h3>");
        writer.println("<p class='text-2xl font-bold text-gray-900'>" + totalActive + "</p>");
        writer.println("</div>");
        writer.println("</div>");
        writer.println("</div>");

        writer.println("<div class='bg-white rounded-lg shadow-sm p-6'>");
        writer.println("<div class='flex items-center'>");
        writer.println("<div class='w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mr-4'>");
        writer.println("<span class='text-2xl'>💰</span>");
        writer.println("</div>");
        writer.println("<div>");
        writer.println("<h3 class='text-sm font-medium text-gray-500'>Total Saved</h3>");
        writer.println("<p class='text-2xl font-bold text-gray-900'>LKR " + String.format("%.2f", totalSaved.doubleValue()) + "</p>");
        writer.println("</div>");
        writer.println("</div>");
        writer.println("</div>");

        writer.println("<div class='bg-white rounded-lg shadow-sm p-6'>");
        writer.println("<div class='flex items-center'>");
        writer.println("<div class='w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center mr-4'>");
        writer.println("<span class='text-2xl'>📊</span>");
        writer.println("</div>");
        writer.println("<div>");
        writer.println("<h3 class='text-sm font-medium text-gray-500'>Total Discounts</h3>");
        writer.println("<p class='text-2xl font-bold text-gray-900'>" + discounts.size() + "</p>");
        writer.println("</div>");
        writer.println("</div>");
        writer.println("</div>");

        writer.println("</div>");

        // Create New Discount Button
        writer.println("<div class='mb-6'>");
        writer.println("<button onclick='showCreateModal()' class='bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg font-semibold shadow-sm'>");
        writer.println("➕ Create New Discount");
        writer.println("</button>");
        writer.println("</div>");

        // Discounts Table
        writer.println("<div class='bg-white rounded-lg shadow-sm overflow-hidden'>");
        writer.println("<div class='px-6 py-4 bg-gray-50 border-b'>");
        writer.println("<h3 class='text-lg font-semibold text-gray-900'>All Discounts</h3>");
        writer.println("</div>");

        if (discounts.isEmpty()) {
            writer.println("<div class='p-8 text-center text-gray-500'>");
            writer.println("No discounts created yet. Create your first discount to get started!");
            writer.println("</div>");
        } else {
            writer.println("<div class='overflow-x-auto'>");
            writer.println("<table class='w-full text-sm'>");
            writer.println("<thead class='bg-gray-50'>");
            writer.println("<tr class='text-left text-xs font-medium text-gray-500 uppercase tracking-wider'>");
            writer.println("<th class='px-6 py-3'>Code</th>");
            writer.println("<th class='px-6 py-3'>Description</th>");
            writer.println("<th class='px-6 py-3'>Type</th>");
            writer.println("<th class='px-6 py-3'>Value</th>");
            writer.println("<th class='px-6 py-3'>Min Purchase</th>");
            writer.println("<th class='px-6 py-3'>Usage</th>");
            writer.println("<th class='px-6 py-3'>Period</th>");
            writer.println("<th class='px-6 py-3'>Status</th>");
            writer.println("<th class='px-6 py-3'>Actions</th>");
            writer.println("</tr>");
            writer.println("</thead>");
            writer.println("<tbody class='divide-y divide-gray-200'>");

            for (Discount discount : discounts) {
                writer.println("<tr class='hover:bg-gray-50'>");

                // Code
                writer.println("<td class='px-6 py-4 font-mono font-semibold'>" + discount.discountCode() + "</td>");

                // Description
                writer.println("<td class='px-6 py-4'>" + discount.description() + "</td>");

                // Type & Value
                writer.println("<td class='px-6 py-4'>");
                if (discount.discountType() == DiscountType.PERCENTAGE) {
                    writer.println("<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800'>Percentage</span>");
                } else {
                    writer.println("<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800'>Fixed Amount</span>");
                }
                writer.println("</td>");

                // Value
                writer.println("<td class='px-6 py-4 font-semibold'>");
                if (discount.discountType() == DiscountType.PERCENTAGE) {
                    writer.println(discount.discountValue() + "%");
                } else {
                    writer.println("LKR " + String.format("%.2f", discount.discountValue().doubleValue()));
                }
                writer.println("</td>");

                // Min Purchase
                writer.println("<td class='px-6 py-4'>LKR " + String.format("%.2f", discount.minPurchaseAmount().doubleValue()) + "</td>");

                // Usage
                writer.println("<td class='px-6 py-4'>" + discount.getUsageStatus() + "</td>");

                // Period
                writer.println("<td class='px-6 py-4'>");
                writer.println("<div class='text-xs'>");
                writer.println("<div>From: " + discount.startDate().toLocalDate() + "</div>");
                writer.println("<div>To: " + discount.endDate().toLocalDate() + "</div>");
                writer.println("</div>");
                writer.println("</td>");

                // Status
                writer.println("<td class='px-6 py-4'>");
                if (discount.isValid()) {
                    writer.println("<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800'>✅ Active</span>");
                } else if (!discount.isActive()) {
                    writer.println("<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800'>❌ Disabled</span>");
                } else if (LocalDateTime.now().isBefore(discount.startDate())) {
                    writer.println("<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-yellow-100 text-yellow-800'>⏳ Pending</span>");
                } else if (LocalDateTime.now().isAfter(discount.endDate())) {
                    writer.println("<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-gray-100 text-gray-800'>⏰ Expired</span>");
                } else {
                    writer.println("<span class='inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800'>❌ Limit Reached</span>");
                }
                writer.println("</td>");

                // Actions
                writer.println("<td class='px-6 py-4'>");
                writer.println("<div class='flex space-x-2'>");
                writer.println("<button onclick='editDiscount(" + discount.discountId() + ")' class='text-blue-600 hover:text-blue-900 text-sm font-medium'>Edit</button>");

                String toggleText = discount.isActive() ? "Disable" : "Enable";
                String toggleColor = discount.isActive() ? "text-orange-600 hover:text-orange-900" : "text-green-600 hover:text-green-900";
                writer.println("<button onclick='toggleDiscount(" + discount.discountId() + ")' class='" + toggleColor + " text-sm font-medium'>" + toggleText + "</button>");

                writer.println("<button onclick='deleteDiscount(" + discount.discountId() + ")' class='text-red-600 hover:text-red-900 text-sm font-medium'>Delete</button>");
                writer.println("</div>");
                writer.println("</td>");

                writer.println("</tr>");
            }

            writer.println("</tbody>");
            writer.println("</table>");
            writer.println("</div>");
        }

        writer.println("</div>");
        writer.println("</div>");

        // Add Create/Edit Modal and JavaScript
        generateDiscountModal(writer, request.getContextPath());

        writer.println("</body>");
        writer.println("</html>");
    }

    private void generateDiscountDetailsHTML(HttpServletRequest request, HttpServletResponse response, Discount discount)
            throws IOException {
        // Implementation for viewing individual discount details
        // This would show a detailed view of a single discount
        response.sendRedirect(request.getContextPath() + "/manager/discounts");
    }

    private void generateDiscountModal(java.io.PrintWriter writer, String contextPath) {
        // Create/Edit Discount Modal
        writer.println("<div id='discountModal' class='fixed inset-0 bg-black bg-opacity-50 hidden z-50'>");
        writer.println("<div class='flex items-center justify-center min-h-screen p-4'>");
        writer.println("<div class='bg-white rounded-lg max-w-2xl w-full max-h-screen overflow-y-auto'>");
        writer.println("<div class='p-6'>");
        writer.println("<div class='flex justify-between items-center mb-6'>");
        writer.println("<h2 id='modalTitle' class='text-xl font-bold text-gray-900'>Create New Discount</h2>");
        writer.println("<button onclick='closeModal()' class='text-gray-400 hover:text-gray-600'>");
        writer.println("<svg class='w-6 h-6' fill='none' stroke='currentColor' viewBox='0 0 24 24'>");
        writer.println("<path stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M6 18L18 6M6 6l12 12'></path>");
        writer.println("</svg>");
        writer.println("</button>");
        writer.println("</div>");

        writer.println("<form id='discountForm' method='post'>");
        writer.println("<input type='hidden' id='action' name='action' value='create'>");
        writer.println("<input type='hidden' id='discountId' name='discountId'>");

        writer.println("<div class='grid grid-cols-1 md:grid-cols-2 gap-6'>");

        // Discount Code
        writer.println("<div>");
        writer.println("<label class='block text-sm font-medium text-gray-700 mb-2'>Discount Code *</label>");
        writer.println("<input type='text' id='discountCode' name='discountCode' required maxlength='50'");
        writer.println("class='w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 uppercase'");
        writer.println("placeholder='e.g., SAVE20'>");
        writer.println("</div>");

        // Description
        writer.println("<div>");
        writer.println("<label class='block text-sm font-medium text-gray-700 mb-2'>Description *</label>");
        writer.println("<input type='text' id='description' name='description' required maxlength='255'");
        writer.println("class='w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500'");
        writer.println("placeholder='e.g., 20% off for new customers'>");
        writer.println("</div>");

        writer.println("</div>");

        writer.println("<div class='grid grid-cols-1 md:grid-cols-2 gap-6 mt-6'>");

        // Discount Type
        writer.println("<div>");
        writer.println("<label class='block text-sm font-medium text-gray-700 mb-2'>Discount Type *</label>");
        writer.println("<select id='discountType' name='discountType' required onchange='updateDiscountValueLabel()'");
        writer.println("class='w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500'>");
        writer.println("<option value='PERCENTAGE'>Percentage (%)</option>");
        writer.println("<option value='FIXED_AMOUNT'>Fixed Amount (LKR)</option>");
        writer.println("</select>");
        writer.println("</div>");

        // Discount Value
        writer.println("<div>");
        writer.println("<label class='block text-sm font-medium text-gray-700 mb-2'>Discount Value *</label>");
        writer.println("<input type='number' id='discountValue' name='discountValue' required min='0' step='0.01'");
        writer.println("class='w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500'");
        writer.println("placeholder='e.g., 20 or 100.00'>");
        writer.println("<p id='discountValueHelp' class='text-xs text-gray-500 mt-1'>Percentage (0-100) or fixed amount</p>");
        writer.println("</div>");

        writer.println("</div>");

        writer.println("<div class='grid grid-cols-1 md:grid-cols-2 gap-6 mt-6'>");

        // Min Purchase Amount
        writer.println("<div>");
        writer.println("<label class='block text-sm font-medium text-gray-700 mb-2'>Min Purchase Amount (LKR) *</label>");
        writer.println("<input type='number' id='minPurchaseAmount' name='minPurchaseAmount' required min='0' step='0.01' value='0'");
        writer.println("class='w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500'>");
        writer.println("</div>");

        // Max Discount Amount
        writer.println("<div>");
        writer.println("<label class='block text-sm font-medium text-gray-700 mb-2'>Max Discount Amount (LKR)</label>");
        writer.println("<input type='number' id='maxDiscountAmount' name='maxDiscountAmount' min='0' step='0.01'");
        writer.println("class='w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500'");
        writer.println("placeholder='Optional - Leave empty for no limit'>");
        writer.println("</div>");

        writer.println("</div>");

        writer.println("<div class='grid grid-cols-1 md:grid-cols-2 gap-6 mt-6'>");

        // Start Date
        writer.println("<div>");
        writer.println("<label class='block text-sm font-medium text-gray-700 mb-2'>Start Date *</label>");
        writer.println("<input type='datetime-local' id='startDate' name='startDate' required");
        writer.println("class='w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500'>");
        writer.println("</div>");

        // End Date
        writer.println("<div>");
        writer.println("<label class='block text-sm font-medium text-gray-700 mb-2'>End Date *</label>");
        writer.println("<input type='datetime-local' id='endDate' name='endDate' required");
        writer.println("class='w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500'>");
        writer.println("</div>");

        writer.println("</div>");

        writer.println("<div class='mt-6'>");
        writer.println("<label class='block text-sm font-medium text-gray-700 mb-2'>Usage Limit</label>");
        writer.println("<input type='number' id='usageLimit' name='usageLimit' min='1'");
        writer.println("class='w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500'");
        writer.println("placeholder='Optional - Leave empty for unlimited usage'>");
        writer.println("</div>");

        writer.println("<div class='mt-6 flex justify-end space-x-4'>");
        writer.println("<button type='button' onclick='closeModal()' class='px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50'>Cancel</button>");
        writer.println("<button type='submit' class='px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700'>Save Discount</button>");
        writer.println("</div>");

        writer.println("</form>");
        writer.println("</div>");
        writer.println("</div>");
        writer.println("</div>");
        writer.println("</div>");

        // JavaScript for modal functionality
        writer.println("<script>");
        writer.println("function showCreateModal() {");
        writer.println("  document.getElementById('modalTitle').textContent = 'Create New Discount';");
        writer.println("  document.getElementById('action').value = 'create';");
        writer.println("  document.getElementById('discountForm').reset();");
        writer.println("  document.getElementById('discountModal').classList.remove('hidden');");
        writer.println("  setDefaultDates();");
        writer.println("}");

        writer.println("function editDiscount(discountId) {");
        writer.println("  // This would populate the form with existing discount data");
        writer.println("  // For now, redirect to a separate edit page or implement AJAX loading");
        writer.println("  alert('Edit functionality would load discount data into the modal');");
        writer.println("}");

        writer.println("function toggleDiscount(discountId) {");
        writer.println("  if (confirm('Are you sure you want to toggle this discount status?')) {");
        writer.println("    var form = document.createElement('form');");
        writer.println("    form.method = 'post';");
        writer.println("    form.innerHTML = '<input name=\"action\" value=\"toggle\"><input name=\"discountId\" value=\"' + discountId + '\">';");
        writer.println("    document.body.appendChild(form);");
        writer.println("    form.submit();");
        writer.println("  }");
        writer.println("}");

        writer.println("function deleteDiscount(discountId) {");
        writer.println("  if (confirm('Are you sure you want to delete this discount? This action cannot be undone.')) {");
        writer.println("    var form = document.createElement('form');");
        writer.println("    form.method = 'post';");
        writer.println("    form.innerHTML = '<input name=\"action\" value=\"delete\"><input name=\"discountId\" value=\"' + discountId + '\">';");
        writer.println("    document.body.appendChild(form);");
        writer.println("    form.submit();");
        writer.println("  }");
        writer.println("}");

        writer.println("function closeModal() {");
        writer.println("  document.getElementById('discountModal').classList.add('hidden');");
        writer.println("}");

        writer.println("function updateDiscountValueLabel() {");
        writer.println("  var type = document.getElementById('discountType').value;");
        writer.println("  var help = document.getElementById('discountValueHelp');");
        writer.println("  if (type === 'PERCENTAGE') {");
        writer.println("    help.textContent = 'Enter percentage (0-100)';");
        writer.println("    document.getElementById('discountValue').setAttribute('max', '100');");
        writer.println("  } else {");
        writer.println("    help.textContent = 'Enter fixed amount in LKR';");
        writer.println("    document.getElementById('discountValue').removeAttribute('max');");
        writer.println("  }");
        writer.println("}");

        writer.println("function setDefaultDates() {");
        writer.println("  var now = new Date();");
        writer.println("  var start = new Date(now.getTime());");
        writer.println("  var end = new Date(now.getTime() + (30 * 24 * 60 * 60 * 1000)); // 30 days from now");
        writer.println("  ");
        writer.println("  document.getElementById('startDate').value = formatDateForInput(start);");
        writer.println("  document.getElementById('endDate').value = formatDateForInput(end);");
        writer.println("}");

        writer.println("function formatDateForInput(date) {");
        writer.println("  var year = date.getFullYear();");
        writer.println("  var month = String(date.getMonth() + 1).padStart(2, '0');");
        writer.println("  var day = String(date.getDate()).padStart(2, '0');");
        writer.println("  var hours = String(date.getHours()).padStart(2, '0');");
        writer.println("  var minutes = String(date.getMinutes()).padStart(2, '0');");
        writer.println("  return year + '-' + month + '-' + day + 'T' + hours + ':' + minutes;");
        writer.println("}");

        writer.println("// Set form action dynamically");
        writer.println("document.getElementById('discountForm').action = '" + contextPath + "/manager/discounts';");
        writer.println("</script>");
    }

    private String getSuccessMessage(String action) {
        return switch (action) {
            case "create" -> "Discount created successfully!";
            case "update" -> "Discount updated successfully!";
            case "delete" -> "Discount deleted successfully!";
            case "toggle" -> "Discount status updated successfully!";
            default -> "Operation completed successfully!";
        };
    }

    private String getErrorMessage(String action) {
        return switch (action) {
            case "create" -> "Failed to create discount";
            case "update" -> "Failed to update discount";
            case "delete" -> "Failed to delete discount";
            case "toggle" -> "Failed to update discount status";
            default -> "Operation failed";
        };
    }
}
