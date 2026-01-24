const amqp = require("amqplib");
const queueName = "hello";

async function receive() {
  let connection;
  try {
    connection = await amqp.connect("amqp://localhost");
    const channel = await connection.createChannel();

    await channel.assertQueue(queueName, { durable: false });

    console.log(
      `[*] Waiting for messages in ${queueName}. To exit, press CTRL+C`
    );

    channel.consume(
      queueName,
      (msg) => {
        console.log(`[x] Received "${msg.content.toString()}"`);
      },
      {
        noAck: true,
      }
    );
  } catch (err) {
    console.error(err);
    if (connection) {
      connection.close();
    }
  }
}

receive();
