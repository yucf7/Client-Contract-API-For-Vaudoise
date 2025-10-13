-- ==============================
-- CLIENT & CONTRACT TABLES
-- ==============================

-- Client type enum
CREATE TYPE client_type AS ENUM ('PERSON', 'COMPANY');

-- Client table
CREATE TABLE IF NOT EXISTS client (
    id SERIAL PRIMARY KEY,
    type client_type NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    birthdate DATE,
    company_identifier VARCHAR(50) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Index on email for faster lookups
CREATE INDEX IF NOT EXISTS idx_client_email ON client(email);

-- Contract table
CREATE TABLE IF NOT EXISTS contract (
    id SERIAL PRIMARY KEY,
    client_id INTEGER NOT NULL REFERENCES client(id) ON DELETE CASCADE,
    start_date DATE DEFAULT CURRENT_DATE NOT NULL,
    end_date DATE,
    cost_amount NUMERIC(10,2) NOT NULL CHECK (cost_amount >= 0),
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- New column to mark active contracts
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

-- Index to quickly fetch active contracts (using is_active flag)
CREATE INDEX IF NOT EXISTS idx_contract_client_active 
ON contract(client_id) 
WHERE is_active = TRUE;

-- ==============================
-- Seed Data for Testing
-- ==============================

-- Clients
INSERT INTO client (type, name, email, phone, birthdate, company_identifier)
VALUES
('PERSON', 'Alice Martin', 'alice.martin@example.com', '+41791234567', '1990-05-12', NULL),
('PERSON', 'Bob Dupont', 'bob.dupont@example.com', '+41792345678', '1985-11-20', NULL),
('COMPANY', 'TechCorp SA', 'contact@techcorp.ch', '+41793456789', NULL, 'TC-12345'),
('COMPANY', 'InnoSolutions AG', 'hello@innosolutions.ch', '+41794567890', NULL, 'IS-98765');

-- Contracts
INSERT INTO contract (client_id, start_date, end_date, cost_amount)
VALUES
(1, '2025-01-01', NULL, 1200.00),
(1, '2023-01-01', '2024-12-31', 800.00), -- old contract
(2, '2025-02-15', NULL, 1500.00),
(3, '2025-03-01', NULL, 5000.00),
(4, '2025-04-01', '2025-09-30', 3000.00);
