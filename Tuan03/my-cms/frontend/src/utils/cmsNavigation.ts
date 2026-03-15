import type { PageId } from '../types/cms';

export interface NavItemConfig {
  id: PageId;
  label: string;
  short: string;
}

export interface NavGroup {
  section: string;
  items: NavItemConfig[];
}

export const PAGE_TITLES: Record<PageId, string> = {
  dashboard: 'Dashboard',
  posts: 'Bài viết',
  'new-post': 'Tạo bài viết mới',
  users: 'Quản lý người dùng',
  roles: 'Phân quyền',
  plugins: 'Plugin Runtime',
};

export const NAV_GROUPS: NavGroup[] = [
  {
    section: 'Tổng quan',
    items: [{ id: 'dashboard', label: 'Dashboard', short: 'DB' }],
  },
  {
    section: 'Nội dung',
    items: [
      { id: 'posts', label: 'Bài viết', short: 'PO' },
      { id: 'new-post', label: 'Tạo bài mới', short: 'NW' },
    ],
  },
  {
    section: 'Người dùng',
    items: [
      { id: 'users', label: 'Quản lý user', short: 'US' },
      { id: 'roles', label: 'Phân quyền', short: 'RL' },
    ],
  },
  {
    section: 'Hệ thống',
    items: [{ id: 'plugins', label: 'Plugins', short: 'PL' }],
  },
];
