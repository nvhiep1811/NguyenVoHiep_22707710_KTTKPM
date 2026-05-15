import { Link, useLocation } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { cartApi, DEMO_USER_ID } from '../api';

function Header() {
  const location = useLocation();
  const [cartCount, setCartCount] = useState(0);

  useEffect(() => {
    fetchCartCount();
    // Poll cart count every 5 seconds
    const interval = setInterval(fetchCartCount, 5000);
    return () => clearInterval(interval);
  }, [location.pathname]);

  const fetchCartCount = async () => {
    try {
      const res = await cartApi.get(`/cart/${DEMO_USER_ID}`);
      const items = res.data.items || [];
      const count = items.reduce((sum, item) => sum + item.quantity, 0);
      setCartCount(count);
    } catch {
      setCartCount(0);
    }
  };

  return (
    <header className="header">
      <div className="header-inner">
        <Link to="/" className="logo">
          <span className="logo-icon">⚡</span>
          <span className="logo-text">FLASH SALE</span>
        </Link>
        <nav className="nav-links">
          <Link
            to="/"
            className={`nav-link ${location.pathname === '/' ? 'active' : ''}`}
          >
            🏪 Sản phẩm
          </Link>
          <Link
            to="/cart"
            className={`nav-link cart-link ${location.pathname === '/cart' ? 'active' : ''}`}
          >
            🛒 Giỏ hàng
            {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
          </Link>
        </nav>
      </div>
    </header>
  );
}

export default Header;
