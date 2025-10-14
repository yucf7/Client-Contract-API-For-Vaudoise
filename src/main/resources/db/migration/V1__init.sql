-- ==============================
-- CLIENT & CONTRACT TABLES
-- ==============================

-- Ensure clean start
DROP TABLE IF EXISTS contract CASCADE;
DROP TABLE IF EXISTS client CASCADE;
DROP TYPE IF EXISTS client_type CASCADE;

-- Enable extension to generate UUIDs if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Client table using UUID for id (compatible with your Java UUID mapping)
CREATE TABLE client (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type VARCHAR(50) NOT NULL CHECK (type IN ('PERSON', 'COMPANY')),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    birthdate DATE,
    company_identifier VARCHAR(50) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Index on email for faster lookups
CREATE INDEX idx_client_email ON client(email);

-- Contract table
CREATE TABLE contract (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id UUID NOT NULL REFERENCES client(id) ON DELETE CASCADE,
    start_date DATE DEFAULT CURRENT_DATE NOT NULL,
    end_date DATE,
    cost_amount NUMERIC(10,2) NOT NULL CHECK (cost_amount >= 0),
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Trigger function to update is_active flag
CREATE OR REPLACE FUNCTION update_contract_is_active()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.end_date IS NULL OR NEW.end_date > CURRENT_DATE THEN
        NEW.is_active := TRUE;
    ELSE
        NEW.is_active := FALSE;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to call the function before insert or update
CREATE TRIGGER trg_update_is_active
BEFORE INSERT OR UPDATE ON contract
FOR EACH ROW
EXECUTE FUNCTION update_contract_is_active();

-- Index to quickly fetch active contracts
CREATE INDEX idx_contract_client_active ON contract(client_id) WHERE is_active = TRUE;

