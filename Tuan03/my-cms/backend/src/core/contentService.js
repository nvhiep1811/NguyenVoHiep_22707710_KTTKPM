function createHttpError(statusCode, message) {
  const error = new Error(message);
  error.statusCode = statusCode;
  return error;
}

function normalizeText(value) {
  return typeof value === 'string' ? value.trim() : '';
}

export function createContentService({ repositories, hooks }) {
  return {
    async listPosts() {
      return repositories.posts.list();
    },
    async createPost(payload) {
      const title = normalizeText(payload.title);
      const content = normalizeText(payload.content);

      if (!title) {
        throw createHttpError(400, 'Tiêu đề bài viết là bắt buộc.');
      }

      const draft = {
        title,
        category: normalizeText(payload.category) || 'Tin tức',
        status: payload.status ?? 'draft',
        content,
        metaDescription: normalizeText(payload.metaDescription),
        author: 'Admin',
        views: 0,
        layout: normalizeText(payload.layout) || 'article-story',
      };

      const hydratedDraft = await hooks.runWaterfall('posts:beforeCreate', draft);
      const createdPost = await repositories.posts.create(hydratedDraft);

      await repositories.activity.add({
        message: `Bài viết "${createdPost.title}" đã được tạo`,
        eventType: 'content.created',
      });

      await hooks.dispatch('posts:afterCreate', { post: createdPost });

      return createdPost;
    },
  };
}
