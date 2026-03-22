# API Test Guide

## Prerequisites

- SQL Server is running on localhost:1433
- Databases are created from `auth_service_db.sql` and `user_service_db.sql`
- `.env` is stored at workspace root: `Tuan04/.env`
- Environment variable `JWT_SECRET` is loaded from root `.env` (same value for auth-service and api-gateway)
- Services are started:
  - api-gateway on 8090
  - auth-service on 8081
  - user-service on 8082

Example (PowerShell):

```powershell
Set-Location "d:\SS&Design\NguyenVoHiep_22707710_KTTKPM\Tuan04"
Get-Content .env | ForEach-Object {
  if ($_ -match "^\s*([^#=]+)=(.*)$") {
    [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2], "Process")
  }
}
```

## Run with Postman

1. Import `api-tests.postman_collection.json`.
2. Run requests in order from 1 to 6.
3. Request 2 automatically stores `accessToken` for JWT-protected requests.

## Covered flow

- Register auth account
- Login and receive JWT
- Access auth API
- Access user profile APIs with JWT
- Verify JWT is required for `/users/**` through gateway
