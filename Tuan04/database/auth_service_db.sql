/* =========================================================
   AUTH SERVICE DATABASE
   Database: auth_service_db
   ========================================================= */

-- Tạo database
IF DB_ID('auth_service_db') IS NOT NULL
BEGIN
    ALTER DATABASE auth_service_db SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE auth_service_db;
END;
GO

CREATE DATABASE auth_service_db;
GO

USE auth_service_db;
GO

/* =========================================================
   1. USERS_AUTH
   Chỉ lưu thông tin xác thực
   ========================================================= */
CREATE TABLE users_auth (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL CONSTRAINT df_users_auth_status DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL CONSTRAINT df_users_auth_created_at DEFAULT GETDATE(),
    updated_at DATETIME NULL
);
GO

ALTER TABLE users_auth
ADD CONSTRAINT uq_users_auth_username UNIQUE (username);
GO

ALTER TABLE users_auth
ADD CONSTRAINT uq_users_auth_email UNIQUE (email);
GO

ALTER TABLE users_auth
ADD CONSTRAINT ck_users_auth_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED'));
GO

/* =========================================================
   2. ROLES
   ========================================================= */
CREATE TABLE roles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);
GO

ALTER TABLE roles
ADD CONSTRAINT uq_auth_roles_name UNIQUE (name);
GO

/* =========================================================
   3. USER_ROLES
   ========================================================= */
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at DATETIME NOT NULL CONSTRAINT df_auth_user_roles_assigned_at DEFAULT GETDATE(),
    PRIMARY KEY (user_id, role_id)
);
GO

ALTER TABLE user_roles
ADD CONSTRAINT fk_auth_user_roles_user
FOREIGN KEY (user_id) REFERENCES users_auth(id);
GO

ALTER TABLE user_roles
ADD CONSTRAINT fk_auth_user_roles_role
FOREIGN KEY (role_id) REFERENCES roles(id);
GO

/* =========================================================
   4. INDEXES
   ========================================================= */
CREATE INDEX idx_users_auth_username ON users_auth(username);
GO

CREATE INDEX idx_users_auth_email ON users_auth(email);
GO

/* =========================================================
   5. DỮ LIỆU MẪU
   ========================================================= */
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('ADMIN');
GO

INSERT INTO users_auth (username, email, password_hash, status)
VALUES
('hiep', 'hiep@gmail.com', '$2a$10$abcdefghijklmnopqrstuv1234567890examplehash11111', 'ACTIVE'),
('admin', 'admin@gmail.com', '$2a$10$abcdefghijklmnopqrstuv1234567890examplehash22222', 'ACTIVE');
GO

INSERT INTO user_roles (user_id, role_id)
VALUES
(1, 1), -- hiep -> USER
(2, 2); -- admin -> ADMIN
GO

/* =========================================================
   6. VIEW KIỂM TRA DỮ LIỆU
   ========================================================= */
CREATE VIEW vw_auth_users AS
SELECT
    ua.id,
    ua.username,
    ua.email,
    ua.status,
    STRING_AGG(r.name, ', ') AS roles
FROM users_auth ua
LEFT JOIN user_roles ur ON ua.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY ua.id, ua.username, ua.email, ua.status;
GO

/* =========================================================
   7. TEST QUERY
   ========================================================= */
SELECT * FROM users_auth;
SELECT * FROM roles;
SELECT * FROM user_roles;
SELECT * FROM vw_auth_users;
GO