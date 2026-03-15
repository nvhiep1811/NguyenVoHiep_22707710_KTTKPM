import mysql from 'mysql2/promise';

export async function createMariaDbClient(config) {
  if (!config.enabled) {
    throw new Error('MariaDB mode is required. Set DB_ENABLED=true in backend/.env.');
  }

  const pool = mysql.createPool({
    host: config.host,
    port: config.port,
    database: config.database,
    user: config.user,
    password: config.password,
    waitForConnections: true,
    connectionLimit: 10,
  });

  try {
    await pool.query('SELECT 1');

    return {
      mode: 'mariadb',
      isReady: true,
      async query(sql, params = []) {
        return pool.query(sql, params);
      },
      async close() {
        await pool.end();
      },
    };
  } catch (error) {
    await pool.end().catch(() => {});
    throw new Error(`[cms-core] MariaDB connection failed: ${error.message}`);
  }
}
