function toDateLabel(input) {
  return new Intl.DateTimeFormat('vi-VN').format(new Date(input));
}

function toRelativeTime(input) {
  const diffMs = Date.now() - new Date(input).getTime();
  const diffMinutes = Math.max(1, Math.round(diffMs / 60000));

  if (diffMinutes < 60) {
    return `${diffMinutes} phút trước`;
  }

  const diffHours = Math.round(diffMinutes / 60);
  if (diffHours < 24) {
    return `${diffHours} giờ trước`;
  }

  const diffDays = Math.round(diffHours / 24);
  if (diffDays < 7) {
    return `${diffDays} ngày trước`;
  }

  const diffWeeks = Math.round(diffDays / 7);
  return `${diffWeeks} tuần trước`;
}

function createExcerpt(content) {
  const compact = content.replace(/\s+/g, ' ').trim();
  if (!compact) {
    return 'Nội dung đang được cập nhật.';
  }

  return compact.length > 120 ? `${compact.slice(0, 117)}...` : compact;
}

function mapPostRecord(post) {
  return {
    id: post.id,
    title: post.title,
    author: post.author,
    category: post.category,
    status: post.status,
    excerpt: post.excerpt,
    metaDescription: post.metaDescription,
    content: post.content,
    views: post.views,
    layout: post.layout,
    date: toDateLabel(post.createdAt),
    createdAt: post.createdAt,
  };
}

function mapPluginRecord(plugin) {
  return {
    slug: plugin.slug,
    name: plugin.name,
    description: plugin.description,
    icon: plugin.icon,
    accent: plugin.accent,
    enabled: plugin.enabled,
    version: plugin.version,
    category: plugin.category,
  };
}

function mapActivityRecord(entry) {
  return {
    id: entry.id,
    message: entry.message,
    eventType: entry.eventType,
    createdAt: entry.createdAt,
    relativeTime: toRelativeTime(entry.createdAt),
  };
}

export function createDataAccess({ client }) {
  async function listPostsFromDb() {
    const [rows] = await client.query(
      `SELECT
        id,
        title,
        author_name,
        category,
        status,
        excerpt,
        meta_description,
        content,
        views,
        layout_name,
        created_at
      FROM posts
      ORDER BY created_at DESC`,
    );

    return rows.map((row) =>
      mapPostRecord({
        id: row.id,
        title: row.title,
        author: row.author_name,
        category: row.category,
        status: row.status,
        excerpt: row.excerpt,
        metaDescription: row.meta_description,
        content: row.content,
        views: row.views,
        layout: row.layout_name,
        createdAt: row.created_at,
      }),
    );
  }

  async function createPostInDb(post) {
    const createdAt = new Date();
    const excerpt = createExcerpt(post.content);

    const [result] = await client.query(
      `INSERT INTO posts
        (title, author_name, category, status, excerpt, meta_description, content, views, layout_name, created_at)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        post.title,
        post.author,
        post.category,
        post.status,
        excerpt,
        post.metaDescription,
        post.content,
        post.views,
        post.layout,
        createdAt,
      ],
    );

    return mapPostRecord({
      id: result.insertId,
      title: post.title,
      author: post.author,
      category: post.category,
      status: post.status,
      excerpt,
      metaDescription: post.metaDescription,
      content: post.content,
      views: post.views,
      layout: post.layout,
      createdAt,
    });
  }

  async function listUsersFromDb() {
    const [rows] = await client.query(
      `SELECT
        id,
        name,
        initials,
        email,
        role_slug,
        status,
        last_login_text,
        avatar_bg,
        avatar_color
      FROM users
      ORDER BY id ASC`,
    );

    return rows.map((row) => ({
      id: row.id,
      name: row.name,
      initials: row.initials,
      email: row.email,
      role: row.role_slug,
      status: row.status,
      lastLogin: row.last_login_text,
      avatarBg: row.avatar_bg,
      avatarColor: row.avatar_color,
    }));
  }

  async function listPluginsFromDb() {
    const [rows] = await client.query(
      `SELECT
        slug,
        name,
        description,
        icon_label,
        accent_color,
        enabled,
        version,
        category_name
      FROM plugins
      ORDER BY name ASC`,
    );

    return rows.map((row) =>
      mapPluginRecord({
        slug: row.slug,
        name: row.name,
        description: row.description,
        icon: row.icon_label,
        accent: row.accent_color,
        enabled: Boolean(row.enabled),
        version: row.version,
        category: row.category_name,
      }),
    );
  }

  async function ensurePluginInDb(manifest) {
    const [rows] = await client.query(
      `SELECT slug FROM plugins WHERE slug = ? LIMIT 1`,
      [manifest.slug],
    );

    if (rows.length === 0) {
      await client.query(
        `INSERT INTO plugins
          (slug, name, description, icon_label, accent_color, enabled, version, category_name)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
        [
          manifest.slug,
          manifest.name,
          manifest.description,
          manifest.icon,
          manifest.accent,
          manifest.enabledByDefault,
          manifest.version,
          manifest.category,
        ],
      );
    }

    const [pluginRows] = await client.query(
      `SELECT
        slug,
        name,
        description,
        icon_label,
        accent_color,
        enabled,
        version,
        category_name
      FROM plugins
      WHERE slug = ?
      LIMIT 1`,
      [manifest.slug],
    );

    const plugin = pluginRows[0];

    return mapPluginRecord({
      slug: plugin.slug,
      name: plugin.name,
      description: plugin.description,
      icon: plugin.icon_label,
      accent: plugin.accent_color,
      enabled: Boolean(plugin.enabled),
      version: plugin.version,
      category: plugin.category_name,
    });
  }

  async function togglePluginInDb(slug) {
    await client.query(
      `UPDATE plugins
      SET enabled = NOT enabled
      WHERE slug = ?`,
      [slug],
    );

    const [rows] = await client.query(
      `SELECT
        slug,
        name,
        description,
        icon_label,
        accent_color,
        enabled,
        version,
        category_name
      FROM plugins
      WHERE slug = ?
      LIMIT 1`,
      [slug],
    );

    if (rows.length === 0) {
      return null;
    }

    const plugin = rows[0];

    return mapPluginRecord({
      slug: plugin.slug,
      name: plugin.name,
      description: plugin.description,
      icon: plugin.icon_label,
      accent: plugin.accent_color,
      enabled: Boolean(plugin.enabled),
      version: plugin.version,
      category: plugin.category_name,
    });
  }

  async function listActivityFromDb(limit = 6) {
    const [rows] = await client.query(
      `SELECT
        id,
        message,
        event_type,
        created_at
      FROM activity_log
      ORDER BY created_at DESC
      LIMIT ?`,
      [limit],
    );

    return rows.map((row) =>
      mapActivityRecord({
        id: row.id,
        message: row.message,
        eventType: row.event_type,
        createdAt: row.created_at,
      }),
    );
  }

  async function addActivityInDb(entry) {
    const createdAt = new Date();
    const [result] = await client.query(
      `INSERT INTO activity_log (message, event_type, created_at)
      VALUES (?, ?, ?)`,
      [entry.message, entry.eventType, createdAt],
    );

    return mapActivityRecord({
      id: result.insertId,
      message: entry.message,
      eventType: entry.eventType,
      createdAt,
    });
  }

  return {
    mode: client.mode,
    repositories: {
      posts: {
        list: listPostsFromDb,
        create: createPostInDb,
      },
      users: {
        list: listUsersFromDb,
      },
      plugins: {
        list: listPluginsFromDb,
        ensure: ensurePluginInDb,
        toggle: togglePluginInDb,
      },
      activity: {
        list: listActivityFromDb,
        add: addActivityInDb,
      },
    },
  };
}
