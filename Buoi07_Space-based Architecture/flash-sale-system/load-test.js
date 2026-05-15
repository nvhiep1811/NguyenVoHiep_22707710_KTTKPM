import http from "k6/http";
import { check, sleep } from "k6";

// Cấu hình Load Test
export let options = {
  stages: [
    { duration: "10s", target: 50 }, // Ramp-up: Tăng dần lên 50 user
    { duration: "30s", target: 1000 }, // Peak: 200 user liên tục
    { duration: "10s", target: 0 }, // Ramp-down
  ],
};

const HOST = "localhost";
const CART_URL = `http://${HOST}:8082/cart/add`;
const ORDER_URL = `http://${HOST}:8083/checkout`;

export default function () {
  // 1. Giả lập random user
  const randomUserId = `user_${Math.floor(Math.random() * 100000)}`;

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  // 2. Thêm vào giỏ hàng trước (Cart PU)
  const cartPayload = JSON.stringify({
    userId: randomUserId,
    productId: "product_1", // Test mua Camera IP
    quantity: 1,
  });

  let cartRes = http.post(CART_URL, cartPayload, params);

  // Nếu thêm giỏ hàng thành công thì mới gọi Checkout
  if (cartRes.status === 200) {
    // 3. Checkout (Order PU)
    const orderPayload = JSON.stringify({
      userId: randomUserId,
    });

    let orderRes = http.post(ORDER_URL, orderPayload, params);

    check(orderRes, {
      "Checkout status is 200": (r) => r.status === 200,
      // Nếu hết hàng nó sẽ trả về 400 Bad Request, đó là hành vi chống oversell đúng.
    });
  }

  sleep(0.1);
}
