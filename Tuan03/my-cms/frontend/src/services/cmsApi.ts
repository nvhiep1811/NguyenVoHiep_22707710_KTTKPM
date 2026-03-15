import type {
  CreatePostInput,
  DashboardPayload,
  PluginManifest,
  PostRecord,
  RolesPayload,
  UserRecord,
} from '../types/cms';

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? '/api';

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(init?.headers ?? {}),
    },
    ...init,
  });

  if (!response.ok) {
    let message = 'Không thể kết nối hệ thống.';

    try {
      const payload = (await response.json()) as { message?: string };
      if (payload.message) {
        message = payload.message;
      }
    } catch {
      message = response.statusText || message;
    }

    throw new Error(message);
  }

  return (await response.json()) as T;
}

export const cmsApi = {
  fetchDashboard() {
    return request<DashboardPayload>('/dashboard');
  },
  fetchPosts() {
    return request<PostRecord[]>('/posts');
  },
  fetchUsers() {
    return request<UserRecord[]>('/users');
  },
  fetchRoles() {
    return request<RolesPayload>('/roles');
  },
  fetchPlugins() {
    return request<PluginManifest[]>('/plugins');
  },
  createPost(payload: CreatePostInput) {
    return request<PostRecord>('/posts', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },
  togglePlugin(slug: string) {
    return request<PluginManifest>(`/plugins/${slug}/toggle`, {
      method: 'PATCH',
    });
  },
};
