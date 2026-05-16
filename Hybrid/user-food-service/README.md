# User/Food Service

REST service for authentication, users, and foods. This service does not use RabbitMQ.

## LAN

- Host: `192.168.1.11`
- Port: `8081`
- MongoDB Atlas database: `food_delivery_user_food_db`
- Collections: `users`, `foods`

## Environment

```bash
USER_FOOD_MONGODB_URI=mongodb+srv://USER:PASS@CLUSTER/food_delivery_user_food_db?retryWrites=true&w=majority
JWT_SECRET=replace-with-at-least-32-character-secret-key
```

## Run

Copy `.env.example` to `.env`, then set your MongoDB Atlas URI:

```powershell
Copy-Item .env.example .env
notepad .env
.\run.ps1
```

Or run directly:

```bash
mvn spring-boot:run
```

## APIs

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Register user, BCrypt password, return JWT |
| `POST` | `/api/auth/login` | Login and return JWT |
| `GET` | `/api/users` | List users |
| `GET` | `/api/users/{id}` | Get user |
| `GET` | `/api/foods` | List available foods |
| `GET` | `/api/foods/{id}` | Get available food |
| `POST` | `/api/foods` | Create food |
| `PUT` | `/api/foods/{id}` | Update food |
| `DELETE` | `/api/foods/{id}` | Soft delete food by setting `available=false` |

When `foods` is empty, sample foods are inserted on startup.
