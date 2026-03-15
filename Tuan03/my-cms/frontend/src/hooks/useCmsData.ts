import { useEffect, useState } from 'react';

import { cmsApi } from '../services/cmsApi';
import type {
  CreatePostInput,
  DashboardPayload,
  PluginManifest,
  PostRecord,
  RolesPayload,
  UserRecord,
} from '../types/cms';

interface CmsDataState {
  dashboard: DashboardPayload | null;
  posts: PostRecord[];
  users: UserRecord[];
  roles: RolesPayload | null;
  plugins: PluginManifest[];
  loading: boolean;
  error: string | null;
}

const initialState: CmsDataState = {
  dashboard: null,
  posts: [],
  users: [],
  roles: null,
  plugins: [],
  loading: true,
  error: null,
};

export function useCmsData() {
  const [state, setState] = useState<CmsDataState>(initialState);
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let active = true;

    async function loadAll() {
      setState((previous) => ({
        ...previous,
        loading: true,
        error: null,
      }));

      try {
        const [dashboard, posts, users, roles, plugins] = await Promise.all([
          cmsApi.fetchDashboard(),
          cmsApi.fetchPosts(),
          cmsApi.fetchUsers(),
          cmsApi.fetchRoles(),
          cmsApi.fetchPlugins(),
        ]);

        if (!active) {
          return;
        }

        setState({
          dashboard,
          posts,
          users,
          roles,
          plugins,
          loading: false,
          error: null,
        });
      } catch (error) {
        if (!active) {
          return;
        }

        setState((previous) => ({
          ...previous,
          loading: false,
          error: error instanceof Error ? error.message : 'Đã xảy ra lỗi tải dữ liệu.',
        }));
      }
    }

    void loadAll();

    return () => {
      active = false;
    };
  }, [reloadKey]);

  async function createPost(payload: CreatePostInput) {
    const createdPost = await cmsApi.createPost(payload);

    setState((previous) => ({
      ...previous,
      posts: [createdPost, ...previous.posts],
    }));

    setReloadKey((value) => value + 1);
    return createdPost;
  }

  async function togglePlugin(slug: string) {
    const updatedPlugin = await cmsApi.togglePlugin(slug);

    setState((previous) => ({
      ...previous,
      plugins: previous.plugins.map((plugin) => {
        return plugin.slug === updatedPlugin.slug ? updatedPlugin : plugin;
      }),
    }));

    setReloadKey((value) => value + 1);
    return updatedPlugin;
  }

  function refresh() {
    setReloadKey((value) => value + 1);
  }

  return {
    ...state,
    createPost,
    togglePlugin,
    refresh,
  };
}
