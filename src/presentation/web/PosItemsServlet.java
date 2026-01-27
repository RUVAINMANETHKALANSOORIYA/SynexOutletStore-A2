package presentation.web;

import infrastructure.jdbc.JdbcItemRepository;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;

public class PosItemsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUserId") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html");
            return;
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        var itemRepo = new JdbcItemRepository();
        var items = itemRepo.findAllActive();

        out.println("<!doctype html><html><head><meta charset='utf-8'><title>POS Items</title>");
        out.println("<script src='https://cdn.tailwindcss.com'></script>");
        out.println("</head>");
        out.println("<body class='bg-gradient-to-br from-orange-50 to-amber-50 min-h-screen'>");

        // Header
        out.println("<header class='bg-white shadow-lg mb-8'>");
        out.println("<div class='container mx-auto px-4 py-6'>");
        out.println("<h2 class='text-3xl font-bold bg-gradient-to-r from-orange-600 to-amber-600 bg-clip-text text-transparent'>");
        out.println("💳 POS - Point of Sale</h2>");
        out.println("</div></header>");

        out.println("<div class='container mx-auto px-4 max-w-7xl'>");

        // Navigation buttons
        out.println("<div class='mb-6 flex gap-4'>");
        out.println("<a href='" + req.getContextPath() + "/cart/view' ");
        out.println("class='inline-flex items-center bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg font-semibold transition duration-200 shadow-lg'>");
        out.println("🛒 View Cart</a>");
        out.println("<a href='" + req.getContextPath() + "/pos-home.html' ");
        out.println("class='inline-flex items-center bg-gray-600 hover:bg-gray-700 text-white px-6 py-3 rounded-lg font-semibold transition duration-200 shadow-lg'>");
        out.println("← Back to POS Home</a>");
        out.println("</div>");

        // Items table
        out.println("<div class='bg-white rounded-2xl shadow-xl overflow-hidden'>");
        out.println("<div class='overflow-x-auto'>");
        out.println("<table class='w-full'>");
        out.println("<thead class='bg-gradient-to-r from-orange-600 to-amber-600 text-white'>");
        out.println("<tr>");
        out.println("<th class='px-6 py-4 text-left font-semibold'>Code</th>");
        out.println("<th class='px-6 py-4 text-left font-semibold'>Name</th>");
        out.println("<th class='px-6 py-4 text-right font-semibold'>Price</th>");
        out.println("<th class='px-6 py-4 text-center font-semibold'>Quantity</th>");
        out.println("<th class='px-6 py-4 text-center font-semibold'>Action</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody class='divide-y divide-gray-200'>");

        for (var it : items) {
            out.println("<tr class='hover:bg-orange-50 transition duration-200'>");
            out.println("<td class='px-6 py-4 font-mono text-sm text-gray-600'>" + it.code() + "</td>");
            out.println("<td class='px-6 py-4 text-gray-800 font-medium'>" + it.name() + "</td>");
            out.println("<td class='px-6 py-4 text-green-600 font-semibold'>LKR " + it.price() + "</td>");
            out.println("<td class='px-6 py-4'>");
            out.println("<form method='post' action='" + req.getContextPath() + "/pos/cart/add' class='flex gap-2'>");
            out.println("<input type='hidden' name='itemCode' value='" + it.code() + "'/>");
            out.println("<input type='number' name='qty' value='1' min='1' required ");
            out.println("class='w-20 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent'/>");
            out.println("<button type='submit' class='bg-gradient-to-r from-orange-600 to-amber-600 hover:from-orange-700 hover:to-amber-700 text-white px-6 py-2 rounded-lg transition duration-200 font-semibold shadow-lg'>Add to Cart</button>");
            out.println("</form>");
            out.println("</td>");
            out.println("<td class='px-6 py-4 text-center text-gray-500'>-</td>");
            out.println("</tr>");
        }

        out.println("</tbody>");
        out.println("</table>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");

        out.println("</body></html>");
    }
}
