function formatPrice(price) {
  return new Intl.NumberFormat('vi-VN').format(price) + '₫';
}

function CartItem({ item, onRemove }) {
  return (
    <div className="cart-item">
      <div className="cart-item-info">
        <div className="cart-item-name">{item.productName}</div>
        <div className="cart-item-price">{formatPrice(item.price)}</div>
        <div className="cart-item-qty">Số lượng: {item.quantity}</div>
      </div>
      <div className="cart-item-subtotal">{formatPrice(item.subtotal)}</div>
      <button
        className="btn btn-danger"
        onClick={() => onRemove(item.productId)}
        title="Xóa khỏi giỏ hàng"
      >
        ✕
      </button>
    </div>
  );
}

export default CartItem;
