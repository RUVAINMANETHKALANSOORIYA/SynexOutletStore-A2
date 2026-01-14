package domain.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderReport {
    private final Long orderId;
    private final String orderType;
    private final String paymentMethod;
    private final BigDecimal totalAmount;
    private final String customerInfo; // Customer name or "POS Sale"
    private final LocalDateTime createdAt;

    public OrderReport(Long orderId, String orderType, String paymentMethod,
                      BigDecimal totalAmount, String customerInfo, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.orderType = orderType;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.customerInfo = customerInfo;
        this.createdAt = createdAt;
    }

    // Getters
    public Long orderId() { return orderId; }
    public String orderType() { return orderType; }
    public String paymentMethod() { return paymentMethod; }
    public BigDecimal totalAmount() { return totalAmount; }
    public String customerInfo() { return customerInfo; }
    public LocalDateTime createdAt() { return createdAt; }
}
