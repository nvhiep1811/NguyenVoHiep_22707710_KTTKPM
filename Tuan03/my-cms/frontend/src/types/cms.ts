export type PageId =
  | 'dashboard'
  | 'posts'
  | 'new-post'
  | 'users'
  | 'roles'
  | 'plugins';

export type PostStatus = 'draft' | 'review' | 'published';
export type UserRole = 'admin' | 'editor' | 'viewer';
export type UserStatus = 'active' | 'inactive';
export type PluginTone = 'success' | 'info' | 'warning' | 'neutral';

export interface DashboardMetric {
  label: string;
  value: number | string;
  delta: string;
}

export interface PluginCard {
  title: string;
  value: string;
  detail: string;
  tone: PluginTone;
}

export interface ActivityItem {
  id: number;
  message: string;
  eventType: string;
  createdAt: string;
  relativeTime: string;
}

export interface DashboardPayload {
  metrics: DashboardMetric[];
  pluginCards: PluginCard[];
  activity: ActivityItem[];
}

export interface PostRecord {
  id: number;
  title: string;
  author: string;
  category: string;
  status: PostStatus;
  excerpt: string;
  metaDescription: string;
  content: string;
  views: number;
  layout: string;
  date: string;
  createdAt: string;
}

export interface CreatePostInput {
  title: string;
  category: string;
  status: PostStatus;
  content: string;
  metaDescription: string;
  layout: string;
}

export interface UserRecord {
  id: number;
  name: string;
  initials: string;
  email: string;
  role: UserRole;
  status: UserStatus;
  lastLogin: string;
  avatarBg: string;
  avatarColor: string;
}

export interface RoleSummary {
  slug: UserRole;
  label: string;
  permissionsLabel: string;
  userCount: number;
}

export interface PermissionRow {
  label: string;
  admin: boolean;
  editor: boolean;
  viewer: boolean;
}

export interface RolesPayload {
  summary: RoleSummary[];
  permissions: PermissionRow[];
}

export interface PluginManifest {
  slug: string;
  name: string;
  description: string;
  icon: string;
  accent: string;
  enabled: boolean;
  version: string;
  category: string;
  capabilities: string[];
  hookCount: number;
}
