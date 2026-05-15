import axios from 'axios';

const HOST = window.location.hostname;

export const productApi = axios.create({
  baseURL: import.meta.env.VITE_PRODUCT_PU_URL || `http://${HOST}:8081`,
  timeout: 5000,
});

export const cartApi = axios.create({
  baseURL: import.meta.env.VITE_CART_PU_URL || `http://${HOST}:8082`,
  timeout: 5000,
});

export const orderApi = axios.create({
  baseURL: import.meta.env.VITE_ORDER_PU_URL || `http://${HOST}:8083`,
  timeout: 5000,
});

export const inventoryApi = axios.create({
  baseURL: import.meta.env.VITE_INVENTORY_PU_URL || `http://${HOST}:8084`,
  timeout: 5000,
});

// Hard-coded user ID for demo
export const DEMO_USER_ID = 'user_1';
