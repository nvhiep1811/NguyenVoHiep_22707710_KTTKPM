import { Router } from 'express';

function safe(handler) {
  return async function safeHandler(request, response, next) {
    try {
      await handler(request, response, next);
    } catch (error) {
      next(error);
    }
  };
}

export function registerRoutes(app, controller) {
  const router = Router();

  router.get('/health', safe(controller.getHealth));
  router.get('/dashboard', safe(controller.getDashboard));
  router.get('/posts', safe(controller.getPosts));
  router.post('/posts', safe(controller.createPost));
  router.get('/users', safe(controller.getUsers));
  router.get('/roles', safe(controller.getRoles));
  router.get('/plugins', safe(controller.getPlugins));
  router.patch('/plugins/:slug/toggle', safe(controller.togglePlugin));

  app.use('/api', router);

  app.use((request, response) => {
    response.status(404).json({
      message: `Không tìm thấy route ${request.method} ${request.originalUrl}.`,
    });
  });

  app.use((error, _request, response, _next) => {
    const statusCode = error.statusCode ?? 500;
    const message = error.message ?? 'Đã xảy ra lỗi không mong muốn.';

    if (statusCode >= 500) {
      console.error('[cms-core] unexpected error', error);
    }

    response.status(statusCode).json({ message });
  });
}
