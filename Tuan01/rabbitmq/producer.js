const amqp = require("amqplib");
const queueName = "hello";

async function send() {
  let connection;
  try {
    connection = await amqp.connect("amqp://localhost");
    const channel = await connection.createChannel();

    await channel.assertQueue(queueName, { durable: false });

    const msg = "Hello Hiep with RabbitMQ from Node.js!";

    channel.sendToQueue(queueName, Buffer.from(msg));
    console.log(`[x] Sent "${msg}"`);

    await channel.close();
  } catch (err) {
    console.error(err);
  } finally {
    if (connection) {
      await connection.close();
    }
  }
}

send();
