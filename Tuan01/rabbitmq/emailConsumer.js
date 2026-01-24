require("dotenv").config();
const amqp = require("amqplib");
const nodemailer = require("nodemailer");

async function startEmailWorker() {
  const connection = await amqp.connect(process.env.RABBITMQ_URL);
  const channel = await connection.createChannel();

  const queue = "email_queue";
  await channel.assertQueue(queue, { durable: true });
  channel.prefetch(1);

  console.log("📧 Email worker đang chạy...");

  const transporter = nodemailer.createTransport({
    service: process.env.EMAIL_SERVICE,
    auth: {
      user: process.env.EMAIL_USER,
      pass: process.env.EMAIL_PASS,
    },
  });

  channel.consume(queue, async (msg) => {
    if (!msg) return;

    let data;

    // 1️⃣ Parse JSON an toàn
    try {
      data = JSON.parse(msg.content.toString());
    } catch (err) {
      console.error("❌ JSON không hợp lệ:", msg.content.toString());
      channel.ack(msg); // ❗ bỏ message
      return;
    }

    // 2️⃣ Validate email
    if (!data.email || !data.email.includes("@")) {
      console.error("❌ Email không hợp lệ:", data.email);
      channel.ack(msg); // ❗ KHÔNG retry
      return;
    }

    try {
      console.log("📥 Đang gửi mail tới:", data.email);

      await transporter.sendMail({
        from: `"${process.env.EMAIL_FROM_NAME}" <${process.env.EMAIL_USER}>`,
        to: data.email,
        subject: data.subject || "(No subject)",
        text: data.content || "",
      });

      console.log("✅ Gửi email thành công");
      channel.ack(msg);
    } catch (err) {
      console.error("❌ Lỗi SMTP:", err.message);

      // 3️⃣ Retry CHỈ khi là lỗi SMTP / network
      channel.nack(msg, false, true);
    }
  });
}

startEmailWorker();
