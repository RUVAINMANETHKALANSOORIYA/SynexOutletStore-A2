package presentation.web;

import domain.store.Cart;
import jakarta.servlet.http.HttpSession;

public final class SessionCart {
    private SessionCart() {}

    /**
     * Gets or creates customer cart (for online customers)
     */
    public static Cart getOrCreate(HttpSession session) {
        return getOrCreate(session, "cart");
    }

    /**
     * Gets or creates POS cart (for cashiers/managers)
     */
    public static Cart getOrCreatePosCart(HttpSession session) {
        return getOrCreate(session, "posCart");
    }

    /**
     * Gets or creates cart with specific attribute name
     */
    private static Cart getOrCreate(HttpSession session, String attributeName) {
        Cart cart = (Cart) session.getAttribute(attributeName);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(attributeName, cart);
        }
        return cart;
    }
}
