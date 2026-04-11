# User Service

Spring Boot `User Service` cho bai `Mini Food Ordering System`.

## APIs

- `POST /register`
- `POST /login`
- `GET /users`
- `GET /users/me`
- `GET /users/{id}`
- `PUT /users/{id}`
- `PATCH /users/{id}/role`
- `PATCH /users/{id}/status`
- `GET /internal/users/{id}/validation`
- `GET /actuator/health`
- `GET /actuator/info`

## Chuc nang bo sung cho tich hop

- JWT don gian
- CORS cau hinh duoc theo LAN
- request id / correlation id
- request logging co `instanceId`
- health check cho gateway / monitor

## Chay local

```powershell
.\mvnw.cmd spring-boot:run
```

`application.yml` da duoc cau hinh de tu dong import file `.env` neu file nay ton tai trong root project.

## Demo requests

- `docs/demo-curl.md`
- `postman/user-service.postman_collection.json`

## Environment files

- `.env` dung cho local, da duoc ignore khoi git
- `.env.example` dung de push len GitHub
- `application.yml` doc gia tri tu `.env` qua `spring.config.import`
