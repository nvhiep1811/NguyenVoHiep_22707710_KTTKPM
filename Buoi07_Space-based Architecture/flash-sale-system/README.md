# ⚡ Flash Sale System - Space-Based Architecture

Hệ thống Flash Sale chịu tải cao sử dụng **Space-Based Architecture** với **Redis** làm Data Grid và **MongoDB Atlas** làm persistent storage.

## 🏗️ Kiến trúc hệ thống

### Vì sao đây là Space-Based Architecture?

Space-Based Architecture (SBA) giải quyết bài toán **bottleneck database** khi có lượng request lớn (1000+ req/s) bằng cách:

1. **Loại bỏ database khỏi hot path**: Thay vì đọc/ghi MongoDB trực tiếp, mọi xử lý chính đều diễn ra trên **Redis (Data Grid)** - bộ nhớ in-memory có tốc độ đọc/ghi cực nhanh.
2. **Processing Unit (PU)**: Mỗi service là một Processing Unit độc lập, chứa logic xử lý và truy cập Data Grid. Các PU có thể scale horizontally.
3. **Async Persistence**: Dữ liệu chỉ được ghi xuống MongoDB Atlas một cách bất đồng bộ thông qua Sync Worker, không ảnh hưởng đến latency của hot path.

### Sơ đồ kiến trúc

```
                         ┌──────────────┐
                         │   Frontend   │
                         │  React Vite  │
                         │   :3000      │
                         └──────┬───────┘
                                │
              ┌─────────────────┼─────────────────┐
              │                 │                  │
    ┌─────────▼──────┐ ┌───────▼────────┐ ┌───────▼────────┐
    │  Product PU    │ │   Cart PU      │ │   Order PU     │
    │  :8081         │ │   :8082        │ │   :8083        │
    │  GET /products │ │  POST /cart/add│ │ POST /checkout │
    └─────────┬──────┘ └───────┬────────┘ └───────┬────────┘
              │                │                  │
              │                │          ┌───────▼────────┐
              │                │          │ Inventory PU   │
              │                │          │   :8084        │
              │                │          │ Lua Script     │
              │                │          └───────┬────────┘
              │                │                  │
    ┌─────────▼────────────────▼──────────────────▼────────┐
    │                    REDIS DATA GRID                     │
    │  products:all | product:{id} | cart:{userId}          │
    │  stock:{productId} | order:events                     │
    └──────────────────────────┬────────────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │    Sync Worker      │
                    │    :8085            │
                    │ • Data Seeder       │
                    │ • Data Loader       │
                    │ • Order Consumer    │
                    └──────────┬──────────┘
                               │ (async)
                    ┌──────────▼──────────┐
                    │  MongoDB Atlas      │
                    │  (Persistent Store) │
                    │  flash_sale_db      │
                    └─────────────────────┘
```

### Vai trò của từng thành phần

| Component | Port | Vai trò |
|-----------|------|---------|
| **Product PU** | 8081 | Đọc sản phẩm từ Redis, trả về danh sách/chi tiết sản phẩm |
| **Cart PU** | 8082 | Quản lý giỏ hàng trong Redis Hash, TTL 30 phút |
| **Order PU** | 8083 | Điều phối checkout: lấy cart → gọi Inventory → publish event |
| **Inventory PU** | 8084 | Quản lý stock bằng Redis atomic operations + Lua Script |
| **Sync Worker** | 8085 | Seed data, load MongoDB→Redis, consume order events |
| **Redis** | 6379 | Data Grid - nơi xử lý chính trong flash sale |
| **MongoDB Atlas** | Cloud | Persistent storage - chỉ dùng cho seed/load/async write |

### Vai trò của Redis Data Grid

Redis đóng vai trò **Data Grid** trung tâm trong Space-Based Architecture:
- **In-memory**: Tốc độ đọc/ghi ~100,000 ops/s, latency < 1ms
- **Products cache**: Lưu toàn bộ danh sách sản phẩm, tránh query MongoDB
- **Cart storage**: Lưu giỏ hàng tạm thời với TTL 30 phút
- **Stock counter**: Lưu số lượng tồn kho, hỗ trợ atomic DECRBY
- **Event queue**: Redis List làm message broker cho order events

### Vai trò của MongoDB Atlas

MongoDB Atlas chỉ được dùng cho **cold path**:
- Seed dữ liệu sản phẩm ban đầu
- Load dữ liệu vào Redis khi hệ thống khởi động
- Lưu orders sau checkout (async, không blocking)
- Lưu stock_movements cho audit trail
- Cập nhật inventory_snapshots

### Vì sao không nên gọi DB trực tiếp khi Flash Sale?

Trong Flash Sale với 1000+ concurrent requests:
- **Database bottleneck**: MongoDB có giới hạn connections, mỗi query mất 5-50ms
- **Lock contention**: Nhiều request cùng update stock → row-level locking → queue
- **Network latency**: Round-trip MongoDB Atlas qua network > round-trip Redis local
- **Connection pool exhaustion**: Pool connections cạn kiệt → timeout → failed requests

Giải pháp SBA: Redis xử lý mọi thứ in-memory, MongoDB chỉ nhận async writes.

## 🛡️ Chống Oversell bằng Redis Lua Script

### Vấn đề Race Condition

```
❌ CÁCH SAI (Race Condition):
Thread 1: GET stock → 1       Thread 2: GET stock → 1
Thread 1: if 1 > 0 → DECR    Thread 2: if 1 > 0 → DECR
Thread 1: stock = 0           Thread 2: stock = -1  ← OVERSELL!
```

### Giải pháp: Lua Script Atomic

```lua
-- Lua Script chạy ATOMIC trên Redis (single-threaded)
-- Bước 1: Kiểm tra TẤT CẢ sản phẩm có đủ stock
for i = 1, #KEYS do
    local stock = tonumber(redis.call('GET', KEYS[i]))
    if stock == nil or stock < tonumber(ARGV[i]) then
        return -1  -- Không đủ hàng → KHÔNG giảm bất kỳ sản phẩm nào
    end
end

-- Bước 2: Nếu đủ → DECRBY TẤT CẢ sản phẩm
for i = 1, #KEYS do
    redis.call('DECRBY', KEYS[i], tonumber(ARGV[i]))
end

return 1  -- Thành công
```

Lua Script đảm bảo:
- **Atomicity**: Toàn bộ script chạy không bị interrupt
- **All-or-nothing**: Hoặc giảm tất cả, hoặc không giảm gì
- **No race condition**: Redis single-threaded đảm bảo serialize execution

## 🔄 Luồng Checkout

```
1. User nhấn "Checkout" → Frontend gọi POST /checkout {userId: "user_1"}
2. Order PU nhận request
3. Order PU đọc cart:user_1 từ Redis Hash
4. Order PU đọc product:{id} từ Redis để lấy tên, giá
5. Order PU gọi Inventory PU: POST /stock/decrease
6. Inventory PU thực thi Lua Script atomic trên Redis
   - Nếu stock không đủ → return -1 → Order PU trả "Out of stock"
   - Nếu stock đủ → DECRBY → return 1
7. Order PU tạo order event, LPUSH vào order:events (Redis List)
8. Order PU xóa cart:user_1 từ Redis
9. Order PU trả response ngay: {orderCode, status: "SUCCESS"}
10. Sync Worker poll order:events (RPOP)
11. Sync Worker ghi order + stock_movements vào MongoDB Atlas (async)
```

**Hot path (steps 2-9)**: Chỉ Redis, ~5-10ms latency
**Cold path (steps 10-11)**: Async ghi MongoDB, không ảnh hưởng user

## 🚀 Hướng dẫn chạy

### Yêu cầu
- Java 17+
- Maven 3.8+
- Node.js 18+
- Redis (Docker hoặc local)
- MongoDB Atlas account (đã có connection string)

### Bước 1: Chạy Redis bằng Docker
```bash
docker run -d --name flash-sale-redis -p 6379:6379 redis:7.2-alpine
```

### Bước 2: Cấu hình MongoDB Atlas

Mở file `backend/sync-worker/src/main/resources/application.yml` và thay connection string:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://<username>:<password>@<cluster>.mongodb.net/flash_sale_db?retryWrites=true&w=majority
```

### Bước 3: Chạy Backend Services

**Quan trọng**: Chạy Sync Worker **trước** các PU khác (vì nó seed data và load vào Redis).

```bash
# Terminal 1: Sync Worker (seed + load data)
cd backend/sync-worker
mvn spring-boot:run

# Terminal 2: Product PU
cd backend/product-pu
mvn spring-boot:run

# Terminal 3: Cart PU
cd backend/cart-pu
mvn spring-boot:run

# Terminal 4: Inventory PU
cd backend/inventory-pu
mvn spring-boot:run

# Terminal 5: Order PU
cd backend/order-pu
mvn spring-boot:run
```

### Bước 4: Chạy Frontend
```bash
cd frontend/flash-sale-ui
npm install
npm run dev
```

Truy cập: http://localhost:3000

### Chạy bằng Docker Compose

```bash
# Set MongoDB Atlas URI
export MONGODB_ATLAS_URI="mongodb+srv://<username>:<password>@<cluster>.mongodb.net/flash_sale_db"

# Chạy tất cả services
docker-compose up --build
```

## 🧪 Test bằng Postman

### 1. Xem sản phẩm
```
GET http://localhost:8081/products
GET http://localhost:8081/products/product_1
```

### 2. Thêm vào giỏ hàng
```
POST http://localhost:8082/cart/add
Body: {"userId": "user_1", "productId": "product_1", "quantity": 1}
```

### 3. Xem giỏ hàng
```
GET http://localhost:8082/cart/user_1
```

### 4. Checkout
```
POST http://localhost:8083/checkout
Body: {"userId": "user_1"}
```

### 5. Kiểm tra stock
```
GET http://localhost:8084/stock/product_1
```

### Load Test với Postman Runner

1. Tạo Collection với request POST /checkout
2. Thêm Pre-request script để add cart trước mỗi request
3. Chạy Collection Runner với 100+ iterations
4. Observe: stock giảm chính xác, không oversell

## 📊 MongoDB Collections

| Collection | Mô tả |
|------------|--------|
| `users` | Thông tin người dùng |
| `products` | Danh sách sản phẩm |
| `flash_sales` | Sự kiện flash sale với items embedded |
| `inventory_snapshots` | Snapshot tồn kho |
| `orders` | Đơn hàng đã checkout |
| `stock_movements` | Lịch sử thay đổi tồn kho |

## 🔧 Cách chứng minh Scale

Để chứng minh SBA có thể scale, clone Processing Unit:

```bash
# Clone Product PU chạy port 8091
cd backend/product-pu
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8091"

# Clone Cart PU chạy port 8092
cd backend/cart-pu
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8092"
```

Cả 2 instance đều đọc từ **cùng Redis Data Grid** → dữ liệu consistent.
Có thể đặt Load Balancer (Nginx) phía trước để round-robin.

## 📁 Cấu trúc thư mục

```
flash-sale-system/
├── backend/
│   ├── product-pu/          # PU1 - Port 8081
│   ├── cart-pu/             # PU2 - Port 8082
│   ├── order-pu/            # PU3 - Port 8083
│   ├── inventory-pu/        # PU4 - Port 8084
│   └── sync-worker/         # Data Loader + Order Consumer - Port 8085
├── frontend/
│   └── flash-sale-ui/       # React Vite - Port 3000
├── docker-compose.yml
└── README.md
```
