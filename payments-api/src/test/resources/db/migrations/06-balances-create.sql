-- 06-balances-create.sql
CREATE TABLE balances (
    id BIGSERIAL PRIMARY KEY,
    currency VARCHAR(3) NOT NULL,
    balance DECIMAL(10,2) NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_balances_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_balances_user_id ON balances(user_id);