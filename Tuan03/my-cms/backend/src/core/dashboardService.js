export function createDashboardService({ repositories, hooks, pluginManager }) {
  return {
    async getOverview() {
      const [posts, users, activity, plugins] = await Promise.all([
        repositories.posts.list(),
        repositories.users.list(),
        repositories.activity.list(6),
        pluginManager.listPlugins(),
      ]);

      const totalViews = posts.reduce((sum, post) => sum + post.views, 0);
      const publishedPosts = posts.filter((post) => post.status === 'published').length;
      const activeUsers = users.filter((user) => user.status === 'active').length;
      const activePlugins = plugins.filter((plugin) => plugin.enabled);

      const pluginCards = await hooks.collect('dashboard:cards', {
        repositories,
        pluginManager,
      });
      const architecture = pluginManager.getArchitectureSnapshot();

      return {
        metrics: [
          {
            label: 'Bài viết',
            value: posts.length,
            delta: `${publishedPosts} bài đã đăng`,
          },
          {
            label: 'Người dùng',
            value: users.length,
            delta: `${activeUsers} user đang hoạt động`,
          },
          {
            label: 'Plugin đang chạy',
            value: activePlugins.length,
            delta: `${plugins.length} plugin đã cài`,
          },
          {
            label: 'Lượt xem nội dung',
            value: totalViews.toLocaleString('vi-VN'),
            delta: 'Tổng hợp từ Analytics Plugin',
          },
        ],
        pluginCards,
        activity,
        architecture,
      };
    },
  };
}
