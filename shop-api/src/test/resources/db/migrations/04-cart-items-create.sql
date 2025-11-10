-- 04-cart-items-create.sql
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    quantity INTEGER NOT NULL,
    item_id BIGINT NOT NULL,
    order_id BIGINT,
    CONSTRAINT fk_cart_items_item_id FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_cart_items_order_id FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE INDEX IF NOT EXISTS idx_cart_items_item_id ON cart_items(item_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_order_id ON cart_items(order_id);