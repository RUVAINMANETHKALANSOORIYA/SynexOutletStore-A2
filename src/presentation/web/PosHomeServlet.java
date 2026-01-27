package presentation.web;

import infrastructure.jdbc.JdbcItemRepository;
import ports.in.BrowseItemsService;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;

public class PosHomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Protect page: must be logged in as staff (cashier or manager)
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUserId") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html");
            return;
        }

        String role = (String) session.getAttribute("staffRole");
        String username = String.valueOf(session.getAttribute("staffUsername"));

        var service = new BrowseItemsService(new JdbcItemRepository());
        var items = service.browseActiveItems();

        resp.setContentType("text/html;charset=UTF-8");

        StringBuilder out = new StringBuilder();
        out.append("<!doctype html><html><head><meta charset='utf-8'><title>POS - SYOS</title>");
        out.append("<script src='https://cdn.tailwindcss.com'></script>");
        out.append("</head>");
        out.append("<body class='bg-gradient-to-br from-orange-50 to-amber-50 min-h-screen'>");

        // Header
        out.append("<header class='bg-white shadow-lg mb-8'>");
        out.append("<div class='container mx-auto px-4 py-6'>");
        out.append("<div class='flex justify-between items-center'>");
        out.append("<h2 class='text-3xl font-bold bg-gradient-to-r from-orange-600 to-amber-600 bg-clip-text text-transparent'>");
        out.append("💳 POS - Welcome, ").append(escape(username)).append(" (").append(escape(role)).append(")</h2>");

        out.append("<div class='flex gap-4'>");
        out.append("<a href='").append(req.getContextPath()).append("/cart/view' ");
        out.append("class='bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-6 py-2 rounded-lg hover:from-blue-700 hover:to-indigo-700 transition duration-200 font-semibold shadow-md'>");
        out.append("🛒 View Cart</a>");

        if ("MANAGER".equalsIgnoreCase(role)) {
            out.append("<a href='").append(req.getContextPath()).append("/manager-dashboard.html' ");
            out.append("class='bg-purple-600 hover:bg-purple-700 text-white px-6 py-2 rounded-lg transition duration-200 font-semibold shadow-md'>");
            out.append("📊 Manager Dashboard</a>");
        } else {
            out.append("<a href='").append(req.getContextPath()).append("/cashier-dashboard.html' ");
            out.append("class='bg-orange-600 hover:bg-orange-700 text-white px-6 py-2 rounded-lg transition duration-200 font-semibold shadow-md'>");
            out.append("📊 Dashboard</a>");
        }

        out.append("<form method='post' action='").append(req.getContextPath()).append("/staff/logout' class='inline'>");
        out.append("<button type='submit' class='bg-gray-200 hover:bg-gray-300 text-gray-700 px-6 py-2 rounded-lg transition duration-200 font-semibold'>Logout</button>");
        out.append("</form>");
        out.append("</div></div></div></header>");

        out.append("<div class='container mx-auto px-4 max-w-7xl'>");
        out.append("<h3 class='text-2xl font-bold text-gray-800 mb-6'>🏷️ Products for Sale</h3>");

        if (items.isEmpty()) {
            out.append("<div class='bg-white rounded-2xl shadow-xl p-12 text-center'>");
            out.append("<p class='text-xl text-gray-600'>No products available at the moment.</p>");
            out.append("</div>");
        } else {
            out.append("<div class='grid md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6'>");

            for (var it : items) {
                out.append("<div class='bg-white rounded-xl shadow-lg hover:shadow-2xl transition duration-300 overflow-hidden transform hover:scale-[1.02]'>");
                out.append("<div class='bg-gradient-to-br from-orange-500 to-amber-600 h-32 flex items-center justify-center'>");
                out.append("<svg class='w-16 h-16 text-white opacity-80' fill='none' stroke='currentColor' viewBox='0 0 24 24'>");
                out.append("<path stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4'></path>");
                out.append("</svg></div>");

                out.append("<div class='p-5'>");
                out.append("<p class='text-xs text-gray-500 font-mono mb-1'>").append(escape(it.code())).append("</p>");
                out.append("<h4 class='text-lg font-bold text-gray-800 mb-2'>").append(escape(it.name())).append("</h4>");
                out.append("<p class='text-2xl font-bold bg-gradient-to-r from-green-600 to-teal-600 bg-clip-text text-transparent mb-4'>LKR ").append(it.price()).append("</p>");

                out.append("<form method='post' action='").append(req.getContextPath()).append("/pos/cart/add' class='space-y-3'>");
                out.append("<input type='hidden' name='itemCode' value='").append(escape(it.code())).append("'>");
                out.append("<div class='flex items-center gap-2'>");
                out.append("<label class='text-sm font-medium text-gray-700'>Qty:</label>");
                out.append("<input type='number' name='qty' value='1' min='1' ");
                out.append("class='flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent outline-none'>");
                out.append("</div>");
                out.append("<button type='submit' class='w-full bg-gradient-to-r from-orange-600 to-amber-600 hover:from-orange-700 hover:to-amber-700 text-white font-semibold py-2 rounded-lg transition duration-200 shadow-md'>");
                out.append("Add to Cart</button>");
                out.append("</form>");
                out.append("</div></div>");
            }

            out.append("</div>");
        }

        out.append("</div></body></html>");

        resp.getWriter().write(out.toString());
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

