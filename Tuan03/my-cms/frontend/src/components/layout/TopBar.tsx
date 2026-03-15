interface TopBarProps {
  title: string;
  onRefresh: () => void;
  onAction: () => void;
  actionLabel: string;
  isRefreshing: boolean;
  isPending: boolean;
}

export function TopBar({
  title,
  onRefresh,
  onAction,
  actionLabel,
  isRefreshing,
  isPending,
}: TopBarProps) {
  return (
    <header className="topbar">
      <div>
        <p className="eyebrow">Quản trị nội dung</p>
        <h2>{title}</h2>
      </div>

      <div className="topbar__actions">
        {isPending ? <span className="pill pill--ghost">Đang chuyển trang</span> : null}
        {isRefreshing ? <span className="pill pill--ghost">Đang đồng bộ</span> : null}
        <button className="button button--secondary" onClick={onRefresh} type="button">
          Đồng bộ
        </button>
        <button className="button" onClick={onAction} type="button">
          {actionLabel}
        </button>
      </div>
    </header>
  );
}
