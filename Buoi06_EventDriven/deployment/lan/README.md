# LAN deployment: 5 machines

Replace the sample `192.168.1.x` addresses in each env file before running.

## What to edit

Use these files as the per-machine source of truth:

- Machine 1: `deployment/lan/machine1-fe-gateway-kafka.env.example`
- Machine 2: `deployment/lan/machine2-user.env.example`
- Machine 3: `deployment/lan/machine3-movie.env.example`
- Machine 4: `deployment/lan/machine4-booking.env.example`
- Machine 5: `deployment/lan/machine5-payment-notification.env.example`

Important variables:

- `KAFKA_ADVERTISED_HOST`: set this on Machine 1 to the Machine 1 LAN IP. Kafka returns this address to all clients.
- `KAFKA_BOOTSTRAP_SERVERS`: set this on every machine to `<machine-1-ip>:9092`.
- `VITE_API_BASE`: set this on Machine 1 to `http://<machine-1-ip>:8080/api`.
- `USER_SERVICE_URI`, `MOVIE_SERVICE_URI`, `BOOKING_SERVICE_URI`, `PAYMENT_SERVICE_URI`, `NOTIFICATION_SERVICE_URI`: set these on Machine 1 so Gateway can reach the service machines.
- `*_HOST_PORT`: set these to the LAN-facing ports listed below.
- `CORS_ALLOWED_ORIGIN_PATTERNS`: include `http://<machine-1-ip>:8080` and `http://<machine-1-ip>:3000`.
- `MONGO_ATLAS_*`: copy the same Atlas credentials to every machine, or replace each `*_MONGODB_URI` with a complete MongoDB URI.

## Machine layout

- Machine 1: Frontend + Spring Cloud Gateway + Kafka
- Machine 2: User Service
- Machine 3: Movie Service
- Machine 4: Booking Service
- Machine 5: Payment Service + Notification Service

## Build

Run this once on each machine after pulling/copying the project:

```powershell
mvn -q package -DskipTests
```

The frontend Docker image builds its own static bundle. The Spring service Dockerfiles copy the JAR from each service `target` directory.

## Run commands

Machine 1:

```powershell
copy deployment\lan\machine1-fe-gateway-kafka.env.example deployment\lan\machine1.env
# Edit deployment\lan\machine1.env first.
docker compose --env-file deployment\lan\machine1.env up -d --build kafka frontend
docker compose --env-file deployment\lan\machine1.env up -d --build --no-deps api-gateway
```

Machine 2:

```powershell
copy deployment\lan\machine2-user.env.example deployment\lan\machine2.env
# Edit deployment\lan\machine2.env first.
docker compose --env-file deployment\lan\machine2.env up -d --build --no-deps user-service
```

Machine 3:

```powershell
copy deployment\lan\machine3-movie.env.example deployment\lan\machine3.env
# Edit deployment\lan\machine3.env first.
docker compose --env-file deployment\lan\machine3.env up -d --build --no-deps movie-service
```

Machine 4:

```powershell
copy deployment\lan\machine4-booking.env.example deployment\lan\machine4.env
# Edit deployment\lan\machine4.env first.
docker compose --env-file deployment\lan\machine4.env up -d --build --no-deps booking-service
```

Machine 5:

```powershell
copy deployment\lan\machine5-payment-notification.env.example deployment\lan\machine5.env
# Edit deployment\lan\machine5.env first.
docker compose --env-file deployment\lan\machine5.env up -d --build --no-deps payment-service notification-service
```

Use `--no-deps` on machines 2-5 so Docker Compose does not try to start Kafka locally.

## Firewall ports

Open these inbound ports:

- Machine 1: `8080`, `3000`, `9092`
- Machine 2: `8081`
- Machine 3: `8082`
- Machine 4: `8085`
- Machine 5: `8083`, `8084`

Open the application through:

```text
http://<machine-1-ip>:8080
```
