package presentation.web;

import domain.store.Cart;
import jakarta.servlet.http.HttpSession;

public final class SessionCart {
    private SessionCart() {}

    public static Cart getOrCreate(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}
