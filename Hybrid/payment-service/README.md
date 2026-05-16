# Payment Service

Async payment service. It has no frontend REST API; it consumes order events and publishes payment events.

## LAN

- Host: `192.168.1.13`
- Port: `8083`
- MongoDB Atlas database: `food_delivery_payment_db`
- Collection: `payments`
- RabbitMQ: `192.168.1.100:5672`

## Environment

```bash
PAYMENT_MONGODB_URI=mongodb+srv://USER:PASS@CLUSTER/food_delivery_payment_db?retryWrites=true&w=majority
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

- `ORDER_CREATED` from `order.created.payment.queue`

Processing:

- Create payment record with `PENDING`
- Random result: 80% `SUCCESS`, 20% `FAILED`
- On success, create transaction code `PAY-yyyyMMddHHmmss-random`, save payment, publish `PAYMENT_SUCCESS`
- On failed, save reason `Random payment failed`, publish `PAYMENT_FAILED`

Publishes:

- `PAYMENT_SUCCESS` with routing key `payment.success`
- `PAYMENT_FAILED` with routing key `payment.failed`
