# Movie Ticket Event-Driven Services

Spring Boot implementation for:

- `booking-service`: write `booking_db.bookings`, publish `BOOKING_CREATED`, and update booking status from payment result events.
- `payment-service`: consume `BOOKING_CREATED`, write `payment_db.payments`, publish `PAYMENT_COMPLETED` or `BOOKING_FAILED`.
- `notification-service`: consume payment result events, write `notification_db.notifications`, and store consumed events in `notification_db.events`.

## Tech stack

- Java 17
- Spring Boot 3.3.4
- Spring Data MongoDB
- Spring Kafka
- Spring Cloud Gateway
- MongoDB
- Kafka

## Run infrastructure

Copy `.env.example` to `.env`, then fill your MongoDB Atlas username, URL-encoded password, and cluster host.

The services use MongoDB Atlas through:

- `USER_MONGODB_URI`
- `MOVIE_MONGODB_URI`
- `BOOKING_MONGODB_URI`
- `PAYMENT_MONGODB_URI`
- `NOTIFICATION_MONGODB_URI`

Kafka can still run locally with Docker:

```powershell
docker compose up -d kafka
```

This project uses the official Apache Kafka Docker image: `apache/kafka:3.7.2`.
Kafka exposes `localhost:9092` for services started with Maven and `kafka:9092` for services started inside Docker.

Local MongoDB is optional and disabled by default. Start it only when you explicitly want the local fallback:

```powershell
docker compose --profile local-mongo up -d mongodb
```

## Run services

```powershell
mvn -pl user-service spring-boot:run
mvn -pl gateway-service spring-boot:run
mvn -pl movie-service spring-boot:run
mvn -pl booking-service spring-boot:run
mvn -pl payment-service spring-boot:run
mvn -pl notification-service spring-boot:run
```

Default ports:

- Frontend via Gateway: `http://localhost:8080`
- Frontend direct: `http://localhost:3000`
- API Gateway health: `http://localhost:8080/health`
- Payment Service: `http://localhost:8083`
- Notification Service: `http://localhost:8084`
- Booking Service: `http://localhost:8085`
- User Service: `http://localhost:8081`
- Movie Service: `http://localhost:8082`
- MongoDB: Atlas URI from `.env`
- Kafka: `localhost:9092`

When running with Docker Compose, open the app through `http://localhost:8080`. `gateway-service` is a Spring Cloud Gateway app. It serves the React frontend for non-API routes and proxies `/api/...` to backend services.
The Docker frontend is built with `VITE_API_BASE=http://localhost:8080/api` by default.

Run the full system:

```powershell
mvn -q package -DskipTests
docker compose up -d --build
```

## Run on 5 LAN machines

Use the env templates in `deployment/lan` when splitting the system across machines:

- Machine 1: Frontend + Spring Cloud Gateway + Kafka
- Machine 2: User Service
- Machine 3: Movie Service
- Machine 4: Booking Service
- Machine 5: Payment Service + Notification Service

Start from [deployment/lan/README.md](deployment/lan/README.md). The main values to edit are `KAFKA_ADVERTISED_HOST`, `KAFKA_BOOTSTRAP_SERVERS`, `VITE_API_BASE`, `*_SERVICE_URI`, `*_HOST_PORT`, and `CORS_ALLOWED_ORIGIN_PATTERNS`.

## Demo with Booking Service

Create a booking. The service stores a `PENDING` booking and publishes `BOOKING_CREATED`.

```powershell
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8085/api/bookings `
  -ContentType 'application/json' `
  -Body '{
    "userId": "user-001",
    "movieId": "movie-001",
    "showTimeId": "showtime-001",
    "movieTitle": "Demo Movie",
    "seats": 2,
    "totalPrice": 150000
  }'
```

Check booking history:

```powershell
Invoke-RestMethod http://localhost:8085/api/bookings/users/user-001
```

## Demo without Booking Service

Call Payment Service simulate endpoint. It processes the booking-created payload and publishes a result event.

```powershell
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8083/api/payments/simulate `
  -ContentType 'application/json' `
  -Body '{
    "bookingId": "booking-001",
    "userId": "user-001",
    "movieId": "movie-001",
    "totalPrice": 150000,
    "movieTitle": "Demo Movie"
  }'
```

Check payment:

```powershell
Invoke-RestMethod http://localhost:8083/api/payments/booking/booking-001
```

Check notifications:

```powershell
Invoke-RestMethod http://localhost:8084/api/notifications/users/user-001
```

Check event store:

```powershell
Invoke-RestMethod http://localhost:8084/api/events
```

Realtime event stream:

```powershell
curl.exe -N http://localhost:8084/api/events/stream
```

Filter event store by type:

```powershell
Invoke-RestMethod http://localhost:8084/api/events/types/PAYMENT_COMPLETED
```

## Topics

- `BOOKING_CREATED`
- `PAYMENT_COMPLETED`
- `BOOKING_FAILED`
- `USER_REGISTERED`

Each event topic also has a dead-letter topic:

- `BOOKING_CREATED.DLQ`
- `PAYMENT_COMPLETED.DLQ`
- `BOOKING_FAILED.DLQ`
- `USER_REGISTERED.DLQ`

## Bonus Features

- Dead Letter Queue: Kafka consumers use `DefaultErrorHandler` + `DeadLetterPublishingRecoverer`; failed messages move to `<topic>.DLQ`.
- Retry mechanism: consumer retry is configurable with `KAFKA_RETRY_MAX_ATTEMPTS` and `KAFKA_RETRY_BACKOFF_MS`.
- Event log: Notification Service persists consumed and DLQ events in `notification_db.events`.
- Realtime dashboard: the Events page consumes `/api/events/stream` using Server-Sent Events and updates live.
- API Gateway: `gateway-service` uses Spring Cloud Gateway and proxies all `/api/**` traffic while forwarding frontend routes to the React container.

## Events collection

Notification Service stores consumed events in `notification_db.events`:

- `eventType`
- `source`
- `payload`
- `status`
- `timestamp`

DLQ events are stored with `status = DEAD_LETTER`.

Payment Service publishes result events with this envelope:

```json
{
  "eventType": "PAYMENT_COMPLETED",
  "source": "payment-service",
  "payload": {
    "paymentId": "...",
    "bookingId": "...",
    "userId": "...",
    "amount": 150000
  },
  "status": "PUBLISHED",
  "timestamp": "2026-05-08T07:00:00Z"
}
```

Consumers still read JSON as plain string and unwrap `payload` manually, so they remain compatible with simple payload-only events produced by other services.

## MongoDB indexes

Spring Data MongoDB auto index creation is enabled in both services.

Payment Service:

- `payments.bookingId` unique index
- `payments.userId + createdAt` compound index for user payment history

Booking Service:

- `bookings.userId` index
- `bookings.status` index
- `bookings.userId + createdAt` compound index for user booking history

Notification Service:

- `notifications.userId` index
- `notifications.isRead` index
- `notifications.userId + isRead + createdAt` compound index for unread notifications
- `events.eventType + timestamp` compound index for audit queries
