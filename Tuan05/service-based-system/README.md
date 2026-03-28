# Service-Based System Demo

Demo co ban theo kien truc service-based gom:

- order-service (Spring Boot)
- payment-service (Spring Boot)
- shipping-service (Spring Boot)
- api-gateway (Spring Cloud Gateway)
- frontend (React + Vite + Nginx)
- 1 shared database (PostgreSQL)

## 1. Chay toan bo bang Docker Compose

```bash
docker compose up --build
```

Neu muon doi profile healthcheck:

```bash
# May manh (len nhanh hon)
docker compose -f docker-compose.yml -f docker-compose.fast.yml up --build

# May yeu (on dinh hon)
docker compose -f docker-compose.yml -f docker-compose.safe.yml up --build
```

Sau khi chay:

- Frontend: http://localhost:5173
- Gateway: http://localhost:8080
- Order API qua Gateway: http://localhost:8080/api/orders
- Payment API qua Gateway: http://localhost:8080/api/payments
- Shipping API qua Gateway: http://localhost:8080/api/shipments

## 2. API health check

- http://localhost:8080/api/gateway/health
- http://localhost:8080/api/orders/health
- http://localhost:8080/api/payments/health
- http://localhost:8080/api/shipments/health

## 3. Ghi chu

- 3 service dung chung 1 DB: `my_shared_db`.
- Moi service co bang rieng va du lieu mau trong `data.sql`.
- Frontend goi vao API Gateway (port 8080), gateway dinh tuyen den 3 service.
- File profile healthcheck:
  - `docker-compose.fast.yml`: uu tien toc do startup.
  - `docker-compose.safe.yml`: uu tien do on dinh tren may cau hinh thap.
