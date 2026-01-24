require("dotenv").config();
const amqp = require("amqplib");
const readline = require("readline");

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

async function sendEmailJob(email, subject, content) {
  const connection = await amqp.connect(process.env.RABBITMQ_URL);
  const channel = await connection.createChannel();

  const queue = "email_queue";
  await channel.assertQueue(queue, { durable: true });

  const payload = { email, subject, content };

  channel.sendToQueue(queue, Buffer.from(JSON.stringify(payload)), {
    persistent: true,
  });

  console.log("📤 Đã gửi job:", payload);

  setTimeout(() => {
    connection.close();
    process.exit(0);
  }, 500);
}

rl.question("📧 Nhập email người nhận: ", (email) => {
  rl.question("📝 Nhập tiêu đề: ", (subject) => {
    rl.question("✉️ Nhập nội dung: ", (content) => {
      sendEmailJob(email, subject, content);
      rl.close();
    });
  });
});
