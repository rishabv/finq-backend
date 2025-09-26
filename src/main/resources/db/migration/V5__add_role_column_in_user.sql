ALTER TABLE users ADD COLUMN role_id UUID;

UPDATE users SET role_id = 'ddfe79e4-8e26-4b61-8481-1498d9b852ad' WHERE role_id IS NULL;

ALTER TABLE users ADD CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id)
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE users ALTER COLUMN role_id SET NOT NULL;

CREATE INDEX idx_users_role_id ON users(role_id);