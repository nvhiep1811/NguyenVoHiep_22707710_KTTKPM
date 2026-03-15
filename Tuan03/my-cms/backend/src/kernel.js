import { createAuthService } from './core/authService.js';
import { createContentService } from './core/contentService.js';
import { createDashboardService } from './core/dashboardService.js';
import { createDataAccess } from './data/createDataAccess.js';
import { createMariaDbClient } from './data/mariaDbClient.js';
import { createHookBus } from './extension/hookBus.js';
import { builtinPlugins } from './plugins/builtinPlugins.js';
import { createPluginManager } from './plugins/pluginManager.js';

function readConfig() {
  return {
    port: Number(process.env.PORT ?? 4000),
    database: {
      enabled: process.env.DB_ENABLED === 'true',
      host: process.env.DB_HOST ?? '127.0.0.1',
      port: Number(process.env.DB_PORT ?? 3306),
      database: process.env.DB_NAME ?? 'microkernel_cms',
      user: process.env.DB_USER ?? 'root',
      password: process.env.DB_PASSWORD ?? '',
    },
  };
}

export async function createKernel() {
  const config = readConfig();
  const hooks = createHookBus();
  const client = await createMariaDbClient(config.database);
  const dataAccess = createDataAccess({ client });

  const services = {};

  services.content = createContentService({
    repositories: dataAccess.repositories,
    hooks,
  });

  services.auth = createAuthService({
    repositories: dataAccess.repositories,
  });

  const pluginManager = createPluginManager({
    hooks,
    repositories: dataAccess.repositories,
    services,
    dataMode: dataAccess.mode,
  });

  services.dashboard = createDashboardService({
    repositories: dataAccess.repositories,
    hooks,
    pluginManager,
  });

  await pluginManager.install(builtinPlugins);

  return {
    port: config.port,
    dataMode: dataAccess.mode,
    services,
    pluginManager,
    async shutdown() {
      await client.close();
    },
  };
}
