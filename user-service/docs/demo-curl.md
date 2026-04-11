# Demo curl

Mac dinh service chay o:

```text
http://localhost:8081
```

Neu chay cheo may trong LAN, doi `BASE_URL` thanh IP that, vi du:

```text
http://192.168.1.20:8081
```

## 1. Dat bien

```powershell
$BASE_URL = "http://localhost:8081"
$REQUEST_ID = [guid]::NewGuid().ToString()
$EMAIL = "demo.user@example.com"
$PASSWORD = "Demo1234"
```

## 2. Health check

```powershell
curl.exe --request GET `
  --url "$BASE_URL/actuator/health" `
  --header "X-Request-Id: $REQUEST_ID"
```

## 3. Register

```powershell
curl.exe --request POST `
  --url "$BASE_URL/register" `
  --header "Content-Type: application/json" `
  --header "X-Request-Id: $REQUEST_ID" `
  --data "{\"fullName\":\"Demo User\",\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}"
```

## 4. Login

```powershell
$LOGIN_RESPONSE = curl.exe --silent `
  --request POST `
  --url "$BASE_URL/login" `
  --header "Content-Type: application/json" `
  --header "X-Request-Id: $REQUEST_ID" `
  --data "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}"

$LOGIN_RESPONSE
```

## 5. Tach token va userId

```powershell
$LOGIN_JSON = $LOGIN_RESPONSE | ConvertFrom-Json
$TOKEN = $LOGIN_JSON.accessToken
$USER_ID = $LOGIN_JSON.user.id

"TOKEN=$TOKEN"
"USER_ID=$USER_ID"
```

## 6. Lay thong tin user hien tai

```powershell
curl.exe --request GET `
  --url "$BASE_URL/users/me" `
  --header "Authorization: Bearer $TOKEN" `
  --header "X-Request-Id: $REQUEST_ID"
```

## 7. Validate user cho Order Service

```powershell
curl.exe --request GET `
  --url "$BASE_URL/internal/users/$USER_ID/validation" `
  --header "X-Request-Id: $REQUEST_ID"
```

## 8. Lay danh sach users bang admin

Mac dinh service se bootstrap:

```text
email: admin@mini-food.local
password: Admin@12345
```

Login admin:

```powershell
$ADMIN_LOGIN = curl.exe --silent `
  --request POST `
  --url "$BASE_URL/login" `
  --header "Content-Type: application/json" `
  --data "{\"email\":\"admin@mini-food.local\",\"password\":\"Admin@12345\"}"

$ADMIN_TOKEN = ($ADMIN_LOGIN | ConvertFrom-Json).accessToken
```

Lay danh sach users:

```powershell
curl.exe --request GET `
  --url "$BASE_URL/users" `
  --header "Authorization: Bearer $ADMIN_TOKEN" `
  --header "X-Request-Id: $REQUEST_ID"
```
