import { useEffect, useMemo, useState } from 'react';
import { Link, NavLink, Navigate, Route, Routes, useLocation, useNavigate, useParams } from 'react-router-dom';
import api from './api.js';

const money = new Intl.NumberFormat('vi-VN', {
  style: 'currency',
  currency: 'VND'
});

const paymentMethods = [
  { value: 'COD', label: 'COD', hint: 'Thanh toán khi nhận hàng' },
  { value: 'BANKING', label: 'Banking', hint: 'Chuyển khoản ngân hàng' },
  { value: 'MOMO', label: 'Momo', hint: 'Ví điện tử Momo' },
  { value: 'VNPAY', label: 'VNPAY', hint: 'Cổng thanh toán VNPAY' }
];

function readAuth() {
  return {
    token: localStorage.getItem('token') || '',
    userId: localStorage.getItem('userId') || '',
    email: localStorage.getItem('email') || '',
    role: localStorage.getItem('role') || ''
  };
}

function readCart() {
  try {
    return JSON.parse(localStorage.getItem('cart') || '[]');
  } catch {
    return [];
  }
}

function saveCart(cart) {
  localStorage.setItem('cart', JSON.stringify(cart));
}

function cartQuantity(cart) {
  return cart.reduce((sum, item) => sum + item.quantity, 0);
}

function App() {
  const [auth, setAuth] = useState(readAuth());
  const [cartCount, setCartCount] = useState(() => cartQuantity(readCart()));
  const [toast, setToast] = useState('');
  const isLoggedIn = Boolean(auth.token && auth.userId);

  function handleAuth(nextAuth) {
    localStorage.setItem('token', nextAuth.token);
    localStorage.setItem('userId', nextAuth.userId);
    localStorage.setItem('email', nextAuth.email);
    localStorage.setItem('role', nextAuth.role);
    setAuth(nextAuth);
  }

  function logout() {
    ['token', 'userId', 'email', 'role', 'cart'].forEach((key) => localStorage.removeItem(key));
    setAuth(readAuth());
    setCartCount(0);
  }

  function refreshCartCount(message = '') {
    setCartCount(cartQuantity(readCart()));
    if (message) {
      setToast(message);
      window.setTimeout(() => setToast(''), 2200);
    }
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <Link className="brand" to="/foods">
          <img className="brand-logo" src="/logo.svg" alt="Food Delivery" />
          <span>Food Delivery</span>
        </Link>
        <nav className="nav" aria-label="Điều hướng chính">
          <NavLink to="/foods">Món ăn</NavLink>
          {isLoggedIn && <NavLink to="/orders/new">Giỏ hàng <span className="nav-badge">{cartCount}</span></NavLink>}
          {isLoggedIn && <NavLink to="/orders">Đơn của tôi</NavLink>}
          {!isLoggedIn && <NavLink to="/login">Đăng nhập</NavLink>}
          {!isLoggedIn && <NavLink to="/register">Đăng ký</NavLink>}
        </nav>
        {isLoggedIn && (
          <div className="account-chip">
            <span>{auth.email}</span>
            <button className="ghost-button small" onClick={logout}>Thoát</button>
          </div>
        )}
      </header>

      {toast && <div className="toast">{toast}</div>}

      <main className="page">
        <Routes>
          <Route path="/" element={<Navigate to="/foods" replace />} />
          <Route path="/login" element={<LoginPage onAuth={handleAuth} />} />
          <Route path="/register" element={<RegisterPage onAuth={handleAuth} />} />
          <Route path="/foods" element={<FoodListPage isLoggedIn={isLoggedIn} onCartChange={refreshCartCount} />} />
          <Route path="/foods/:id" element={<FoodDetailPage isLoggedIn={isLoggedIn} onCartChange={refreshCartCount} />} />
          <Route path="/orders/new" element={<RequireAuth isLoggedIn={isLoggedIn}><CreateOrderPage auth={auth} onCartChange={refreshCartCount} /></RequireAuth>} />
          <Route path="/orders" element={<RequireAuth isLoggedIn={isLoggedIn}><MyOrdersPage auth={auth} /></RequireAuth>} />
        </Routes>
      </main>
    </div>
  );
}

function RequireAuth({ isLoggedIn, children }) {
  const location = useLocation();
  if (!isLoggedIn) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }
  return children;
}

function AuthLayout({ title, subtitle, children, aside }) {
  return (
    <section className="auth-layout">
      <div className="auth-copy">
        <span className="eyebrow">Food Delivery</span>
        <h1>{title}</h1>
        <p>{subtitle}</p>
        
      </div>
      <div className="auth-panel">
        {children}
        {aside}
      </div>
    </section>
  );
}

function LoginPage({ onAuth }) {
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function submit(event) {
    event.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const response = await api.post('/api/auth/login', form);
      onAuth(response.data);
      navigate(location.state?.from || '/foods');
    } catch (err) {
      setError(readError(err, 'Đăng nhập thất bại'));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <AuthLayout title="Chào mừng trở lại" subtitle="Đăng nhập để đặt món và theo dõi đơn hàng của bạn.">
      <form onSubmit={submit} className="form">
        <label>Email<input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} type="email" placeholder="you@example.com" required /></label>
        <label>Mật khẩu<input value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} type="password" placeholder="Tối thiểu 6 ký tự" required /></label>
        {error && <p className="error">{error}</p>}
        <button type="submit" disabled={submitting}>{submitting ? 'Đang đăng nhập...' : 'Đăng nhập'}</button>
      </form>
      <p className="form-footer">Chưa có tài khoản? <Link to="/register">Đăng ký ngay</Link></p>
    </AuthLayout>
  );
}

function RegisterPage({ onAuth }) {
  const navigate = useNavigate();
  const [form, setForm] = useState({ fullName: '', email: '', password: '', phone: '' });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function submit(event) {
    event.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const response = await api.post('/api/auth/register', form);
      onAuth(response.data);
      navigate('/foods');
    } catch (err) {
      setError(readError(err, 'Đăng ký thất bại'));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <AuthLayout title="Tạo tài khoản" subtitle="Tạo tài khoản nhanh để bắt đầu đặt món.">
      <form onSubmit={submit} className="form">
        <label>Họ tên<input value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} placeholder="Nguyễn Văn A" required /></label>
        <label>Email<input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} type="email" placeholder="a@example.com" required /></label>
        <label>Mật khẩu<input value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} type="password" minLength={6} placeholder="Tối thiểu 6 ký tự" required /></label>
        <label>Số điện thoại<input value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} placeholder="0900000000" /></label>
        {error && <p className="error">{error}</p>}
        <button type="submit" disabled={submitting}>{submitting ? 'Đang tạo...' : 'Tạo tài khoản'}</button>
      </form>
      <p className="form-footer">Đã có tài khoản? <Link to="/login">Đăng nhập</Link></p>
    </AuthLayout>
  );
}

function FoodListPage({ isLoggedIn, onCartChange }) {
  const [foods, setFoods] = useState([]);
  const [quantities, setQuantities] = useState({});
  const [query, setQuery] = useState('');
  const [category, setCategory] = useState('Tất cả');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    api.get('/api/foods')
      .then((response) => setFoods(response.data))
      .catch((err) => setError(readError(err, 'Không tải được món ăn')))
      .finally(() => setLoading(false));
  }, []);

  const categories = useMemo(() => ['Tất cả', ...Array.from(new Set(foods.map((food) => food.category).filter(Boolean)))], [foods]);
  const filteredFoods = useMemo(() => foods.filter((food) => {
    const keyword = query.trim().toLowerCase();
    const matchKeyword = !keyword || [food.name, food.description, food.restaurantName, food.category].filter(Boolean).join(' ').toLowerCase().includes(keyword);
    const matchCategory = category === 'Tất cả' || food.category === category;
    return matchKeyword && matchCategory;
  }), [foods, query, category]);

  function updateQuantity(foodId, quantity) {
    setQuantities({ ...quantities, [foodId]: Math.max(1, Number(quantity) || 1) });
  }

  function add(food) {
    const quantity = quantities[food.id] || 1;
    addToCart(food, quantity);
    onCartChange(`Đã thêm ${quantity} x ${food.name} vào giỏ`);
  }

  return (
    <section>
      <div className="hero-panel">
        <div>
          <span className="eyebrow">Giao nhanh trong hôm nay</span>
          <h1>Chọn món ngon, đặt đơn thật nhanh</h1>
          <p>Xem món đang có, thêm vào giỏ và theo dõi trạng thái đơn hàng sau khi đặt.</p>
        </div>
        <div className="hero-actions">
          {isLoggedIn ? <Link className="primary-link" to="/orders/new">Xem giỏ hàng</Link> : <Link className="primary-link" to="/login">Đăng nhập để đặt món</Link>}
          <Link className="secondary-link" to="/orders">Theo dõi đơn</Link>
        </div>
      </div>

      <div className="toolbar">
        <div>
          <h2>Danh sách món</h2>
          <p>{filteredFoods.length} món sẵn sàng đặt</p>
        </div>
        <div className="filters">
          <input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Tìm món, quán, danh mục" />
          <select value={category} onChange={(e) => setCategory(e.target.value)}>
            {categories.map((item) => <option value={item} key={item}>{item}</option>)}
          </select>
        </div>
      </div>

      {error && <p className="error">{error}</p>}
      {loading && <FoodSkeleton />}
      {!loading && filteredFoods.length === 0 && <EmptyState title="Không tìm thấy món" text="Thử đổi từ khóa hoặc chọn danh mục khác." />}
      {!loading && filteredFoods.length > 0 && (
        <div className="food-grid">
          {filteredFoods.map((food) => (
            <article className="food-card" key={food.id}>
              <Link to={`/foods/${food.id}`} className="food-image-link">
                <img src={food.imageUrl || fallbackImage(food.name)} alt={food.name} />
              </Link>
              <div className="food-card-body">
                <div className="food-meta">
                  <span>{food.category || 'Món ăn'}</span>
                  <strong>{money.format(Number(food.price || 0))}</strong>
                </div>
                <div>
                  <h3>{food.name}</h3>
                  <p>{food.restaurantName}</p>
                </div>
                <p className="food-desc">{food.description}</p>
                <div className="inline-actions">
                  <input aria-label="Số lượng" type="number" min="1" value={quantities[food.id] || 1} onChange={(e) => updateQuantity(food.id, e.target.value)} />
                  <button onClick={() => add(food)}>Thêm vào giỏ</button>
                </div>
              </div>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}

function FoodDetailPage({ isLoggedIn, onCartChange }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [food, setFood] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get(`/api/foods/${id}`)
      .then((response) => setFood(response.data))
      .catch((err) => setError(readError(err, 'Không tìm thấy món ăn')));
  }, [id]);

  if (error) return <p className="error">{error}</p>;
  if (!food) return <FoodSkeleton />;

  function add() {
    addToCart(food, quantity);
    onCartChange(`Đã thêm ${quantity} x ${food.name} vào giỏ`);
    navigate(isLoggedIn ? '/orders/new' : '/login');
  }

  return (
    <section className="detail-layout">
      <img className="detail-image" src={food.imageUrl || fallbackImage(food.name)} alt={food.name} />
      <div className="detail-copy">
        <Link className="back-link" to="/foods">Quay lại danh sách</Link>
        <span className="eyebrow">{food.category || 'Món ăn'}</span>
        <h1>{food.name}</h1>
        <p>{food.description}</p>
        <p className="restaurant">{food.restaurantName}</p>
        <strong>{money.format(Number(food.price || 0))}</strong>
        <div className="inline-actions wide">
          <input aria-label="Số lượng" type="number" min="1" value={quantity} onChange={(e) => setQuantity(Math.max(1, Number(e.target.value) || 1))} />
          <button onClick={add}>{isLoggedIn ? 'Thêm và tạo đơn' : 'Đăng nhập để đặt'}</button>
        </div>
      </div>
    </section>
  );
}

function CreateOrderPage({ auth, onCartChange }) {
  const navigate = useNavigate();
  const [cart, setCart] = useState(readCart());
  const [paymentMethod, setPaymentMethod] = useState('COD');
  const [message, setMessage] = useState('');
  const [createdOrderId, setCreatedOrderId] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const total = useMemo(() => cart.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0), [cart]);

  function changeQuantity(foodId, quantity) {
    const nextCart = cart.map((item) => item.foodId === foodId ? { ...item, quantity: Math.max(1, Number(quantity) || 1) } : item);
    setCart(nextCart);
    saveCart(nextCart);
    onCartChange();
  }

  function remove(foodId) {
    const nextCart = cart.filter((item) => item.foodId !== foodId);
    setCart(nextCart);
    saveCart(nextCart);
    onCartChange();
  }

  async function submit(event) {
    event.preventDefault();
    setError('');
    setMessage('');
    setCreatedOrderId('');
    if (cart.length === 0) {
      setError('Giỏ hàng đang trống');
      return;
    }

    setSubmitting(true);
    try {
      const response = await api.post('/api/orders', {
        userId: auth.userId,
        items: cart.map((item) => ({
          foodId: item.foodId,
          foodName: item.foodName,
          price: Number(item.price),
          quantity: item.quantity
        })),
        paymentMethod
      });
      saveCart([]);
      setCart([]);
      onCartChange();
      setCreatedOrderId(response.data.orderId);
      setMessage('Đơn hàng đã được tạo. Hệ thống đang xử lý thanh toán.');
      window.setTimeout(() => navigate('/orders'), 1400);
    } catch (err) {
      setError(readError(err, 'Không tạo được đơn hàng'));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section>
      <div className="checkout-head">
        <div>
          <span className="eyebrow">Checkout</span>
          <h1>Tạo đơn hàng</h1>
          <p>Kiểm tra lại món, chọn phương thức thanh toán và gửi đơn.</p>
        </div>
        <Link className="secondary-link" to="/foods">Thêm món</Link>
      </div>

      <form onSubmit={submit} className="checkout-layout">
        <div className="checkout-main">
          <Step number="1" title="Kiểm tra món đã chọn" />
          <div className="cart-list">
            {cart.map((item) => (
              <div className="cart-row" key={item.foodId}>
                <div>
                  <strong>{item.foodName}</strong>
                  <p>{money.format(Number(item.price))} mỗi phần</p>
                </div>
                <input aria-label="Số lượng" type="number" min="1" value={item.quantity} onChange={(e) => changeQuantity(item.foodId, e.target.value)} />
                <strong>{money.format(Number(item.price) * item.quantity)}</strong>
                <button type="button" className="danger-button" onClick={() => remove(item.foodId)}>Xóa</button>
              </div>
            ))}
            {cart.length === 0 && <EmptyState title="Giỏ hàng đang trống" text="Quay lại danh sách món để chọn món trước khi tạo đơn." action={<Link className="primary-link" to="/foods">Chọn món</Link>} />}
          </div>

          <Step number="2" title="Chọn phương thức thanh toán" />
          <div className="payment-grid">
            {paymentMethods.map((method) => (
              <label className={`payment-option ${paymentMethod === method.value ? 'selected' : ''}`} key={method.value}>
                <input type="radio" name="paymentMethod" value={method.value} checked={paymentMethod === method.value} onChange={(e) => setPaymentMethod(e.target.value)} />
                <span>{method.label}</span>
                <small>{method.hint}</small>
              </label>
            ))}
          </div>
        </div>

        <aside className="summary-panel">
          <h2>Tóm tắt đơn</h2>
          <div className="summary-line"><span>Số món</span><strong>{cartQuantity(cart)}</strong></div>
          <div className="summary-line"><span>Tạm tính</span><strong>{money.format(total)}</strong></div>
          <div className="summary-line muted-line"><span>Trạng thái</span><span>Đang xử lý</span></div>
          {message && <p className="success">{message}{createdOrderId ? ` Mã đơn: ${createdOrderId}` : ''}</p>}
          {error && <p className="error">{error}</p>}
          <button type="submit" disabled={submitting || cart.length === 0}>{submitting ? 'Đang gửi đơn...' : 'Đặt món'}</button>
          <p className="summary-note">Sau khi đặt món, hệ thống sẽ tự chuyển sang trang đơn hàng để bạn theo dõi trạng thái.</p>
        </aside>
      </form>
    </section>
  );
}

function MyOrdersPage({ auth }) {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function load() {
    setLoading(true);
    setError('');
    try {
      const response = await api.get(`/api/orders/user/${auth.userId}`);
      setOrders(response.data);
    } catch (err) {
      setError(readError(err, 'Không tải được đơn hàng'));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    const timer = window.setInterval(load, 8000);
    return () => window.clearInterval(timer);
  }, []);

  return (
    <section>
      <div className="checkout-head">
        <div>
          <span className="eyebrow">Đơn hàng</span>
          <h1>Đơn của tôi</h1>
          <p>Theo dõi trạng thái đơn hàng và bấm làm mới khi cần.</p>
        </div>
        <button className="secondary-button" onClick={load} disabled={loading}>{loading ? 'Đang tải...' : 'Làm mới'}</button>
      </div>
      {error && <p className="error">{error}</p>}
      <div className="orders-table">
        {orders.map((order) => (
          <article className="order-row" key={order.id}>
            <div className="order-main">
              <strong>#{shortId(order.id)}</strong>
              <p>{new Date(order.createdAt).toLocaleString('vi-VN')}</p>
              <small>{order.items?.map((item) => `${item.foodName} x${item.quantity}`).join(', ')}</small>
            </div>
            <div className="status-group">
              <span className={`status ${order.status.toLowerCase()}`}>{statusLabel(order.status)}</span>
              <span className={`status ${order.paymentStatus.toLowerCase()}`}>{paymentStatusLabel(order.paymentStatus)}</span>
            </div>
            <strong>{money.format(Number(order.totalAmount || 0))}</strong>
          </article>
        ))}
        {!loading && orders.length === 0 && <EmptyState title="Chưa có đơn hàng" text="Tạo đơn đầu tiên để theo dõi trạng thái tại đây." action={<Link className="primary-link" to="/foods">Chọn món</Link>} />}
      </div>
    </section>
  );
}

function Step({ number, title }) {
  return <div className="step-title"><span>{number}</span><h2>{title}</h2></div>;
}

function FoodSkeleton() {
  return <div className="skeleton-grid">{[1, 2, 3, 4].map((item) => <div className="skeleton-card" key={item} />)}</div>;
}

function EmptyState({ title, text, action }) {
  return <div className="empty-state"><h3>{title}</h3><p>{text}</p>{action}</div>;
}

function addToCart(food, quantity) {
  const cart = readCart();
  const existing = cart.find((item) => item.foodId === food.id);
  if (existing) {
    existing.quantity += Number(quantity);
  } else {
    cart.push({
      foodId: food.id,
      foodName: food.name,
      price: Number(food.price),
      quantity: Number(quantity)
    });
  }
  saveCart(cart);
}

function readError(err, fallback) {
  const messages = err?.response?.data?.messages;
  if (Array.isArray(messages) && messages.length > 0) return messages.join(', ');
  return err?.response?.data?.message || fallback;
}

function fallbackImage(name) {
  return `https://placehold.co/900x600/f7efe4/23332d?text=${encodeURIComponent(name)}`;
}

function shortId(id = '') {
  return id.length > 10 ? id.slice(-8).toUpperCase() : id;
}

function statusLabel(status) {
  return {
    CREATED: 'Đã tạo',
    PAID: 'Đã thanh toán',
    PAYMENT_FAILED: 'Thanh toán lỗi',
    CANCELLED: 'Đã hủy'
  }[status] || status;
}

function paymentStatusLabel(status) {
  return {
    PENDING: 'Đang xử lý',
    SUCCESS: 'Thành công',
    FAILED: 'Thất bại'
  }[status] || status;
}

export default App;