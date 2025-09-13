-- Enable pgcrypto for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Users table
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  email VARCHAR(255),
  customer_id VARCHAR(255),
  phone VARCHAR(50),
  gender VARCHAR(50),
  date_of_birth VARCHAR(50),
  pan_number VARCHAR(50),
  adhaar_number VARCHAR(50),
  password_hash VARCHAR(255),

  status VARCHAR(50) NOT NULL DEFAULT 'INACTIVE',
  kyc_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
  is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
  is_phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
  mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
  mfa_secret VARCHAR(255),
  last_login_at VARCHAR(255),
  failed_login_attempts INT NOT NULL DEFAULT 0,
  account_locked_until TIME,

  -- BaseEntity fields
  creation_date TIMESTAMP,
  last_modified_date TIMESTAMP,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Indexes from @Table(indexes=...)
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users (phone);
CREATE INDEX IF NOT EXISTS idx_users_customer_id ON users (customer_id);
CREATE INDEX IF NOT EXISTS idx_users_status ON users (status);

-- Optional unique constraint on email
ALTER TABLE IF EXISTS users
  ADD CONSTRAINT UK_users_email UNIQUE (email);