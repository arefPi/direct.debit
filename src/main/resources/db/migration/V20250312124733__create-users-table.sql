CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id VARCHAR(255) NOT NULL,
    national_id VARCHAR(10) NOT NULL,
    phone_number VARCHAR(12) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_user_id ON users(user_id);

CREATE OR REPLACE FUNCTION update_users_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_users_updated_at_column();
