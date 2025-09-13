CREATE EXTENSION IF NOT EXISTS pgcrypto;
ALTER TABLE IF EXISTS admin_users
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

insert into admin_users (
                    employee_id,
                    first_name,
                    last_name,
                    email,
                    gender,
                    designation,
                    department,
                    branch_id,
                    role_id,
                    status,
                    password_hash,
                    mfa_enabled,
                    failed_login_attempts,
                    is_deleted,
                    is_active,
                    creation_date,
                    last_modified_date)
select 'FQ1721' AS employee_id,
        'Rishabh' AS first_name,
        'Verma' AS last_name,
        '${superadmin_email}' AS email,
        NULL::varchar AS gender,
        'Super Admin' AS designation,
        'Administration' AS department,
        NULL::varchar AS branch_id,
        r.id AS role_id,
        'ACTIVE' AS status,
        crypt('${superadmin_password}', gen_salt('bf')) AS password_hash,
        FALSE AS mfa_enabled,
        0 AS failed_login_attempts,
        FALSE AS is_deleted,
        TRUE AS is_active,
        NOW() AS creation_date,
        NOW() AS last_modified_date
from (select id from roles where role_code = 'SUPER_ADMIN') r WHERE not exists
(select 1 from admin_users au where au.email = '${superadmin_email}')