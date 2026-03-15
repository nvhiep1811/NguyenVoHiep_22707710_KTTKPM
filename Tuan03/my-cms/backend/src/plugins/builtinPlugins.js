function summarizeContent(content) {
  const compact = content.replace(/\s+/g, ' ').trim();
  if (!compact) {
    return 'Mô tả sẽ được sinh tự động khi nội dung được cập nhật.';
  }

  return compact.length > 150 ? `${compact.slice(0, 147)}...` : compact;
}

export const builtinPlugins = [
  {
    meta: {
      slug: 'seo',
      name: 'SEO Plugin',
      description: 'Tối ưu meta description, sitemap và structured data.',
      icon: 'SEO',
      accent: '#d7efe5',
      version: '1.2.0',
      category: 'content',
      enabledByDefault: true,
      capabilities: ['Meta description', 'Sitemap', 'Structured data'],
    },
    hooks: {
      'posts:beforeCreate': async (draft) => {
        if (draft.metaDescription) {
          return draft;
        }

        return {
          ...draft,
          metaDescription: summarizeContent(draft.content || draft.title),
        };
      },
      'posts:afterCreate': async ({ post }, { repositories }) => {
        await repositories.activity.add({
          message: `SEO Plugin đã đồng bộ metadata cho "${post.title}"`,
          eventType: 'plugin.seo.sync',
        });
      },
      'dashboard:cards': async ({ repositories }) => {
        const posts = await repositories.posts.list();
        const optimized = posts.filter((post) => post.metaDescription).length;

        return {
          title: 'SEO coverage',
          value: `${optimized}/${posts.length}`,
          detail: 'Bài viết đã có meta description',
          tone: 'success',
        };
      },
    },
  },
  {
    meta: {
      slug: 'analytics',
      name: 'Analytics Plugin',
      description: 'Theo dõi traffic, lượt xem và hành vi nội dung.',
      icon: 'ANA',
      accent: '#e8ecff',
      version: '2.0.1',
      category: 'insight',
      enabledByDefault: true,
      capabilities: ['Traffic overview', 'Page views', 'Realtime events'],
    },
    hooks: {
      'posts:afterCreate': async ({ post }, { repositories }) => {
        await repositories.activity.add({
          message: `Analytics Plugin bắt đầu theo dõi bài "${post.title}"`,
          eventType: 'plugin.analytics.track',
        });
      },
      'dashboard:cards': async ({ repositories }) => {
        const posts = await repositories.posts.list();
        const totalViews = posts.reduce((sum, post) => sum + post.views, 0);

        return {
          title: 'Analytics',
          value: totalViews.toLocaleString('vi-VN'),
          detail: 'Tổng lượt xem được plugin gom từ nội dung',
          tone: 'info',
        };
      },
    },
  },
  {
    meta: {
      slug: 'builder',
      name: 'Page Builder Plugin',
      description: 'Áp layout block-based cho landing page và bài viết dài.',
      icon: 'BLD',
      accent: '#f2ead7',
      version: '0.9.4',
      category: 'experience',
      enabledByDefault: true,
      capabilities: ['Layout presets', 'Section blocks', 'Landing pages'],
    },
    hooks: {
      'posts:beforeCreate': async (draft) => {
        return {
          ...draft,
          layout: draft.layout || 'article-story',
        };
      },
      'dashboard:cards': async ({ repositories }) => {
        const posts = await repositories.posts.list();
        const layouts = new Set(posts.map((post) => post.layout));

        return {
          title: 'Page Builder',
          value: `${layouts.size} layouts`,
          detail: 'Layout presets đang được dùng trong content',
          tone: 'warning',
        };
      },
    },
  },
];
