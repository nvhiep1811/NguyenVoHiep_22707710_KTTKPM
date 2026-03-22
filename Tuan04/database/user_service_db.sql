/* =========================================================
   USER SERVICE DATABASE
   Database: user_service_db
   ========================================================= */

-- Tạo database
IF DB_ID('user_service_db') IS NOT NULL
BEGIN
    ALTER DATABASE user_service_db SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE user_service_db;
END;
GO

CREATE DATABASE user_service_db;
GO

USE user_service_db;
GO

/* =========================================================
   1. USER_PROFILES
   Lưu thông tin hồ sơ
   user_id là ID logic từ auth_service_db.users_auth.id
   Không tạo foreign key cross-database để giữ tính độc lập service
   ========================================================= */
CREATE TABLE user_profiles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    full_name NVARCHAR(100) NULL,
    phone VARCHAR(20) NULL,
    address NVARCHAR(255) NULL,
    date_of_birth DATE NULL,
    avatar_url VARCHAR(255) NULL,
    created_at DATETIME NOT NULL CONSTRAINT df_user_service_profiles_created_at DEFAULT GETDATE(),
    updated_at DATETIME NULL
);
GO

ALTER TABLE user_profiles
ADD CONSTRAINT uq_user_service_profiles_user_id UNIQUE (user_id);
GO

/* =========================================================
   2. INDEXES
   ========================================================= */
CREATE INDEX idx_user_service_profiles_user_id ON user_profiles(user_id);
GO

CREATE INDEX idx_user_service_profiles_full_name ON user_profiles(full_name);
GO

/* =========================================================
   3. DỮ LIỆU MẪU
   user_id phải khớp logic với users_auth.id bên auth_service_db
   ========================================================= */
INSERT INTO user_profiles (user_id, full_name, phone, address, date_of_birth, avatar_url)
VALUES
(1, N'Hiệp Nguyễn', '0123456789', N'TP. Hồ Chí Minh', '2003-05-10', 'https://example.com/avatar-hiep.jpg'),
(2, N'Quản trị viên', '0987654321', N'Hà Nội', '2000-01-01', 'https://example.com/avatar-admin.jpg');
GO

/* =========================================================
   4. VIEW KIỂM TRA DỮ LIỆU
   ========================================================= */
CREATE VIEW vw_user_profiles AS
SELECT
    id,
    user_id,
    full_name,
    phone,
    address,
    date_of_birth,
    avatar_url,
    created_at,
    updated_at
FROM user_profiles;
GO

/* =========================================================
   5. TEST QUERY
   ========================================================= */
SELECT * FROM user_profiles;
SELECT * FROM vw_user_profiles;
GO