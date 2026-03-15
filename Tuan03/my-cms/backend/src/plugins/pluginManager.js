function createHttpError(statusCode, message) {
  const error = new Error(message);
  error.statusCode = statusCode;
  return error;
}

export function createPluginManager({ hooks, repositories, services, dataMode }) {
  const registry = new Map();

  function buildPluginContext(slug) {
    return {
      hooks,
      repositories,
      services,
      plugin: registry.get(slug),
    };
  }

  function toPluginView(persisted, runtime) {
    return {
      slug: persisted.slug,
      name: runtime.meta.name,
      description: runtime.meta.description,
      icon: runtime.meta.icon,
      accent: runtime.meta.accent,
      enabled: persisted.enabled,
      version: runtime.meta.version,
      category: runtime.meta.category,
      capabilities: runtime.meta.capabilities,
      hookCount: Object.keys(runtime.hooks).length,
    };
  }

  const manager = {
    async install(definitions) {
      for (const definition of definitions) {
        const persisted = await repositories.plugins.ensure(definition.meta);
        registry.set(definition.meta.slug, {
          meta: definition.meta,
          hooks: definition.hooks ?? {},
          enabled: persisted.enabled,
        });

        for (const [eventName, handler] of Object.entries(definition.hooks ?? {})) {
          hooks.tap(eventName, async (payload) => {
            const runtime = registry.get(definition.meta.slug);
            if (!runtime?.enabled) {
              return undefined;
            }

            return handler(payload, buildPluginContext(definition.meta.slug));
          });
        }
      }

      return manager.listPlugins();
    },
    async listPlugins() {
      const storedPlugins = await repositories.plugins.list();

      return storedPlugins
        .map((persisted) => {
          const runtime = registry.get(persisted.slug);
          if (!runtime) {
            return null;
          }

          runtime.enabled = persisted.enabled;
          return toPluginView(persisted, runtime);
        })
        .filter(Boolean)
        .sort((left, right) => left.name.localeCompare(right.name));
    },
    async togglePlugin(slug) {
      const persisted = await repositories.plugins.toggle(slug);
      if (!persisted) {
        throw createHttpError(404, `Không tìm thấy plugin "${slug}".`);
      }

      const runtime = registry.get(slug);
      if (!runtime) {
        throw createHttpError(404, `Plugin "${slug}" chưa được đăng ký vào kernel.`);
      }

      runtime.enabled = persisted.enabled;

      await repositories.activity.add({
        message: `${runtime.meta.name} ${persisted.enabled ? 'đã được bật' : 'đã được tắt'}`,
        eventType: 'plugin.toggle',
      });

      await hooks.dispatch('plugins:toggled', {
        plugin: {
          ...runtime.meta,
          enabled: runtime.enabled,
        },
      });

      return toPluginView(persisted, runtime);
    },
    getArchitectureSnapshot() {
      const plugins = Array.from(registry.values())
        .filter((plugin) => plugin.enabled)
        .map((plugin) => plugin.meta.name);

      return {
        ui: 'React UI',
        core: [
          'API / Controllers',
          'Core Services: Content + Auth',
          'Plugin Manager',
          'Extension API / Hooks',
          'Data Access',
        ],
        plugins,
        dataSource: dataMode === 'mariadb' ? 'MariaDB runtime' : dataMode,
        pluginCount: plugins.length,
      };
    },
  };

  return manager;
}
