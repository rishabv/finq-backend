-- Roles
CREATE TABLE IF NOT EXISTS roles (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  role_name VARCHAR(100) UNIQUE NOT NULL,
  role_code VARCHAR(50) UNIQUE NOT NULL,
  parent_role_id UUID NULL,
  level INT NOT NULL DEFAULT 1,
  description TEXT,
  creation_date TIMESTAMP,
  last_modified_date TIMESTAMP,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT fk_roles_parent
    FOREIGN KEY (parent_role_id) REFERENCES roles(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_role_code ON roles (role_code);
CREATE INDEX IF NOT EXISTS idx_role_name ON roles (role_name);
CREATE INDEX IF NOT EXISTS idx_roles_parent ON roles(parent_role_id);

-- Permissions
CREATE TABLE IF NOT EXISTS permissions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  permission_name VARCHAR(50) UNIQUE NOT NULL,
  permission_code VARCHAR(50) UNIQUE NOT NULL,
  resource VARCHAR(50) NOT NULL,
  action VARCHAR(20) NOT NULL,
  description TEXT,
  creation_date TIMESTAMP,
  last_modified_date TIMESTAMP,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_permission_code ON permissions (permission_code);
CREATE INDEX IF NOT EXISTS idx_permissions_resource_action ON permissions (resource, action);

-- Role-Permission join
CREATE TABLE IF NOT EXISTS role_permission (
  role_id UUID NOT NULL,
  permission_id UUID NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  CONSTRAINT fk_role_permission_role
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
  CONSTRAINT fk_role_permission_permission
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_role_permission_role ON role_permission(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permission_permission ON role_permission(permission_id);