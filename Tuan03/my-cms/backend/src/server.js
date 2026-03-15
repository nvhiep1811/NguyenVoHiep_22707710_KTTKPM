import 'dotenv/config';

import cors from 'cors';
import express from 'express';

import { registerRoutes } from './api/registerRoutes.js';
import { createCmsController } from './controllers/cmsController.js';
import { createKernel } from './kernel.js';

const app = express();
const kernel = await createKernel();
const controller = createCmsController({
  services: kernel.services,
  pluginManager: kernel.pluginManager,
  dataMode: kernel.dataMode,
});

app.use(cors());
app.use(express.json({ limit: '1mb' }));

registerRoutes(app, controller);

const server = app.listen(kernel.port, () => {
  console.log(
    `[cms-core] running at http://localhost:${kernel.port} using ${kernel.dataMode} data access`,
  );
});

async function shutdown() {
  server.close(async () => {
    await kernel.shutdown();
    process.exit(0);
  });
}

process.on('SIGINT', shutdown);
process.on('SIGTERM', shutdown);
