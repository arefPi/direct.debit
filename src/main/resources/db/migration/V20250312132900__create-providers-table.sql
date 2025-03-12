CREATE TABLE IF NOT EXISTS providers (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    provider_name VARCHAR(100) NOT NULL,
    provider_id VARCHAR(255) NOT NULL UNIQUE,
    redirect_url TEXT NOT NULL,
    client_id VARCHAR(255) NOT NULL,
    client_secret VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_providers_id ON providers(provider_id);

CREATE OR REPLACE FUNCTION update_providers_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_providers_updated_at
    BEFORE UPDATE ON providers
    FOR EACH ROW
    EXECUTE FUNCTION update_providers_updated_at();