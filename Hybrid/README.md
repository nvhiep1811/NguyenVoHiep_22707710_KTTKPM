# Food Delivery System Mini - Hybrid Microservices + Event-Driven Architecture

He thong gom 5 Spring Boot service rieng, 1 frontend React/Vite, MongoDB rieng cho tung service, va RabbitMQ cho event bat dong bo.

## 1. Kien truc he thong

```text
React Frontend (192.168.1.14:3000)
        |
        | REST only
        v
API Gateway (192.168.1.10:8080)
        |
        | REST
        +--> User/Food Service (192.168.1.11:8081) --> MongoDB: food_delivery_user_food_db
        |
        +--> Order Service (192.168.1.12:8082) ------> MongoDB: food_delivery_order_db
                    |
                    | publish ORDER_CREATED
                    v
RabbitMQ TopicExchange: food.delivery.exchange (192.168.1.100)
                    |
                    +--> Payment Service (192.168.1.13:8083) --> MongoDB: food_delivery_payment_db
                    |          |
                    |          | publish PAYMENT_SUCCESS / PAYMENT_FAILED
                    |          v
                    +--> Order Service updates order status
                    |
                    +--> Notification Service (192.168.1.13:8084) --> MongoDB: food_delivery_notification_db
```

Frontend chi goi API Gateway. Payment Service va Notification Service hoat dong phia sau bang RabbitMQ event.

## 2. Service map

| Service | Project | Host | Port | Database | RabbitMQ |
| --- | --- | --- | --- | --- | --- |
| API Gateway | `api-gateway` | `192.168.1.10` | `8080` | No | No |
| User/Food | `user-food-service` | `192.168.1.11` | `8081` | `food_delivery_user_food_db` | No |
| Order | `order-service` | `192.168.1.12` | `8082` | `food_delivery_order_db` | Producer + Consumer |
| Payment | `payment-service` | `192.168.1.13` | `8083` | `food_delivery_payment_db` | Producer + Consumer |
| Notification | `notification-service` | `192.168.1.13` | `8084` | `food_delivery_notification_db` | Consumer |
| Frontend | `food-delivery-frontend` | `192.168.1.14` | `3000` | No | No |

## 3. REST flow

1. Frontend login/register qua `POST /api/auth/**` tren Gateway.
2. Frontend lay danh sach mon qua `GET /api/foods`.
3. Frontend tao don qua `POST /api/orders`.
4. Order Service luu order voi `status=CREATED`, `paymentStatus=PENDING`.
5. Order Service tra response ngay cho frontend.

## 4. Event-driven flow

1. Order Service publish `ORDER_CREATED` voi routing key `order.created`.
2. Payment Service consume tu `order.created.payment.queue`.
3. Payment Service random ket qua thanh toan: 80% success, 20% failed.
4. Payment Service publish `PAYMENT_SUCCESS` hoac `PAYMENT_FAILED`.
5. Order Service consume payment event tu queue rieng de cap nhat order.
6. Notification Service consume payment event tu queue rieng de luu notification va in console.

## 5. RabbitMQ exchange, queue, routing key

Exchange: `food.delivery.exchange` (`TopicExchange`)

| Queue | Routing key | Consumer |
| --- | --- | --- |
| `order.created.payment.queue` | `order.created` | Payment Service |
| `payment.success.order.queue` | `payment.success` | Order Service |
| `payment.failed.order.queue` | `payment.failed` | Order Service |
| `payment.success.notification.queue` | `payment.success` | Notification Service |
| `payment.failed.notification.queue` | `payment.failed` | Notification Service |

Order Service va Notification Service khong dung chung queue cho payment event. Moi service co queue rieng de ca hai cung nhan duoc `PAYMENT_SUCCESS` va `PAYMENT_FAILED`.

## 6. Chay RabbitMQ bang Docker Compose

Tren may `192.168.1.100`, chay trong thu muc root:

```bash
docker compose up -d
```

RabbitMQ ports:

- AMQP: `5672`
- Management Dashboard: `15672`

## 7. RabbitMQ Management Dashboard

Mo:

```text
http://192.168.1.100:15672
```

Dang nhap:

- Username: `admin`
- Password: `admin123`

## 8. Cau hinh MongoDB Atlas

Moi service dung MongoDB Atlas URI rieng. Dat bien moi truong tren tung may truoc khi chay:

```bash
USER_FOOD_MONGODB_URI=mongodb+srv://USER:PASS@CLUSTER/food_delivery_user_food_db?retryWrites=true&w=majority
ORDER_MONGODB_URI=mongodb+srv://USER:PASS@CLUSTER/food_delivery_order_db?retryWrites=true&w=majority
PAYMENT_MONGODB_URI=mongodb+srv://USER:PASS@CLUSTER/food_delivery_payment_db?retryWrites=true&w=majority
NOTIFICATION_MONGODB_URI=mongodb+srv://USER:PASS@CLUSTER/food_delivery_notification_db?retryWrites=true&w=majority
JWT_SECRET=replace-with-at-least-32-character-secret-key
```

Tren PowerShell:

```powershell
$env:USER_FOOD_MONGODB_URI="mongodb+srv://USER:PASS@CLUSTER/food_delivery_user_food_db?retryWrites=true&w=majority"
```

## 9. Cau hinh IP LAN bang `.env`

Moi project co san `.env.example` va `run.ps1`.

Tren tung may:

```powershell
Copy-Item .env.example .env
notepad .env
.\run.ps1
```

Sua IP theo `ipconfig` cua cac may:

| File | Bien can sua thuong gap |
| --- | --- |
| `api-gateway/.env` | `USER_FOOD_SERVICE_URL`, `ORDER_SERVICE_URL`, `FRONTEND_ORIGIN_LAN` |
| `user-food-service/.env` | `USER_FOOD_MONGODB_URI`, `JWT_SECRET` |
| `order-service/.env` | `ORDER_MONGODB_URI`, `RABBITMQ_HOST` |
| `payment-service/.env` | `PAYMENT_MONGODB_URI`, `RABBITMQ_HOST` |
| `notification-service/.env` | `NOTIFICATION_MONGODB_URI`, `RABBITMQ_HOST` |
| `food-delivery-frontend/.env` | `VITE_API_BASE_URL` |

Mac dinh cac service van dung IP trong de bai:

- Gateway: `192.168.1.10:8080`
- User/Food: `192.168.1.11:8081`
- Order: `192.168.1.12:8082`
- Payment: `192.168.1.13:8083`
- Notification: `192.168.1.13:8084`
- Frontend: `192.168.1.14:3000`
- RabbitMQ: `192.168.1.100`

## 10. Cach chay tung service tren LAN

Tren may `192.168.1.11`:

```bash
cd user-food-service
./run.ps1
```

Tren may `192.168.1.12`:

```bash
cd order-service
./run.ps1
```

Tren may `192.168.1.13`:

```bash
cd payment-service
./run.ps1
```

Mo terminal khac tren may `192.168.1.13`:

```bash
cd notification-service
./run.ps1
```

Tren may `192.168.1.10`:

```bash
cd api-gateway
./run.ps1
```

Tren may `192.168.1.14`:

```bash
cd food-delivery-frontend
npm install
./run.ps1
```

## 11. Test bang Postman

Base URL qua Gateway:

```text
http://192.168.1.10:8080
```

Register:

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "fullName": "Nguyen Van A",
  "email": "a@example.com",
  "password": "123456",
  "phone": "0900000000"
}
```

Login:

```json
{
  "email": "a@example.com",
  "password": "123456"
}
```

Create order:

```http
POST /api/orders
Content-Type: application/json
```

```json
{
  "userId": "USER_ID",
  "items": [
    {
      "foodId": "FOOD_ID",
      "foodName": "Com ga xoi mo",
      "price": 45000,
      "quantity": 2
    }
  ],
  "paymentMethod": "COD"
}
```

Get user orders:

```http
GET /api/orders/user/USER_ID
```

## 12. Kich ban demo

1. Chay RabbitMQ tren may `192.168.1.100`.
2. Mo dashboard `http://192.168.1.100:15672`, login `admin/admin123`.
3. Chay User/Food Service.
4. Chay Order Service.
5. Chay Payment Service.
6. Chay Notification Service.
7. Chay API Gateway.
8. Chay Frontend.
9. Login user.
10. Xem danh sach mon.
11. Dat mon.
12. Order Service tra response ngay: `Order created. Payment is processing asynchronously.`
13. Vao RabbitMQ Dashboard xem exchange, queue, message rates.
14. Payment Service consume `ORDER_CREATED`.
15. Payment Service publish `PAYMENT_SUCCESS` hoac `PAYMENT_FAILED`.
16. Order Service consume payment event va cap nhat order.
17. Notification Service consume payment event va in thong bao.
18. Frontend vao My Orders va bam Refresh de thay trang thai moi.
