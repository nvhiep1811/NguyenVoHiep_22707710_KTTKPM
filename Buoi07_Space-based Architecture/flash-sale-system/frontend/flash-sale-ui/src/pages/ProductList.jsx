import { useState, useEffect } from 'react';
import { productApi } from '../api';
import ProductCard from '../components/ProductCard';

function ProductList() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProducts();
    // Auto-refresh stock every 5 seconds
    const interval = setInterval(fetchProducts, 5000);
    return () => clearInterval(interval);
  }, []);

  const fetchProducts = async () => {
    try {
      const res = await productApi.get('/products');
      setProducts(res.data);
    } catch (err) {
      console.error('Failed to fetch products:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner" />
        <span>Đang tải sản phẩm...</span>
      </div>
    );
  }

  return (
    <>
      <div className="flash-banner">
        <h1>⚡ FLASH SALE - GIẢM GIÁ SỐC ĐẾN 50%</h1>
        <p>Số lượng có hạn • Mua nhanh kẻo hết</p>
      </div>

      {products.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">📦</div>
          <h3>Chưa có sản phẩm</h3>
          <p>Hệ thống đang tải hoặc chưa có sản phẩm.</p>
        </div>
      ) : (
        <div className="product-grid">
          {products.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      )}
    </>
  );
}

export default ProductList;
