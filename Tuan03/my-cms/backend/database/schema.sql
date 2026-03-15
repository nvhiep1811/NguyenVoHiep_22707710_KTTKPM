CREATE DATABASE IF NOT EXISTS microkernel_cms
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE microkernel_cms;

CREATE TABLE IF NOT EXISTS posts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  author_name VARCHAR(120) NOT NULL,
  category VARCHAR(120) NOT NULL,
  status ENUM('draft', 'review', 'published') NOT NULL DEFAULT 'draft',
  excerpt TEXT NOT NULL,
  meta_description TEXT NOT NULL,
  content LONGTEXT NOT NULL,
  views INT NOT NULL DEFAULT 0,
  layout_name VARCHAR(120) NOT NULL DEFAULT 'article-story',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  initials VARCHAR(12) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
  role_slug ENUM('admin', 'editor', 'viewer') NOT NULL,
  status ENUM('active', 'inactive') NOT NULL DEFAULT 'active',
  last_login_text VARCHAR(120) NOT NULL,
  avatar_bg VARCHAR(16) NOT NULL,
  avatar_color VARCHAR(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS plugins (
  slug VARCHAR(80) PRIMARY KEY,
  name VARCHAR(160) NOT NULL,
  description TEXT NOT NULL,
  icon_label VARCHAR(20) NOT NULL,
  accent_color VARCHAR(16) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT FALSE,
  version VARCHAR(40) NOT NULL,
  category_name VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS activity_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  message VARCHAR(255) NOT NULL,
  event_type VARCHAR(120) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO posts
  (id, title, author_name, category, status, excerpt, meta_description, content, views, layout_name, created_at)
VALUES
  (
    1,
    'Giới thiệu về CMS microkernel',
    'Admin',
    'Tin tức',
    'published',
    'Bản giới thiệu kiến trúc microkernel cho CMS mới.',
    'Bản giới thiệu kiến trúc microkernel cho CMS mới với core services, plugin manager và MariaDB.',
    'CMS được xây dựng theo kiến trúc microkernel để giữ core nhỏ, dễ mở rộng bằng plugin.',
    840,
    'article-story',
    '2026-03-14 09:00:00'
  ),
  (
    2,
    'Hướng dẫn cài đặt plugin',
    'Editor 1',
    'Hướng dẫn',
    'review',
    'Các bước đăng ký plugin vào kernel và hook bus.',
    'Các bước đăng ký plugin vào kernel và hook bus để mở rộng hệ thống.',
    'Plugin được cài qua plugin manager, sau đó đăng ký hook để can thiệp vào vòng đời nội dung.',
    420,
    'guide-stepper',
    '2026-03-13 15:30:00'
  ),
  (
    3,
    'Cập nhật tính năng Q1',
    'Admin',
    'Tin tức',
    'draft',
    'Bản nháp cập nhật tính năng quý I.',
    'Bản nháp cập nhật tính năng quý I của nền tảng CMS.',
    'Bản nháp này tổng hợp các tính năng dự kiến triển khai trong quý I.',
    190,
    'article-story',
    '2026-03-12 10:15:00'
  ),
  (
    4,
    'Tài liệu API v2.0',
    'Dev',
    'Kỹ thuật',
    'published',
    'Mô tả contract API giữa React UI và CMS core.',
    'Mô tả contract API giữa React UI và CMS core cho phiên bản 2.0.',
    'Tài liệu API trình bày rõ dashboard, posts, users, roles và plugin runtime.',
    1020,
    'docs-clean',
    '2026-03-10 08:45:00'
  )
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  author_name = VALUES(author_name),
  category = VALUES(category),
  status = VALUES(status),
  excerpt = VALUES(excerpt),
  meta_description = VALUES(meta_description),
  content = VALUES(content),
  views = VALUES(views),
  layout_name = VALUES(layout_name),
  created_at = VALUES(created_at);

INSERT INTO users
  (id, name, initials, email, role_slug, status, last_login_text, avatar_bg, avatar_color)
VALUES
  (1, 'Admin', 'AD', 'admin@cms.io', 'admin', 'active', 'Hôm nay', '#d7efe5', '#0f5a46'),
  (2, 'Editor 1', 'E1', 'editor1@cms.io', 'editor', 'active', '2 giờ trước', '#e8ecff', '#3a4bbf'),
  (3, 'Dev', 'DV', 'dev@cms.io', 'editor', 'active', 'Hôm qua', '#f2ead7', '#80520e'),
  (4, 'Viewer 1', 'V1', 'viewer1@cms.io', 'viewer', 'inactive', '1 tuần trước', '#f0ece8', '#59524b')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  initials = VALUES(initials),
  email = VALUES(email),
  role_slug = VALUES(role_slug),
  status = VALUES(status),
  last_login_text = VALUES(last_login_text),
  avatar_bg = VALUES(avatar_bg),
  avatar_color = VALUES(avatar_color);

INSERT INTO plugins
  (slug, name, description, icon_label, accent_color, enabled, version, category_name)
VALUES
  (
    'seo',
    'SEO Plugin',
    'Tối ưu meta description, sitemap và structured data.',
    'SEO',
    '#d7efe5',
    TRUE,
    '1.2.0',
    'content'
  ),
  (
    'analytics',
    'Analytics Plugin',
    'Theo dõi traffic, lượt xem và hành vi nội dung.',
    'ANA',
    '#e8ecff',
    TRUE,
    '2.0.1',
    'insight'
  ),
  (
    'builder',
    'Page Builder Plugin',
    'Áp layout block-based cho landing page và bài viết dài.',
    'BLD',
    '#f2ead7',
    TRUE,
    '0.9.4',
    'experience'
  )
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  description = VALUES(description),
  icon_label = VALUES(icon_label),
  accent_color = VALUES(accent_color),
  enabled = VALUES(enabled),
  version = VALUES(version),
  category_name = VALUES(category_name);

INSERT INTO activity_log
  (id, message, event_type, created_at)
VALUES
  (1, 'Admin đăng nhập vào dashboard', 'auth.login', '2026-03-15 08:00:00'),
  (2, 'SEO Plugin đã được bật', 'plugin.toggle', '2026-03-15 07:10:00'),
  (3, 'Bài viết mới được gửi chờ duyệt', 'content.created', '2026-03-15 06:15:00'),
  (4, 'Backup MariaDB hoàn tất', 'system.backup', '2026-03-15 03:40:00')
ON DUPLICATE KEY UPDATE
  message = VALUES(message),
  event_type = VALUES(event_type),
  created_at = VALUES(created_at);
