-- ===============================
-- DATABASE
-- ===============================
DROP DATABASE IF EXISTS syos_posdb;
CREATE DATABASE syos_posdb;
USE syos_posdb;

-- ===============================
-- USERS (Staff only: Manager, Cashier)
-- ===============================
CREATE TABLE users (
                       user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role ENUM('MANAGER', 'CASHIER') NOT NULL,
                       email VARCHAR(100),
                       status ENUM('ACTIVE', 'DISABLED') DEFAULT 'ACTIVE',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- DEV Seed Manager
INSERT INTO users (username, password_hash, role, email)
VALUES ('manager', '123456', 'MANAGER', 'manager@syos.com'),
       ('cashier1', '123456', 'CASHIER', 'cashier1@syos.com');

-- ===============================
-- CUSTOMERS (Online customers only)
-- ===============================
CREATE TABLE customers (
                           customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           username VARCHAR(50) UNIQUE NOT NULL,
                           password_hash VARCHAR(64) NOT NULL,
                           full_name VARCHAR(100) NOT NULL,
                           email VARCHAR(100) NOT NULL,
                           phone VARCHAR(20),
                           status ENUM('ACTIVE', 'DISABLED') DEFAULT 'ACTIVE',
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO customers (username, password_hash, full_name, email, phone)
VALUES ('john_doe', 'password123', 'John Doe', 'john@example.com', '+94771234567');

-- ===============================
-- ITEMS (Products) - ENHANCED
-- ===============================
CREATE TABLE items (
                       item_code VARCHAR(20) PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       price DECIMAL(10,2) NOT NULL,
                       is_active TINYINT(1) NOT NULL DEFAULT 1,  -- NEW: For manager item lifecycle
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- NEW
);

INSERT INTO items (item_code, name, price) VALUES
                                               ('ITM001','Milk 1L', 450.00),
                                               ('ITM002','Bread', 220.00),
                                               ('ITM003','Eggs 12 pack', 980.00),
                                               ('ITM004', 'Rice 5kg', 1250.00),
                                               ('ITM005', 'Cooking Oil 1L', 680.00),
                                               ('ITM006', 'Sugar 1kg', 320.00),
                                               ('ITM007', 'Tea Bags 100pk', 550.00),
                                               ('ITM008', 'Coffee 200g', 890.00),
                                               ('ITM009', 'Biscuits Pack', 280.00),
                                               ('ITM010', 'Juice 1L', 420.00);

-- ===============================
-- STOCK - ENHANCED WITH MANAGER FEATURES
-- ===============================
CREATE TABLE stock (
                       stock_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       item_code VARCHAR(20) NOT NULL,
                       batch_code VARCHAR(50) NOT NULL,
                       received_date DATE NOT NULL,  -- Renamed from purchase_date for consistency
                       expiry_date DATE NOT NULL,
                       shelf_qty INT NOT NULL DEFAULT 0,
                       web_qty INT NOT NULL DEFAULT 0,
                       supplier VARCHAR(100),  -- NEW: Track supplier information
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (item_code) REFERENCES items(item_code),
                       UNIQUE (item_code, batch_code),
                       CONSTRAINT chk_nonnegative_stock CHECK (shelf_qty >= 0 AND web_qty >= 0)
);

CREATE INDEX idx_stock_fifo ON stock(item_code, expiry_date, received_date);
CREATE INDEX idx_stock_supplier ON stock(supplier);  -- NEW: For supplier reporting

-- Stock data
INSERT INTO stock (item_code, batch_code, received_date, expiry_date, shelf_qty, web_qty, supplier) VALUES
-- Milk batches
('ITM001', 'MILK-BATCH-001', '2026-01-01', '2026-01-15', 50, 30, 'Dairy Co Ltd'),
('ITM001', 'MILK-BATCH-002', '2026-01-03', '2026-01-20', 75, 50, 'Dairy Co Ltd'),
('ITM001', 'MILK-BATCH-003', '2026-01-05', '2026-01-25', 100, 80, 'Fresh Milk Inc'),

-- Bread batches
('ITM002', 'BREAD-BATCH-001', '2026-01-04', '2026-01-08', 40, 25, 'Bakery Express'),
('ITM002', 'BREAD-BATCH-002', '2026-01-05', '2026-01-10', 60, 40, 'Bakery Express'),

-- Eggs batches
('ITM003', 'EGGS-BATCH-001', '2025-12-28', '2026-01-12', 30, 20, 'Farm Fresh Ltd'),
('ITM003', 'EGGS-BATCH-002', '2026-01-02', '2026-01-18', 45, 35, 'Farm Fresh Ltd'),
('ITM003', 'EGGS-BATCH-003', '2026-01-04', '2026-01-22', 55, 40, 'Golden Eggs Co'),

-- Additional items
('ITM004', 'RICE-BATCH-001', '2025-12-15', '2026-06-15', 80, 60, 'Rice Mill Co'),
('ITM004', 'RICE-BATCH-002', '2026-01-02', '2026-07-02', 120, 90, 'Rice Mill Co'),
('ITM005', 'OIL-BATCH-001', '2025-12-20', '2026-03-20', 45, 35, 'Oil Brands Ltd'),
('ITM005', 'OIL-BATCH-002', '2026-01-03', '2026-04-03', 65, 50, 'Oil Brands Ltd'),
('ITM006', 'SUGAR-BATCH-001', '2025-12-10', '2026-12-10', 90, 70, 'Sweet Supplies'),
('ITM007', 'TEA-BATCH-001', '2025-12-25', '2026-05-25', 55, 40, 'Tea Traders'),
('ITM007', 'TEA-BATCH-002', '2026-01-04', '2026-06-04', 70, 55, 'Tea Traders'),
('ITM008', 'COFFEE-BATCH-001', '2025-12-28', '2026-04-28', 40, 30, 'Coffee Roasters'),
('ITM008', 'COFFEE-BATCH-002', '2026-01-05', '2026-05-05', 60, 45, 'Coffee Roasters'),
('ITM009', 'BISCUIT-BATCH-001', '2026-01-01', '2026-02-01', 70, 50, 'Snack Factory'),
('ITM009', 'BISCUIT-BATCH-002', '2026-01-04', '2026-02-15', 85, 65, 'Snack Factory'),
('ITM010', 'JUICE-BATCH-001', '2026-01-03', '2026-01-17', 35, 25, 'Fruit Drinks Ltd'),
('ITM010', 'JUICE-BATCH-002', '2026-01-05', '2026-01-22', 50, 40, 'Fruit Drinks Ltd');

-- ===============================
-- ORDERS (Physical + Online)
-- ===============================
CREATE TABLE orders (
                        order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        order_type ENUM('PHYSICAL', 'ONLINE') NOT NULL,
                        customer_id BIGINT NULL,
                        cashier_user_id BIGINT NULL,
                        payment_method ENUM('CASH', 'CARD') NOT NULL,
                        total_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
                        status ENUM('PENDING', 'PAID', 'CANCELLED') NOT NULL DEFAULT 'PAID',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
                        FOREIGN KEY (cashier_user_id) REFERENCES users(user_id)
);

CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_cashier ON orders(cashier_user_id);
CREATE INDEX idx_orders_date ON orders(created_at);  -- NEW: For reporting

-- ===============================
-- ORDER ITEMS
-- ===============================
CREATE TABLE order_items (
                             order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             order_id BIGINT NOT NULL,
                             item_code VARCHAR(20) NOT NULL,
                             quantity INT NOT NULL,
                             unit_price DECIMAL(10,2) NOT NULL,
                             line_total DECIMAL(10,2) NOT NULL,
                             FOREIGN KEY (order_id) REFERENCES orders(order_id),
                             FOREIGN KEY (item_code) REFERENCES items(item_code),
                             CONSTRAINT chk_qty_positive CHECK (quantity > 0)
);

CREATE INDEX idx_order_items_item ON order_items(item_code);  -- NEW: For reporting

-- ===============================
-- FIFO STOCK MOVEMENTS
-- ===============================
CREATE TABLE stock_movements (
                                 movement_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 stock_id BIGINT NOT NULL,
                                 order_id BIGINT NOT NULL,
                                 quantity INT NOT NULL,
                                 source ENUM('SHELF', 'WEB') NOT NULL,
                                 movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (stock_id) REFERENCES stock(stock_id),
                                 FOREIGN KEY (order_id) REFERENCES orders(order_id),
                                 CONSTRAINT chk_move_qty CHECK (quantity > 0)
);

CREATE INDEX idx_stock_movements_order ON stock_movements(order_id);
CREATE INDEX idx_stock_movements_stock ON stock_movements(stock_id);

-- ===============================
-- NEW: MANAGER MODULE TABLES
-- ===============================

-- Stock Transfers (Shelf ↔ Web)
CREATE TABLE stock_transfers (
                                 transfer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 item_code VARCHAR(20) NOT NULL,
                                 stock_id BIGINT NOT NULL,
                                 transfer_type ENUM('SHELF_TO_WEB', 'WEB_TO_SHELF') NOT NULL,
                                 quantity INT NOT NULL,
                                 transferred_by BIGINT NOT NULL,
                                 transfer_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 notes TEXT,
                                 FOREIGN KEY (item_code) REFERENCES items(item_code),
                                 FOREIGN KEY (stock_id) REFERENCES stock(stock_id),
                                 FOREIGN KEY (transferred_by) REFERENCES users(user_id),
                                 CONSTRAINT chk_transfer_qty_positive CHECK (quantity > 0)
);

CREATE INDEX idx_transfers_date ON stock_transfers(transfer_date);
CREATE INDEX idx_transfers_item ON stock_transfers(item_code);
CREATE INDEX idx_transfers_manager ON stock_transfers(transferred_by);

-- Manager Activity Log
CREATE TABLE manager_activities (
                                    activity_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    manager_id BIGINT NOT NULL,
                                    activity_type VARCHAR(50) NOT NULL,
                                    description TEXT NOT NULL,
                                    item_code VARCHAR(20),
                                    activity_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (manager_id) REFERENCES users(user_id),
                                    FOREIGN KEY (item_code) REFERENCES items(item_code)
);

CREATE INDEX idx_activities_manager ON manager_activities(manager_id);
CREATE INDEX idx_activities_date ON manager_activities(activity_date);
CREATE INDEX idx_activities_type ON manager_activities(activity_type);

-- ===============================
-- USEFUL VIEWS FOR REPORTING
-- ===============================

-- Stock Summary View
CREATE VIEW v_stock_summary AS
SELECT
    s.item_code,
    i.name AS item_name,
    i.price AS unit_price,
    SUM(s.shelf_qty) AS total_shelf_qty,
    SUM(s.web_qty) AS total_web_qty,
    SUM(s.shelf_qty + s.web_qty) AS total_qty,
    MIN(s.expiry_date) AS earliest_expiry,
    COUNT(DISTINCT s.batch_code) AS batch_count
FROM stock s
         JOIN items i ON s.item_code = i.item_code
WHERE i.is_active = 1
GROUP BY s.item_code, i.name, i.price;

-- Low Stock Alert View
CREATE VIEW v_low_stock_items AS
SELECT
    item_code,
    item_name,
    total_shelf_qty,
    total_web_qty,
    total_qty
FROM v_stock_summary
WHERE total_qty < 50  -- Configurable threshold
ORDER BY total_qty ASC;

-- Recent Transfers View
CREATE VIEW v_recent_transfers AS
SELECT
    st.transfer_id,
    st.item_code,
    i.name AS item_name,
    st.transfer_type,
    st.quantity,
    u.username AS transferred_by_username,
    st.transfer_date,
    st.notes
FROM stock_transfers st
         JOIN items i ON st.item_code = i.item_code
         JOIN users u ON st.transferred_by = u.user_id
ORDER BY st.transfer_date DESC;

-- ===============================
-- VERIFICATION QUERIES
-- ===============================
-- Run these to verify setup:

-- Check tables exist
-- SHOW TABLES;

-- Check stock_transfers structure
-- DESCRIBE stock_transfers;

-- Check manager_activities structure
-- DESCRIBE manager_activities;

-- Check enhanced items table
-- DESCRIBE items;

-- Check enhanced stock table
-- DESCRIBE stock;

-- Test stock summary view
-- SELECT * FROM v_stock_summary LIMIT 5;

-- Test low stock alerts
-- SELECT * FROM v_low_stock_items;

-- ===============================
-- DISCOUNT SYSTEM - FIFO-BASED
-- ===============================

-- Discounts Table (FIFO-based discount system)
CREATE TABLE discounts (
    discount_id INT PRIMARY KEY AUTO_INCREMENT,
    discount_code VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL,
    discount_type ENUM('PERCENTAGE', 'FIXED_AMOUNT') NOT NULL,
    discount_value DECIMAL(10, 2) NOT NULL,
    min_purchase_amount DECIMAL(10, 2) DEFAULT 0.00,
    max_discount_amount DECIMAL(10, 2) NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    usage_limit INT DEFAULT NULL,
    times_used INT DEFAULT 0,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    CHECK (discount_value > 0),
    CHECK (min_purchase_amount >= 0),
    CHECK (start_date < end_date),
    CHECK (times_used >= 0)
);

-- Order Discounts Junction Table (track which discounts were applied to which orders)
CREATE TABLE order_discounts (
    order_discount_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    discount_id INT NOT NULL,
    discount_amount DECIMAL(10, 2) NOT NULL,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (discount_id) REFERENCES discounts(discount_id) ON DELETE CASCADE
);

-- Add discount_amount column to orders table
ALTER TABLE orders ADD COLUMN discount_amount DECIMAL(10, 2) DEFAULT 0.00;

-- Index for faster FIFO queries (oldest active discounts first)
CREATE INDEX idx_discounts_fifo ON discounts(start_date, is_active, end_date);
CREATE INDEX idx_discounts_active ON discounts(is_active, start_date);
CREATE INDEX idx_order_discounts_order ON order_discounts(order_id);

-- Insert sample discounts for testing
INSERT INTO discounts (discount_code, description, discount_type, discount_value, min_purchase_amount, max_discount_amount, start_date, end_date, created_by)
VALUES
    ('WELCOME10', '10% off for new customers', 'PERCENTAGE', 10.00, 50.00, 100.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1),
    ('SAVE50', 'LKR 50 off on orders above LKR 200', 'FIXED_AMOUNT', 50.00, 200.00, NULL, '2026-01-15 00:00:00', '2026-03-31 23:59:59', 1),
    ('BIGDEAL', '20% off on large orders', 'PERCENTAGE', 20.00, 500.00, 200.00, '2026-01-20 00:00:00', '2026-02-28 23:59:59', 1);

-- View for active discounts with FIFO ordering
CREATE VIEW v_active_discounts_fifo AS
SELECT
    discount_id,
    discount_code,
    description,
    discount_type,
    discount_value,
    min_purchase_amount,
    max_discount_amount,
    start_date,
    end_date,
    usage_limit,
    times_used,
    (usage_limit - times_used) AS remaining_uses,
    CASE
        WHEN usage_limit IS NULL THEN 'Unlimited'
        ELSE CONCAT(times_used, '/', usage_limit)
    END AS usage_status
FROM discounts
WHERE is_active = TRUE
  AND start_date <= NOW()
  AND end_date >= NOW()
  AND (usage_limit IS NULL OR times_used < usage_limit)
ORDER BY start_date ASC;  -- FIFO: Oldest first

