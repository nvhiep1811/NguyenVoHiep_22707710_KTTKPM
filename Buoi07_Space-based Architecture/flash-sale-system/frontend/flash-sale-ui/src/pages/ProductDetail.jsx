import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { productApi, cartApi, inventoryApi, DEMO_USER_ID } from '../api';

function formatPrice(price) {
  return new Intl.NumberFormat('vi-VN').format(price) + '₫';
}

function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [stock, setStock] = useState(0);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [adding, setAdding] = useState(false);

  useEffect(() => {
    fetchProduct();
    fetchStock();
    const interval = setInterval(fetchStock, 3000);
    return () => clearInterval(interval);
  }, [id]);

  const fetchProduct = async () => {
    try {
      const res = await productApi.get(`/products/${id}`);
      setProduct(res.data);
      setStock(res.data.stock);
    } catch (err) {
      console.error('Failed to fetch product:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchStock = async () => {
    try {
      const res = await inventoryApi.get(`/stock/${id}`);
      setStock(res.data.stock);
    } catch (err) {
      console.error('Failed to fetch stock:', err);
    }
  };

  const handleAddToCart = async () => {
    if (adding) return;
    setAdding(true);
    try {
      await cartApi.post('/cart/add', {
        userId: DEMO_USER_ID,
        productId: id,
        quantity: quantity,
      });
      toast.success(`Đã thêm ${quantity} x "${product.name}" vào giỏ hàng!`);
    } catch (err) {
      toast.error('Không thể thêm vào giỏ hàng');
    } finally {
      setAdding(false);
    }
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner" />
        <span>Đang tải...</span>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">❌</div>
        <h3>Không tìm thấy sản phẩm</h3>
        <Link to="/" className="btn btn-primary mt-2">← Về trang chủ</Link>
      </div>
    );
  }

  const discount = Math.round((1 - product.salePrice / product.originalPrice) * 100);
  const isOutOfStock = stock <= 0;

  return (
    <>
      <Link to="/" className="back-btn">← Quay lại</Link>
      <div className="product-detail">
        <div>
          <img
            className="product-detail-image"
            src={product.thumbnailUrl}
            alt={product.name}
            onError={(e) => {
              e.target.src = `https://picsum.photos/seed/${product.id}/600/600`;
            }}
          />
        </div>
        <div className="product-detail-info">
          <div>
            <span className="sale-tag" style={{ position: 'static', display: 'inline-block', marginBottom: '12px' }}>
              FLASH SALE -{discount}%
            </span>
          </div>
          <h1 className="product-detail-name">{product.name}</h1>
          <p className="product-detail-desc">{product.description}</p>

          <div className="price-row">
            <span className="sale-price" style={{ fontSize: '2rem' }}>{formatPrice(product.salePrice)}</span>
            <span className="original-price" style={{ fontSize: '1.1rem' }}>{formatPrice(product.originalPrice)}</span>
          </div>

          <div className="stock-bar" style={{ maxWidth: '300px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
              <span style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Còn {stock} sản phẩm</span>
            </div>
            <div className="stock-bar-track">
              <div
                className={`stock-bar-fill ${isOutOfStock ? 'empty' : stock < 20 ? 'critical' : stock < 50 ? 'low' : ''}`}
                style={{ width: `${Math.min((stock / 200) * 100, 100)}%` }}
              />
            </div>
          </div>

          {!isOutOfStock && (
            <div className="quantity-selector">
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Số lượng:</span>
              <button className="qty-btn" onClick={() => setQuantity(Math.max(1, quantity - 1))}>−</button>
              <span className="qty-value">{quantity}</span>
              <button className="qty-btn" onClick={() => setQuantity(Math.min(stock, quantity + 1))}>+</button>
            </div>
          )}

          <button
            className="btn btn-primary btn-lg"
            onClick={handleAddToCart}
            disabled={isOutOfStock || adding}
            style={{ maxWidth: '300px' }}
          >
            {adding ? '⏳ Đang thêm...' : isOutOfStock ? 'Hết hàng' : '🛒 Thêm vào giỏ hàng'}
          </button>

          <button
            className="btn btn-secondary"
            onClick={() => { handleAddToCart().then(() => navigate('/cart')); }}
            disabled={isOutOfStock || adding}
            style={{ maxWidth: '300px' }}
          >
            ⚡ Mua ngay
          </button>
        </div>
      </div>
    </>
  );
}

export default ProductDetail;
