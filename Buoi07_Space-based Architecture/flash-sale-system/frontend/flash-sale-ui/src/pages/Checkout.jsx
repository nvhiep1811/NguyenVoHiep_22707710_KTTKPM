import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { cartApi, orderApi, DEMO_USER_ID } from '../api';

function formatPrice(price) {
  return new Intl.NumberFormat('vi-VN').format(price) + '₫';
}

function Checkout() {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);
  const [orderResult, setOrderResult] = useState(null);

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

  const handleCheckout = async () => {
    if (processing) return;
    setProcessing(true);
    try {
      const res = await orderApi.post('/checkout', { userId: DEMO_USER_ID });
      if (res.data.status === 'SUCCESS') {
        setOrderResult(res.data);
        toast.success('🎉 Đặt hàng thành công!');
      } else {
        toast.error(res.data.message || 'Checkout thất bại');
      }
    } catch (err) {
      const msg = err.response?.data?.message || 'Checkout thất bại';
      toast.error(msg);
    } finally {
      setProcessing(false);
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

  // Show success screen
  if (orderResult) {
    return (
      <div className="checkout-success">
        <div className="success-icon">🎉</div>
        <h2>Đặt hàng thành công!</h2>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '1rem' }}>
          Cảm ơn bạn đã mua sắm. Đơn hàng của bạn đang được xử lý.
        </p>
        <div className="order-code">{orderResult.orderCode}</div>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', margin: '0.5rem 0 1.5rem' }}>
          Tổng thanh toán: <strong style={{ color: 'var(--accent-primary)' }}>{formatPrice(orderResult.totalAmount)}</strong>
        </p>
        <Link to="/" className="btn btn-primary btn-lg">
          ← Tiếp tục mua sắm
        </Link>
      </div>
    );
  }

  const items = cart?.items || [];
  const isEmpty = items.length === 0;

  if (isEmpty) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">🛒</div>
        <h3>Giỏ hàng trống</h3>
        <p>Không có sản phẩm nào để thanh toán</p>
        <Link to="/" className="btn btn-primary mt-2">← Mua sắm ngay</Link>
      </div>
    );
  }

  return (
    <>
      <h2 className="page-title">⚡ Thanh toán</h2>
      <div className="cart-layout">
        <div className="cart-items">
          <h3 style={{ marginBottom: '1rem', color: 'var(--text-secondary)', fontWeight: 500 }}>
            Sản phẩm trong đơn hàng
          </h3>
          {items.map((item) => (
            <div key={item.productId} className="cart-item">
              <div className="cart-item-info">
                <div className="cart-item-name">{item.productName}</div>
                <div className="cart-item-qty">Số lượng: {item.quantity}</div>
                <div className="cart-item-price">{formatPrice(item.price)} / sản phẩm</div>
              </div>
              <div className="cart-item-subtotal">{formatPrice(item.subtotal)}</div>
            </div>
          ))}
        </div>

        <div className="cart-summary">
          <h3>💳 Xác nhận thanh toán</h3>
          <div className="summary-row">
            <span>Khách hàng</span>
            <span>{DEMO_USER_ID}</span>
          </div>
          <div className="summary-row">
            <span>Số sản phẩm</span>
            <span>{items.reduce((s, i) => s + i.quantity, 0)}</span>
          </div>
          <div className="summary-row total">
            <span>Tổng cộng</span>
            <span>{formatPrice(cart.totalAmount)}</span>
          </div>
          <button
            className="btn btn-primary btn-full btn-lg"
            onClick={handleCheckout}
            disabled={processing}
          >
            {processing ? '⏳ Đang xử lý...' : '⚡ Xác nhận đặt hàng'}
          </button>
          <Link to="/cart" className="btn btn-secondary btn-full mt-1">
            ← Quay lại giỏ hàng
          </Link>
        </div>
      </div>
    </>
  );
}

export default Checkout;
