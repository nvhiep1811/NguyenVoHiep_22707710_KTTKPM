import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { cartApi, DEMO_USER_ID } from '../api';
import CartItem from '../components/CartItem';

function formatPrice(price) {
  return new Intl.NumberFormat('vi-VN').format(price) + '₫';
}

function Cart() {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    try {
      const res = await cartApi.get(`/cart/${DEMO_USER_ID}`);
      setCart(res.data);
    } catch (err) {
      console.error('Failed to fetch cart:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRemoveItem = async (productId) => {
    try {
      await cartApi.delete(`/cart/${DEMO_USER_ID}/items/${productId}`);
      toast.success('Đã xóa sản phẩm khỏi giỏ hàng');
      fetchCart();
    } catch (err) {
      toast.error('Không thể xóa sản phẩm');
    }
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner" />
        <span>Đang tải giỏ hàng...</span>
      </div>
    );
  }

  const items = cart?.items || [];
  const isEmpty = items.length === 0;

  return (
    <>
      <h2 className="page-title">🛒 Giỏ hàng</h2>

      {isEmpty ? (
        <div className="empty-state">
          <div className="empty-state-icon">🛒</div>
          <h3>Giỏ hàng trống</h3>
          <p>Hãy thêm sản phẩm vào giỏ hàng để tiếp tục mua sắm</p>
          <Link to="/" className="btn btn-primary mt-2">← Tiếp tục mua sắm</Link>
        </div>
      ) : (
        <div className="cart-layout">
          <div className="cart-items">
            {items.map((item) => (
              <CartItem key={item.productId} item={item} onRemove={handleRemoveItem} />
            ))}
          </div>

          <div className="cart-summary">
            <h3>📋 Tóm tắt đơn hàng</h3>
            {items.map((item) => (
              <div key={item.productId} className="summary-row">
                <span>{item.productName} x{item.quantity}</span>
                <span>{formatPrice(item.subtotal)}</span>
              </div>
            ))}
            <div className="summary-row total">
              <span>Tổng cộng</span>
              <span>{formatPrice(cart.totalAmount)}</span>
            </div>
            <button
              className="btn btn-primary btn-full btn-lg mt-2"
              onClick={() => navigate('/checkout')}
            >
              ⚡ Thanh toán ngay
            </button>
            <Link to="/" className="btn btn-secondary btn-full mt-1">
              ← Tiếp tục mua sắm
            </Link>
          </div>
        </div>
      )}
    </>
  );
}

export default Cart;
