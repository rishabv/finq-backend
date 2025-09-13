-- Ensure pgcrypto is available for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Add auditing columns to role_permission (if not present)
ALTER TABLE IF EXISTS role_permission
    ADD COLUMN IF NOT EXISTS granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS granted_by VARCHAR(100);

-- Seed permissions (permission_code mirrors permission_name for uniqueness)
INSERT INTO permissions (permission_name, permission_code, resource, action, description)
VALUES
-- User management permissions
('USER_READ', 'USER_READ', 'USER', 'READ', 'View user information'),
('USER_CREATE', 'USER_CREATE', 'USER', 'CREATE', 'Create new users'),
('USER_UPDATE', 'USER_UPDATE', 'USER', 'UPDATE', 'Update user information'),
('USER_DELETE', 'USER_DELETE', 'USER', 'DELETE', 'Delete users'),

-- Account management permissions
('ACCOUNT_READ', 'ACCOUNT_READ', 'ACCOUNT', 'READ', 'View account information'),
('ACCOUNT_CREATE', 'ACCOUNT_CREATE', 'ACCOUNT', 'CREATE', 'Create new accounts'),
('ACCOUNT_UPDATE', 'ACCOUNT_UPDATE', 'ACCOUNT', 'UPDATE', 'Update account information'),
('ACCOUNT_DELETE', 'ACCOUNT_DELETE', 'ACCOUNT', 'DELETE', 'Delete accounts'),
('ACCOUNT_FREEZE', 'ACCOUNT_FREEZE', 'ACCOUNT', 'FREEZE', 'Freeze accounts'),
('ACCOUNT_UNFREEZE', 'ACCOUNT_UNFREEZE', 'ACCOUNT', 'UNFREEZE', 'Unfreeze accounts'),

-- Transaction permissions
('TRANSACTION_READ', 'TRANSACTION_READ', 'TRANSACTION', 'READ', 'View transactions'),
('TRANSACTION_CREATE', 'TRANSACTION_CREATE', 'TRANSACTION', 'CREATE', 'Create transactions'),
('TRANSACTION_APPROVE', 'TRANSACTION_APPROVE', 'TRANSACTION', 'APPROVE', 'Approve transactions'),
('TRANSACTION_REJECT', 'TRANSACTION_REJECT', 'TRANSACTION', 'REJECT', 'Reject transactions'),

-- KYC permissions
('KYC_READ', 'KYC_READ', 'KYC', 'READ', 'View KYC documents'),
('KYC_APPROVE', 'KYC_APPROVE', 'KYC', 'APPROVE', 'Approve KYC'),
('KYC_REJECT', 'KYC_REJECT', 'KYC', 'REJECT', 'Reject KYC'),

-- Admin permissions
('ADMIN_READ', 'ADMIN_READ', 'ADMIN', 'READ', 'View admin information'),
('ADMIN_CREATE', 'ADMIN_CREATE', 'ADMIN', 'CREATE', 'Create admin users'),
('ADMIN_UPDATE', 'ADMIN_UPDATE', 'ADMIN', 'UPDATE', 'Update admin users'),
('ADMIN_DELETE', 'ADMIN_DELETE', 'ADMIN', 'DELETE', 'Delete admin users'),

-- System permissions
('SYSTEM_CONFIG', 'SYSTEM_CONFIG', 'SYSTEM', 'CONFIG', 'Configure system settings'),
('AUDIT_READ', 'AUDIT_READ', 'AUDIT', 'READ', 'View audit logs'),
('REPORT_GENERATE', 'REPORT_GENERATE', 'REPORT', 'GENERATE', 'Generate reports')
ON CONFLICT (permission_code) DO NOTHING;

-- Seed roles (align with current roles schema; no is_system_role column)
INSERT INTO roles (role_name, role_code, description, parent_role_id, level)
VALUES
('Super Admin', 'SUPER_ADMIN', 'Full system access', NULL, 1),
('Bank Manager', 'BANK_MANAGER', 'Bank branch manager', NULL, 2),
('Customer Service', 'CUSTOMER_SERVICE', 'Customer service representative', NULL, 3),
('KYC Officer', 'KYC_OFFICER', 'KYC verification officer', NULL, 3),
('Auditor', 'AUDITOR', 'System auditor', NULL, 3),
('Customer', 'CUSTOMER', 'Bank customer', NULL, 4)
ON CONFLICT (role_code) DO NOTHING;

-- Assign all permissions to Super Admin
INSERT INTO role_permission (role_id, permission_id, granted_by)
SELECT (SELECT id FROM roles WHERE role_code = 'SUPER_ADMIN'), p.id, 'SYSTEM'
FROM permissions p
ON CONFLICT DO NOTHING;

-- Assign permissions to Bank Manager
INSERT INTO role_permission (role_id, permission_id, granted_by)
SELECT (SELECT id FROM roles WHERE role_code = 'BANK_MANAGER'), p.id, 'SYSTEM'
FROM permissions p
WHERE p.permission_name IN (
    'USER_READ', 'USER_UPDATE',
    'ACCOUNT_READ', 'ACCOUNT_CREATE', 'ACCOUNT_UPDATE', 'ACCOUNT_FREEZE', 'ACCOUNT_UNFREEZE',
    'TRANSACTION_READ', 'TRANSACTION_APPROVE', 'TRANSACTION_REJECT',
    'KYC_READ', 'KYC_APPROVE', 'KYC_REJECT',
    'REPORT_GENERATE'
)
ON CONFLICT DO NOTHING;

-- Assign permissions to Customer Service
INSERT INTO role_permission (role_id, permission_id, granted_by)
SELECT (SELECT id FROM roles WHERE role_code = 'CUSTOMER_SERVICE'), p.id, 'SYSTEM'
FROM permissions p
WHERE p.permission_name IN (
    'USER_READ', 'USER_UPDATE',
    'ACCOUNT_READ', 'ACCOUNT_UPDATE',
    'TRANSACTION_READ'
)
ON CONFLICT DO NOTHING;

-- Assign permissions to KYC Officer
INSERT INTO role_permission (role_id, permission_id, granted_by)
SELECT (SELECT id FROM roles WHERE role_code = 'KYC_OFFICER'), p.id, 'SYSTEM'
FROM permissions p
WHERE p.permission_name IN (
    'USER_READ',
    'KYC_READ', 'KYC_APPROVE', 'KYC_REJECT'
)
ON CONFLICT DO NOTHING;

-- Assign permissions to Auditor
INSERT INTO role_permission (role_id, permission_id, granted_by)
SELECT (SELECT id FROM roles WHERE role_code = 'AUDITOR'), p.id, 'SYSTEM'
FROM permissions p
WHERE p.permission_name IN (
    'USER_READ',
    'ACCOUNT_READ',
    'TRANSACTION_READ',
    'AUDIT_READ',
    'REPORT_GENERATE'
)
ON CONFLICT DO NOTHING;

-- Assign minimal permissions to Customer
INSERT INTO role_permission (role_id, permission_id, granted_by)
SELECT (SELECT id FROM roles WHERE role_code = 'CUSTOMER'), p.id, 'SYSTEM'
FROM permissions p
WHERE p.permission_name IN (
    'ACCOUNT_READ',
    'TRANSACTION_READ'
)
ON CONFLICT DO NOTHING;

-- Personal security questions
CREATE TABLE IF NOT EXISTS personal_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question VARCHAR(255) UNIQUE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    creation_date TIMESTAMP,
    last_modified_date TIMESTAMP
);

-- Seed personal questions
INSERT INTO personal_questions (question)
VALUES
('What is your mother''s maiden name?'),
('What was the name of your first pet?'),
('In which city were you born?'),
('What was the name of your elementary school?'),
('What is your favorite teacher''s name?'),
('What is the name of the street you grew up on?'),
('What is the name of your best childhood friend?'),
('What is your favorite book?'),
('What was the model of your first car?')
ON CONFLICT (question) DO NOTHING;
