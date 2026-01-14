package ports.in;

import domain.cart.Cart;
import domain.orders.PaymentMethod;

public interface CheckoutService {
    long placeOnlineOrder(long customerId, Cart cart, PaymentMethod paymentMethod);
    long placePosOrder(long cashierUserId, Cart cart, PaymentMethod paymentMethod);

}
