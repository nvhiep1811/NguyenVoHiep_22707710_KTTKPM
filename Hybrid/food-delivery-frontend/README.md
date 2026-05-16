# Food Delivery Frontend

React + Vite frontend. It only calls API Gateway.

## LAN

- Host: `192.168.1.14`
- Port: `3000`
- API Gateway: `http://192.168.1.10:8080`

## Environment

Copy `.env.example` to `.env` and adjust the Gateway IP if needed:

```bash
VITE_API_BASE_URL=http://192.168.1.10:8080
```

## Run

Copy `.env.example` to `.env`, then adjust the Gateway IP if needed:

```powershell
Copy-Item .env.example .env
notepad .env
```

```bash
npm install
.\run.ps1
```

Open:

```text
http://192.168.1.14:3000
```
