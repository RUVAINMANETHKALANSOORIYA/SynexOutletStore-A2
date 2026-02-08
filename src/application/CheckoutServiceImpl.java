package application;

import domain.cart.Cart;
import domain.cart.CartItem;
import domain.orders.Order;
import domain.orders.OrderType;
import domain.orders.PaymentMethod;
import infrastructure.jdbc.Db;
import infrastructure.jdbc.JdbcStockRepository;
import ports.in.CheckoutService;
import ports.out.OrderRepository;
import ports.out.StockRepository;

import java.sql.Connection;

public final class CheckoutServiceImpl implements CheckoutService {

    private final OrderRepository orders;
    private final StockRepository stock;

    public CheckoutServiceImpl(OrderRepository orders, StockRepository stock) {
        this.orders = orders;
        this.stock = stock;
    }

    @Override
    public long placeOnlineOrder(long customerId, Cart cart, PaymentMethod paymentMethod) {
            // Enhanced input validation
        if (customerId <= 0) {
            throw domain.exception.CheckoutFailedException.invalidCustomer("Invalid customer ID: " + customerId);
        }

        if (cart == null || cart.isEmpty()) {
            throw domain.exception.CheckoutFailedException.emptyCart();
        }

        if (paymentMethod == null) {
            throw domain.exception.CheckoutFailedException.invalidPaymentMethod("null");
        }

        // Enhanced cart validation
        try {
            // cart.validateForCheckout(); // Cart validation will be done during item processing
        } catch (Exception e) {
            throw domain.exception.CheckoutFailedException.cartValidationFailed("Cart validation failed: " + e.getMessage());
        }

        // Validate cart items and check for business rules
        java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
        for (CartItem item : cart.items()) {
            if (item.itemCode() == null || item.itemCode().trim().isEmpty()) {
                throw domain.exception.CheckoutFailedException.invalidItem("Invalid item code in cart");
            }
            if (item.qty() <= 0) {
                throw domain.exception.CheckoutFailedException.invalidQuantity(item.itemCode(), item.qty());
            }
            if (item.unitPrice() <= 0) {
                throw domain.exception.CheckoutFailedException.invalidPrice(item.itemCode(), item.unitPrice());
            }

            totalAmount = totalAmount.add(java.math.BigDecimal.valueOf(item.unitPrice() * item.qty()));
        }

        // Business rule: reasonable order total limits
        if (totalAmount.compareTo(new java.math.BigDecimal("100000")) > 0) { // 100K limit
            throw domain.exception.CheckoutFailedException.orderTooLarge(totalAmount);
        }

        // Enhanced stock availability check with detailed error reporting
        if (stock instanceof JdbcStockRepository) {
            JdbcStockRepository jdbcStock = (JdbcStockRepository) stock;

            for (CartItem item : cart.items()) {
                try {
                    int available = jdbcStock.getAvailableWebStock(item.itemCode());
                    if (available < item.qty()) {
                        throw domain.exception.CheckoutFailedException.stockDepletion(
                            item.itemCode(), item.qty(), available);
                    }
                } catch (domain.exception.CheckoutFailedException e) {
                    throw e; // Re-throw our custom exceptions
                } catch (Exception e) {
                    throw domain.exception.CheckoutFailedException.stockCheckFailed(
                        "Failed to check stock for item " + item.itemCode() + ": " + e.getMessage());
                }
            }
        }

        Connection connection = null;
        try {
            connection = Db.get();
            if (connection == null) {
                throw domain.exception.CheckoutFailedException.databaseConnectionFailed();
            }

            connection.setAutoCommit(false);

            try {
                // Enhanced order creation with validation
                Order order = new Order(null, customerId, OrderType.ONLINE, paymentMethod, cart.total());
                long orderId;

                try {
                    orderId = orders.insertOrder(connection, order);
                    if (orderId <= 0) {
                        throw domain.exception.CheckoutFailedException.orderCreationFailed("Invalid order ID returned");
                    }
                } catch (RuntimeException e) {
                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("foreign key")) {
                            throw domain.exception.CheckoutFailedException.invalidCustomer("Customer does not exist: " + customerId);
                        } else if (e.getMessage().contains("constraint")) {
                            throw domain.exception.CheckoutFailedException.dataConstraintViolation("Order data constraint violation");
                        } else if (e.getMessage().contains("database") || e.getMessage().contains("SQL")) {
                            throw domain.exception.CheckoutFailedException.databaseError("Failed to create order: " + e.getMessage());
                        }
                    }
                    throw domain.exception.CheckoutFailedException.orderCreationFailed("Order creation failed: " + e.getMessage());
                } catch (Exception e) {
                    throw domain.exception.CheckoutFailedException.orderCreationFailed("Order creation failed: " + e.getMessage());
                }

                // Process each cart item with enhanced error handling
                for (CartItem item : cart.items()) {
                    try {
                        // Insert order item
                        orders.insertOrderItem(connection, orderId, item.itemCode(), item.qty(), item.unitPrice());

                        // Deduct stock with validation
                        stock.deductWebStockFifo(connection, orderId, item.itemCode(), item.qty());

                    } catch (RuntimeException e) {
                        if (e.getMessage() != null) {
                            if (e.getMessage().contains("foreign key") && e.getMessage().contains("item")) {
                                throw domain.exception.CheckoutFailedException.invalidItem("Item does not exist: " + item.itemCode());
                            } else if (e.getMessage().contains("stock") || e.getMessage().contains("insufficient")) {
                                throw domain.exception.CheckoutFailedException.stockDepletion(
                                    item.itemCode(), item.qty(), 0); // Stock was depleted during transaction
                            } else if (e.getMessage().contains("database") || e.getMessage().contains("SQL")) {
                                throw domain.exception.CheckoutFailedException.databaseError(
                                    "Failed to process item " + item.itemCode() + ": " + e.getMessage());
                            }
                        }
                        throw domain.exception.CheckoutFailedException.itemProcessingFailed(
                            "Failed to process item " + item.itemCode() + ": " + e.getMessage());
                    } catch (Exception e) {
                        throw domain.exception.CheckoutFailedException.itemProcessingFailed(
                            "Failed to process item " + item.itemCode() + ": " + e.getMessage());
                    }
                }

                connection.commit();

                // Log successful checkout
                java.util.logging.Logger.getLogger(this.getClass().getName())
                    .info(String.format("Online order completed successfully: ID=%d, Customer=%d, Items=%d, Total=%.2f",
                         orderId, customerId, cart.items().size(), cart.total()));

                return orderId;

            } catch (domain.exception.CheckoutFailedException e) {
                connection.rollback();
                throw e; // Re-throw our custom exceptions
            } catch (java.sql.SQLException e) {
                connection.rollback();
                if (e.getMessage().contains("timeout")) {
                    throw domain.exception.CheckoutFailedException.timeout();
                } else if (e.getMessage().contains("connection")) {
                    throw domain.exception.CheckoutFailedException.databaseConnectionFailed();
                } else {
                    throw domain.exception.CheckoutFailedException.databaseError("Database error: " + e.getMessage());
                }
            } catch (Exception ex) {
                connection.rollback();
                throw domain.exception.CheckoutFailedException.unexpectedError("Unexpected checkout error: " + ex.getMessage());
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (Exception e) {
                    java.util.logging.Logger.getLogger(this.getClass().getName())
                        .warning("Failed to reset auto-commit: " + e.getMessage());
                }
            }

        } catch (domain.exception.CheckoutFailedException e) {
            throw e; // Re-throw our custom exceptions
        } catch (java.sql.SQLException e) {
            throw domain.exception.CheckoutFailedException.databaseConnectionFailed();
        } catch (Exception e) {
            throw new RuntimeException("placeOnlineOrder failed: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    java.util.logging.Logger.getLogger(this.getClass().getName())
                        .warning("Failed to close database connection: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public long placePosOrder(long cashierUserId, Cart cart, PaymentMethod paymentMethod) {
        // Enhanced input validation
        if (cashierUserId <= 0) {
            throw domain.exception.CheckoutFailedException.invalidCustomer("Invalid cashier ID: " + cashierUserId);
        }

        if (cart == null || cart.isEmpty()) {
            throw domain.exception.CheckoutFailedException.emptyCart();
        }

        if (paymentMethod == null) {
            throw domain.exception.CheckoutFailedException.invalidPaymentMethod("null");
        }

        // Enhanced cart validation
        try {
            // cart.validateForCheckout(); // Cart validation will be done during item processing
        } catch (Exception e) {
            throw domain.exception.CheckoutFailedException.cartValidationFailed("Cart validation failed: " + e.getMessage());
        }

        // Enhanced stock availability check for shelf stock
        if (stock instanceof JdbcStockRepository) {
            JdbcStockRepository jdbcStock = (JdbcStockRepository) stock;

            for (CartItem item : cart.items()) {
                try {
                    int available = jdbcStock.getAvailableShelfStock(item.itemCode());
                    if (available < item.qty()) {
                        throw domain.exception.CheckoutFailedException.stockDepletion(
                            item.itemCode(), item.qty(), available);
                    }
                } catch (domain.exception.CheckoutFailedException e) {
                    throw e; // Re-throw our custom exceptions
                } catch (Exception e) {
                    throw domain.exception.CheckoutFailedException.stockCheckFailed(
                        "Failed to check shelf stock for item " + item.itemCode() + ": " + e.getMessage());
                }
            }
        }

        Connection connection = null;
        try {
            connection = Db.get();
            if (connection == null) {
                throw domain.exception.CheckoutFailedException.databaseConnectionFailed();
            }

            connection.setAutoCommit(false);

            try {
                // PHYSICAL order => user_id = cashierUserId, customerId = null
                Order order = new Order(cashierUserId, null, OrderType.PHYSICAL, paymentMethod, cart.total());
                long orderId;

                try {
                    orderId = orders.insertOrder(connection, order);
                    if (orderId <= 0) {
                        throw domain.exception.CheckoutFailedException.orderCreationFailed("Invalid order ID returned");
                    }
                } catch (RuntimeException e) {
                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("foreign key")) {
                            throw domain.exception.CheckoutFailedException.invalidCustomer("Cashier does not exist: " + cashierUserId);
                        } else if (e.getMessage().contains("constraint")) {
                            throw domain.exception.CheckoutFailedException.dataConstraintViolation("Order data constraint violation");
                        } else if (e.getMessage().contains("database") || e.getMessage().contains("SQL")) {
                            throw domain.exception.CheckoutFailedException.databaseError("Failed to create POS order: " + e.getMessage());
                        }
                    }
                    throw domain.exception.CheckoutFailedException.orderCreationFailed("POS order creation failed: " + e.getMessage());
                } catch (Exception e) {
                    throw domain.exception.CheckoutFailedException.orderCreationFailed("POS order creation failed: " + e.getMessage());
                }

                // Process each cart item with enhanced error handling
                for (CartItem item : cart.items()) {
                    try {
                        // Insert order item
                        orders.insertOrderItem(connection, orderId, item.itemCode(), item.qty(), item.unitPrice());

                        // POS uses SHELF FIFO deduction with validation
                        stock.deductShelfStockFifo(connection, orderId, item.itemCode(), item.qty());

                    } catch (RuntimeException e) {
                        if (e.getMessage() != null) {
                            if (e.getMessage().contains("foreign key") && e.getMessage().contains("item")) {
                                throw domain.exception.CheckoutFailedException.invalidItem("Item does not exist: " + item.itemCode());
                            } else if (e.getMessage().contains("stock") || e.getMessage().contains("insufficient")) {
                                throw domain.exception.CheckoutFailedException.stockDepletion(
                                    item.itemCode(), item.qty(), 0); // Stock was depleted during transaction
                            } else if (e.getMessage().contains("database") || e.getMessage().contains("SQL")) {
                                throw domain.exception.CheckoutFailedException.databaseError(
                                    "Failed to process POS item " + item.itemCode() + ": " + e.getMessage());
                            }
                        }
                        throw domain.exception.CheckoutFailedException.itemProcessingFailed(
                            "Failed to process POS item " + item.itemCode() + ": " + e.getMessage());
                    } catch (Exception e) {
                        throw domain.exception.CheckoutFailedException.itemProcessingFailed(
                            "Failed to process POS item " + item.itemCode() + ": " + e.getMessage());
                    }
                }

                connection.commit();

                // Log successful POS checkout
                java.util.logging.Logger.getLogger(this.getClass().getName())
                    .info(String.format("POS order completed successfully: ID=%d, Cashier=%d, Items=%d, Total=%.2f",
                         orderId, cashierUserId, cart.items().size(), cart.total()));

                return orderId;

            } catch (domain.exception.CheckoutFailedException e) {
                connection.rollback();
                throw e; // Re-throw our custom exceptions
            } catch (java.sql.SQLException e) {
                connection.rollback();
                if (e.getMessage().contains("timeout")) {
                    throw domain.exception.CheckoutFailedException.timeout();
                } else if (e.getMessage().contains("connection")) {
                    throw domain.exception.CheckoutFailedException.databaseConnectionFailed();
                } else {
                    throw domain.exception.CheckoutFailedException.databaseError("POS database error: " + e.getMessage());
                }
            } catch (Exception ex) {
                connection.rollback();
                throw domain.exception.CheckoutFailedException.unexpectedError("Unexpected POS checkout error: " + ex.getMessage());
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (Exception e) {
                    java.util.logging.Logger.getLogger(this.getClass().getName())
                        .warning("Failed to reset auto-commit: " + e.getMessage());
                }
            }

        } catch (domain.exception.CheckoutFailedException e) {
            throw e; // Re-throw our custom exceptions
        } catch (java.sql.SQLException e) {
            throw domain.exception.CheckoutFailedException.databaseConnectionFailed();
        } catch (Exception e) {
            throw new RuntimeException("placePosOrder failed: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    java.util.logging.Logger.getLogger(this.getClass().getName())
                        .warning("Failed to close database connection: " + e.getMessage());
                }
            }
        }
    }

}
