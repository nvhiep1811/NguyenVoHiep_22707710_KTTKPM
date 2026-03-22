# Monolithic API Test Guide

## Prerequisites

- SQL Server is running on localhost:1433
- Database is created from `mono_db.sql`
- `.env` is stored at workspace root: `Tuan04/.env`
- Environment variable `JWT_SECRET` is loaded from root `.env`
- Monolithic app is running on 8080

Example (PowerShell):

```powershell
Set-Location "d:\SS&Design\NguyenVoHiep_22707710_KTTKPM\Tuan04"
Get-Content .env | ForEach-Object {
	if ($_ -match "^\s*([^#=]+)=(.*)$") {
		[System.Environment]::SetEnvironmentVariable($matches[1], $matches[2], "Process")
	}
}
Set-Location "./monolithic-app"
./mvnw.cmd spring-boot:run
```

## Run with Postman

1. Import `mono-api-tests.postman_collection.json`.
2. Run requests in order from 1 to 5.
3. Request 2 stores `accessToken` automatically for secured endpoints.

## Covered flow

- Register user in monolith
- Login and receive JWT
- Access secured monolith user APIs
- Update profile with JWT
- Verify 401 when JWT is missing
