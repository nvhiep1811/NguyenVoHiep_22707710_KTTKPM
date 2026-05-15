import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { cartApi, DEMO_USER_ID } from '../api';

function formatPrice(price) {
  return new Intl.NumberFormat('vi-VN').format(price) + '₫';
}

function ProductCard({ product }) {
  const navigate = useNavigate();
  const discount = Math.round((1 - product.salePrice / product.originalPrice) * 100);
  const maxStock = 200; // For progress bar scaling
  const stockPercent = Math.min((product.stock / maxStock) * 100, 100);
  const isOutOfStock = product.stock <= 0;

  const getStockBarClass = () => {
    if (isOutOfStock) return 'stock-bar-fill empty';
    if (stockPercent < 15) return 'stock-bar-fill critical';
    if (stockPercent < 40) return 'stock-bar-fill low';
    return 'stock-bar-fill';
  };

  const handleAddToCart = async (e) => {
    e.stopPropagation();
    if (isOutOfStock) return;
    try {
      await cartApi.post('/cart/add', {
        userId: DEMO_USER_ID,
        productId: product.id,
        quantity: 1,
      });
      toast.success(`Đã thêm "${product.name}" vào giỏ hàng!`);
    } catch (err) {
      toast.error('Không thể thêm vào giỏ hàng');
    }
  };

  return (
    <div className="product-card" onClick={() => navigate(`/products/${product.id}`)}>
      <div className="product-card-image-wrapper">
        <span className="sale-tag">-{discount}%</span>
        <img
          className="product-card-image"
          src={product.thumbnailUrl}
          alt={product.name}
          onError={(e) => {
            e.target.src = `https://picsum.photos/seed/${product.id}/400/400`;
          }}
        />
      </div>
      <div className="product-card-body">
        <h3 className="product-card-name">{product.name}</h3>
        <p className="product-card-desc">{product.description}</p>
        <div className="price-row">
          <span className="sale-price">{formatPrice(product.salePrice)}</span>
          <span className="original-price">{formatPrice(product.originalPrice)}</span>
          <span className="discount-percent">-{discount}%</span>
        </div>
        <div className="stock-bar">
          <div className="stock-label">
            <span>{isOutOfStock ? '🔴 Hết hàng' : `⚡ Còn ${product.stock} sản phẩm`}</span>
          </div>
          <div className="stock-bar-track">
            <div
              className={getStockBarClass()}
              style={{ width: `${stockPercent}%` }}
            />
          </div>
        </div>
        <button
          className="btn btn-primary btn-full"
          onClick={handleAddToCart}
          disabled={isOutOfStock}
        >
          {isOutOfStock ? 'Hết hàng' : '🛒 Thêm vào giỏ'}
        </button>
      </div>
    </div>
  );
}

export default ProductCard;
