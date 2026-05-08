# Movie Ticket Event-Driven Services

Spring Boot implementation for:

- `payment-service`: consume `BOOKING_CREATED`, write `payment_db.payments`, publish `PAYMENT_COMPLETED` or `BOOKING_FAILED`.
- `notification-service`: consume payment result events, write `notification_db.notifications`, and store consumed events in `notification_db.events`.

## Tech stack

- Java 17
- Spring Boot 3.3.4
- Spring Data MongoDB
- Spring Kafka
- MongoDB
- Kafka

## Run infrastructure

Copy `.env.example` to `.env`, then fill your MongoDB Atlas username, URL-encoded password, and cluster host.

The services use MongoDB Atlas through:

- `PAYMENT_MONGODB_URI`
- `NOTIFICATION_MONGODB_URI`

Kafka can still run locally with Docker:

```powershell
docker compose up -d kafka
```

This project uses the official Apache Kafka Docker image: `apache/kafka:3.7.2`.

## Run services

```powershell
mvn -pl payment-service spring-boot:run
mvn -pl notification-service spring-boot:run
```

Default ports:

- Payment Service: `http://localhost:8083`
- Notification Service: `http://localhost:8084`
- MongoDB: Atlas URI from `.env`
- Kafka: `localhost:9092`

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

Filter event store by type:

```powershell
Invoke-RestMethod http://localhost:8084/api/events/types/PAYMENT_COMPLETED
```

## Topics

- `BOOKING_CREATED`
- `PAYMENT_COMPLETED`
- `BOOKING_FAILED`

## Events collection

Notification Service stores consumed events in `notification_db.events`:

- `eventType`
- `source`
- `payload`
- `status`
- `timestamp`

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

Notification Service:

- `notifications.userId` index
- `notifications.isRead` index
- `notifications.userId + isRead + createdAt` compound index for unread notifications
- `events.eventType + timestamp` compound index for audit queries
