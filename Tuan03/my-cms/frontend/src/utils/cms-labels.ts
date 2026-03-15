import type { PostStatus, UserRole } from '../types/cms';

export const STATUS_TEXT: Record<PostStatus, string> = {
  draft: 'Nháp',
  review: 'Chờ duyệt',
  published: 'Đã đăng',
};

export const ROLE_TEXT: Record<UserRole, string> = {
  admin: 'Admin',
  editor: 'Editor',
  viewer: 'Viewer',
};

export const CATEGORY_OPTIONS = [
  'Tin tức',
  'Hướng dẫn',
  'Kỹ thuật',
  'Landing page',
];

export const LAYOUT_OPTIONS = [
  'article-story',
  'guide-stepper',
  'docs-clean',
  'landing-feature',
];
