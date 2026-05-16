# Notification Service

Async notification service. It has no frontend REST API; it consumes payment events.

## LAN

- Host: `192.168.1.13`
- Port: `8084`
- MongoDB Atlas database: `food_delivery_notification_db`
- Collection: `notifications`
- RabbitMQ: `192.168.1.100:5672`

## Environment

```bash
NOTIFICATION_MONGODB_URI=mongodb+srv://USER:PASS@CLUSTER/food_delivery_notification_db?retryWrites=true&w=majority
RABBITMQ_HOST=192.168.1.100
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin123
```

## Run

Copy `.env.example` to `.env`, then set your MongoDB Atlas URI and RabbitMQ IP:

```powershell
Copy-Item .env.example .env
notepad .env
.\run.ps1
```

Or run directly:

```bash
mvn spring-boot:run
```

## Event behavior

Consumes:

- `PAYMENT_SUCCESS` from `payment.success.notification.queue`
- `PAYMENT_FAILED` from `payment.failed.notification.queue`

On success it saves:

- title: `Thanh toán thành công`
- message: `Đơn hàng #{orderId} đã thanh toán thành công!`
- type: `PAYMENT_SUCCESS`
- read: `false`

On failed it saves:

- title: `Thanh toán thất bại`
- message: `Đơn hàng #{orderId} thanh toán thất bại!`
- type: `PAYMENT_FAILED`
- read: `false`
