# API Gateway

Spring Cloud Gateway route frontend requests to backend services.

## LAN

- Host: `192.168.1.10`
- Port: `8080`
- Address binding: `0.0.0.0`

## Routes

| Path | Target |
| --- | --- |
| `/api/auth/**` | `http://192.168.1.11:8081` |
| `/api/users/**` | `http://192.168.1.11:8081` |
| `/api/foods/**` | `http://192.168.1.11:8081` |
| `/api/orders/**` | `http://192.168.1.12:8082` |

## Run

Copy `.env.example` to `.env`, then adjust LAN IPs if your machines use different addresses:

```powershell
Copy-Item .env.example .env
notepad .env
.\run.ps1
```

Or run directly:

```bash
mvn spring-boot:run
```

Gateway does not connect to MongoDB and does not use RabbitMQ.
