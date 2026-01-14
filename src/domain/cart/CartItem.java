package domain.cart;

public record CartItem(String itemCode, String name, double unitPrice, int qty) {
    public double lineTotal() { return unitPrice * qty; }
}
