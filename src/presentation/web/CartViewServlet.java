package presentation.web;

import domain.store.Cart;
import domain.store.CartItem;
import infrastructure.jdbc.JdbcItemRepository;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // Check if either customer or staff is logged in
        boolean isCustomer = (session != null && session.getAttribute("customerId") != null);
        boolean isStaff = (session != null && session.getAttribute("staffUserId") != null);

        if (!isCustomer && !isStaff) {
            resp.sendRedirect(req.getContextPath() + "/staff-login.html");
            return;
        }

        // Use appropriate cart based on user type
        Cart cart;
        if (isStaff) {
            cart = SessionCart.getOrCreatePosCart(session);
        } else {
            cart = SessionCart.getOrCreate(session);
        }
        var repo = new JdbcItemRepository();

        List<CartItem> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (var e : cart.items().entrySet()) {
            String code = e.getKey();
            int qty = e.getValue();

            var opt = repo.findByCode(code);
            if (opt.isEmpty()) continue;

            var item = opt.get();
            CartItem line = new CartItem(item.code(), item.name(), item.price(), qty);
            lines.add(line);
            total = total.add(line.lineTotal());
        }

        resp.setContentType("text/html;charset=UTF-8");

        StringBuilder out = new StringBuilder();
        String pageTitle = isStaff ? "POS Cart" : "Your Cart";
        String headerTitle = isStaff ? "💳 POS Shopping Cart" : "🛒 Your Shopping Cart";

        out.append("<!doctype html><html><head><meta charset='utf-8'><title>").append(pageTitle).append("</title>");
        out.append("<script src='https://cdn.tailwindcss.com'></script>");
        out.append("</head>");
        out.append("<body class='bg-gradient-to-br from-blue-50 to-indigo-50 min-h-screen'>");

        // Header
        out.append("<header class='bg-white shadow-lg mb-8'>");
        out.append("<div class='container mx-auto px-4 py-6'>");
        out.append("<h2 class='text-3xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent'>");
        out.append(headerTitle).append("</h2>");
        out.append("</div></header>");

        out.append("<div class='container mx-auto px-4 max-w-6xl'>");
        out.append("<div class='mb-6'>");

        // Use appropriate continue shopping link based on user type
        String continueShoppingUrl = isStaff ? "/pos/home" : "/store/home";
        String continueShoppingText = isStaff ? "← Back to POS" : "← Continue Shopping";
        out.append("<a href='").append(req.getContextPath()).append(continueShoppingUrl).append("' ");
        out.append("class='inline-flex items-center text-blue-600 hover:text-blue-700 font-semibold hover:underline'>");
        out.append(continueShoppingText).append("</a>");
        out.append("</div>");

        if (lines.isEmpty()) {
            out.append("<div class='bg-white rounded-2xl shadow-xl p-12 text-center'>");
            out.append("<div class='w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4'>");
            out.append("<svg class='w-12 h-12 text-gray-400' fill='none' stroke='currentColor' viewBox='0 0 24 24'>");
            out.append("<path stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z'></path>");
            out.append("</svg></div>");
            out.append("<p class='text-xl text-gray-600'>Your cart is empty.</p>");
            out.append("</div>");
        } else {
            out.append("<div class='bg-white rounded-2xl shadow-xl overflow-hidden'>");
            out.append("<div class='overflow-x-auto'>");
            out.append("<table class='w-full'>");
            out.append("<thead class='bg-gradient-to-r from-blue-600 to-indigo-600 text-white'>");
            out.append("<tr>");
            out.append("<th class='px-6 py-4 text-left font-semibold'>Code</th>");
            out.append("<th class='px-6 py-4 text-left font-semibold'>Name</th>");
            out.append("<th class='px-6 py-4 text-right font-semibold'>Unit Price</th>");
            out.append("<th class='px-6 py-4 text-center font-semibold'>Qty</th>");
            out.append("<th class='px-6 py-4 text-right font-semibold'>Total</th>");
            out.append("<th class='px-6 py-4 text-center font-semibold'>Action</th>");
            out.append("</tr></thead>");
            out.append("<tbody class='divide-y divide-gray-200'>");

            for (CartItem ci : lines) {
                out.append("<tr class='hover:bg-gray-50 transition duration-200'>");
                out.append("<td class='px-6 py-4 font-mono text-sm text-gray-600'>").append(esc(ci.itemCode())).append("</td>");
                out.append("<td class='px-6 py-4 font-semibold text-gray-800'>").append(esc(ci.name())).append("</td>");
                out.append("<td class='px-6 py-4 text-right text-gray-700'>LKR ").append(ci.unitPrice()).append("</td>");
                out.append("<td class='px-6 py-4 text-center'>");
                out.append("<span class='inline-block bg-blue-100 text-blue-800 px-3 py-1 rounded-full font-semibold'>").append(ci.quantity()).append("</span>");
                out.append("</td>");
                out.append("<td class='px-6 py-4 text-right font-bold text-gray-900'>LKR ").append(ci.lineTotal()).append("</td>");
                out.append("<td class='px-6 py-4 text-center'>");

                // Use appropriate remove action based on user type
                String removeAction = isStaff ? "/pos/cart/remove" : "/cart/remove";
                out.append("<form method='post' action='").append(req.getContextPath()).append(removeAction).append("'>");
                out.append("<input type='hidden' name='itemCode' value='").append(esc(ci.itemCode())).append("'>");
                out.append("<button type='submit' class='bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg transition duration-200 font-semibold text-sm'>Remove</button>");
                out.append("</form>");
                out.append("</td>");
                out.append("</tr>");
            }

            out.append("</tbody></table>");
            out.append("</div>");

            // Total and actions
            out.append("<div class='bg-gray-50 px-6 py-6 border-t border-gray-200'>");
            out.append("<div class='flex justify-between items-center mb-6'>");
            out.append("<h3 class='text-2xl font-bold text-gray-800'>Total:</h3>");
            out.append("<h3 class='text-3xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent'>LKR ").append(total).append("</h3>");
            out.append("</div>");

            out.append("<div class='flex gap-4'>");

            // Use appropriate clear action based on user type
            String clearAction = isStaff ? "/pos/cart/clear" : "/cart/clear";
            out.append("<form method='post' action='").append(req.getContextPath()).append(clearAction).append("' class='flex-1'>");
            out.append("<button type='submit' class='w-full bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold py-3 rounded-lg transition duration-200'>Clear Cart</button>");
            out.append("</form>");

            // Show different checkout buttons based on user type
            if (isStaff) {
                // Staff sees POS checkout button
                out.append("<a href='").append(req.getContextPath()).append("/pos-checkout.html' ");
                out.append("class='flex-1 text-center bg-gradient-to-r from-orange-600 to-amber-600 hover:from-orange-700 hover:to-amber-700 text-white font-semibold py-3 rounded-lg transition duration-200 shadow-lg'>💳 POS Checkout</a>");
            } else {
                // Customer sees online checkout button
                out.append("<a href='").append(req.getContextPath()).append("/checkout.html' ");
                out.append("class='flex-1 text-center bg-gradient-to-r from-green-600 to-teal-600 hover:from-green-700 hover:to-teal-700 text-white font-semibold py-3 rounded-lg transition duration-200 shadow-lg'>✓ Proceed to Checkout</a>");
            }
            out.append("</div>");
            out.append("</div>");
            out.append("</div>");
        }

        out.append("</div></body></html>");

        resp.getWriter().write(out.toString());
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
                .replace("\"","&quot;").replace("'","&#39;");
    }
}
