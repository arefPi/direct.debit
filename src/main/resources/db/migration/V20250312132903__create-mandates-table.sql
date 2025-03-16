CREATE TABLE IF NOT EXISTS mandates (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    version INTEGER NOT NULL DEFAULT 0,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    reference_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    max_daily_transfer_amount DECIMAL(10, 2) NOT NULL,
    max_daily_transaction_count INTEGER NOT NULL,
    max_transaction_amount DECIMAL(10, 2) NOT NULL,
    expires_in TIMESTAMPTZ NOT NULL,
    token JSONB,
    provider_id INTEGER REFERENCES providers(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_mandates_user_id ON mandates(user_id);
CREATE INDEX IF NOT EXISTS idx_mandates_status ON mandates(status);
CREATE INDEX IF NOT EXISTS idx_mandates_created_at ON mandates(created_at);

CREATE OR REPLACE FUNCTION update_mandates_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_updated_at
BEFORE UPDATE ON mandates
FOR EACH ROW
EXECUTE FUNCTION update_mandates_updated_at_column();
