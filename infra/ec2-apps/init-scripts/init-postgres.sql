-- Script de inicialización de PostgreSQL para Pedidos360
-- Se ejecuta automáticamente al crear el contenedor

-- Crear extensión para UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tabla de productos (Catalog)
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    category VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de pedidos (Orders)
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(100) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    delivery_address TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'CREADO',
    total_amount DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Tabla de items de pedido
CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Tabla de reportes agregados (Report)
CREATE TABLE IF NOT EXISTS report_snapshots (
    id BIGSERIAL PRIMARY KEY,
    report_type VARCHAR(100) NOT NULL,
    report_data JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para mejorar performance
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_active ON products(active);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);

-- Datos de ejemplo para Catalog
INSERT INTO products (name, description, price, stock, category, active) VALUES
('Café Latte Grande', 'Café con leche de 16oz', 3500.00, 100, 'Bebidas Calientes', true),
('Cappuccino', 'Espresso con espuma de leche', 3000.00, 80, 'Bebidas Calientes', true),
('Té Verde', 'Té verde importado', 2000.00, 50, 'Bebidas Calientes', true),
('Croissant de Almendras', 'Croissant relleno de crema de almendras', 2000.00, 40, 'Panadería', true),
('Brownie de Chocolate', 'Brownie con nueces', 2000.00, 30, 'Postres', true),
('Sandwich Integral', 'Sandwich de pavo y queso en pan integral', 6000.00, 25, 'Comida', true),
('Ensalada César', 'Ensalada con pollo, parmesano y aderezo césar', 7000.00, 20, 'Comida', true),
('Jugo Natural Naranja', 'Jugo recién exprimido 250ml', 3000.00, 60, 'Bebidas Frías', true),
('Smoothie Frutilla', 'Smoothie de frutilla con yogurt', 4000.00, 35, 'Bebidas Frías', true),
('Muffin Arándanos', 'Muffin con arándanos frescos', 2000.00, 45, 'Panadería', true)
ON CONFLICT DO NOTHING;

-- Pedido de ejemplo
INSERT INTO orders (customer_id, customer_name, customer_email, delivery_address, status, total_amount, created_by) VALUES
('DEMO001', 'Cliente Demo', 'demo@pedidos360.cl', 'Av. Providencia 2594, Providencia', 'CREADO', 7000.00, 'system')
ON CONFLICT DO NOTHING;

-- Items del pedido de ejemplo
INSERT INTO order_items (order_id, product_id, product_name, quantity, price) VALUES
(1, 1, 'Café Latte Grande', 2, 3500.00)
ON CONFLICT DO NOTHING;

-- Mensajes de confirmación
DO $$
BEGIN
    RAISE NOTICE '✅ Base de datos Pedidos360 inicializada correctamente';
    RAISE NOTICE '📦 % productos cargados', (SELECT COUNT(*) FROM products);
    RAISE NOTICE '🛒 % pedidos de ejemplo', (SELECT COUNT(*) FROM orders);
END $$;
