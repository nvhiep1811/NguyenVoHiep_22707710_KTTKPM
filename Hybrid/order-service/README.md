# Order Service

REST service for orders plus RabbitMQ producer/consumer.

## LAN

- Host: `192.168.1.12`
- Port: `8082`
- MongoDB Atlas database: `food_delivery_order_db`
- Collection: `orders`
- RabbitMQ: `192.168.1.100:5672`

## Environment

```bash
ORDER_MONGODB_URI=mongodb+srv://USER:PASS@CLUSTER/food_delivery_order_db?retryWrites=true&w=majority
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

## REST APIs

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/orders` | Create order, publish `ORDER_CREATED` |
| `GET` | `/api/orders` | List all orders |
| `GET` | `/api/orders/{id}` | Get order |
| `GET` | `/api/orders/user/{userId}` | List user orders |

## Events

Publishes:

- `ORDER_CREATED` to exchange `food.delivery.exchange`, routing key `order.created`

Consumes:

- `PAYMENT_SUCCESS` from `payment.success.order.queue`
- `PAYMENT_FAILED` from `payment.failed.order.queue`

On payment success, order is updated to `PAID/SUCCESS`.
On payment failed, order is updated to `PAYMENT_FAILED/FAILED`.
