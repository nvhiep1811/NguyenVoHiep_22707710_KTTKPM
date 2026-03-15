export function createCmsController({ services, pluginManager, dataMode }) {
  return {
    async getHealth(_request, response) {
      response.json({
        status: 'ok',
        runtime: 'microkernel',
        dataSource: dataMode === 'mariadb' ? 'MariaDB runtime' : dataMode,
        timestamp: new Date().toISOString(),
      });
    },
    async getDashboard(_request, response) {
      response.json(await services.dashboard.getOverview());
    },
    async getPosts(_request, response) {
      response.json(await services.content.listPosts());
    },
    async createPost(request, response) {
      const createdPost = await services.content.createPost(request.body);
      response.status(201).json(createdPost);
    },
    async getUsers(_request, response) {
      response.json(await services.auth.listUsers());
    },
    async getRoles(_request, response) {
      response.json(await services.auth.getRoleMatrix());
    },
    async getPlugins(_request, response) {
      response.json(await pluginManager.listPlugins());
    },
    async togglePlugin(request, response) {
      response.json(await pluginManager.togglePlugin(request.params.slug));
    },
  };
}
