export function createHookBus() {
  const listeners = new Map();

  function tap(eventName, handler) {
    const handlers = listeners.get(eventName) ?? [];
    handlers.push(handler);
    listeners.set(eventName, handlers);

    return () => {
      const current = listeners.get(eventName) ?? [];
      listeners.set(
        eventName,
        current.filter((candidate) => candidate !== handler),
      );
    };
  }

  async function runWaterfall(eventName, payload) {
    let current = payload;
    const handlers = listeners.get(eventName) ?? [];

    for (const handler of handlers) {
      const nextValue = await handler(current);
      if (typeof nextValue !== 'undefined') {
        current = nextValue;
      }
    }

    return current;
  }

  async function dispatch(eventName, payload) {
    const handlers = listeners.get(eventName) ?? [];

    for (const handler of handlers) {
      await handler(payload);
    }
  }

  async function collect(eventName, payload) {
    const handlers = listeners.get(eventName) ?? [];
    const results = [];

    for (const handler of handlers) {
      const value = await handler(payload);
      if (Array.isArray(value)) {
        results.push(...value);
      } else if (typeof value !== 'undefined' && value !== null) {
        results.push(value);
      }
    }

    return results;
  }

  return {
    tap,
    runWaterfall,
    dispatch,
    collect,
  };
}
