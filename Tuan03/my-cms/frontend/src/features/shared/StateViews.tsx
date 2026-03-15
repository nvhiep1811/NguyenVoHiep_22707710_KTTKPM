interface ErrorStateProps {
  message: string;
  onRetry: () => void;
}

export function LoadingState() {
  return (
    <section className="state-card">
      <span className="eyebrow">Đang tải</span>
      <h3>CMS đang đồng bộ dữ liệu từ core services</h3>
      <p>Đợi dashboard, plugins và content service trả dữ liệu.</p>
    </section>
  );
}

export function ErrorState({ message, onRetry }: ErrorStateProps) {
  return (
    <section className="state-card state-card--error">
      <span className="eyebrow">Không tải được dữ liệu</span>
      <h3>{message}</h3>
      <button className="button" onClick={onRetry} type="button">
        Thử lại
      </button>
    </section>
  );
}
