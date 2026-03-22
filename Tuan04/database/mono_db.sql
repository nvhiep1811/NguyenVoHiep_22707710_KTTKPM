/* =========================================================
   MONOLITHIC DATABASE
   Database: mono_db
   ========================================================= */

-- Tạo database
IF DB_ID('mono_db') IS NOT NULL
BEGIN
    ALTER DATABASE mono_db SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE mono_db;
END;
GO

CREATE DATABASE mono_db;
GO

USE mono_db;
GO

/* =========================================================
   1. USERS
   Lưu thông tin đăng nhập
   ========================================================= */
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL CONSTRAINT df_users_status DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL CONSTRAINT df_users_created_at DEFAULT GETDATE(),
    updated_at DATETIME NULL
);
GO

ALTER TABLE users
ADD CONSTRAINT uq_users_username UNIQUE (username);
GO

ALTER TABLE users
ADD CONSTRAINT uq_users_email UNIQUE (email);
GO

ALTER TABLE users
ADD CONSTRAINT ck_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED'));
GO

/* =========================================================
   2. ROLES
   Lưu vai trò USER / ADMIN
   ========================================================= */
CREATE TABLE roles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);
GO

ALTER TABLE roles
ADD CONSTRAINT uq_roles_name UNIQUE (name);
GO

/* =========================================================
   3. USER_ROLES
   Quan hệ nhiều - nhiều giữa users và roles
   ========================================================= */
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at DATETIME NOT NULL CONSTRAINT df_user_roles_assigned_at DEFAULT GETDATE(),
    PRIMARY KEY (user_id, role_id)
);
GO

ALTER TABLE user_roles
ADD CONSTRAINT fk_user_roles_user
FOREIGN KEY (user_id) REFERENCES users(id);
GO

ALTER TABLE user_roles
ADD CONSTRAINT fk_user_roles_role
FOREIGN KEY (role_id) REFERENCES roles(id);
GO

/* =========================================================
   4. USER_PROFILES
   Lưu hồ sơ cá nhân
   Quan hệ 1 - 1 với users
   ========================================================= */
CREATE TABLE user_profiles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    full_name NVARCHAR(100) NULL,
    phone VARCHAR(20) NULL,
    address NVARCHAR(255) NULL,
    date_of_birth DATE NULL,
    avatar_url VARCHAR(255) NULL,
    created_at DATETIME NOT NULL CONSTRAINT df_user_profiles_created_at DEFAULT GETDATE(),
    updated_at DATETIME NULL
);
GO

ALTER TABLE user_profiles
ADD CONSTRAINT uq_user_profiles_user_id UNIQUE (user_id);
GO

ALTER TABLE user_profiles
ADD CONSTRAINT fk_user_profiles_user
FOREIGN KEY (user_id) REFERENCES users(id);
GO

/* =========================================================
   5. INDEXES
   ========================================================= */
CREATE INDEX idx_users_username ON users(username);
GO

CREATE INDEX idx_users_email ON users(email);
GO

CREATE INDEX idx_user_profiles_full_name ON user_profiles(full_name);
GO

/* =========================================================
   6. DỮ LIỆU MẪU
   ========================================================= */
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('ADMIN');
GO

INSERT INTO users (username, email, password_hash, status)
VALUES 
('hiep', 'hiep@gmail.com', '$2a$10$abcdefghijklmnopqrstuv1234567890examplehash11111', 'ACTIVE'),
('admin', 'admin@gmail.com', '$2a$10$abcdefghijklmnopqrstuv1234567890examplehash22222', 'ACTIVE');
GO

INSERT INTO user_profiles (user_id, full_name, phone, address, date_of_birth, avatar_url)
VALUES
(1, N'Hiệp Nguyễn', '0123456789', N'TP. Hồ Chí Minh', '2003-05-10', 'https://example.com/avatar-hiep.jpg'),
(2, N'Quản trị viên', '0987654321', N'Hà Nội', '2000-01-01', 'https://example.com/avatar-admin.jpg');
GO

INSERT INTO user_roles (user_id, role_id)
VALUES
(1, 1), -- hiep -> USER
(2, 2); -- admin -> ADMIN
GO

/* =========================================================
   7. VIEW KIỂM TRA DỮ LIỆU
   ========================================================= */
CREATE VIEW vw_user_full_info AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.status,
    p.full_name,
    p.phone,
    p.address,
    p.date_of_birth,
    p.avatar_url,
    STRING_AGG(r.name, ', ') AS roles
FROM users u
LEFT JOIN user_profiles p ON u.id = p.user_id
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY
    u.id, u.username, u.email, u.status,
    p.full_name, p.phone, p.address, p.date_of_birth, p.avatar_url;
GO

/* =========================================================
   8. TEST QUERY
   ========================================================= */
SELECT * FROM users;
SELECT * FROM roles;
SELECT * FROM user_roles;
SELECT * FROM user_profiles;
SELECT * FROM vw_user_full_info;
GO