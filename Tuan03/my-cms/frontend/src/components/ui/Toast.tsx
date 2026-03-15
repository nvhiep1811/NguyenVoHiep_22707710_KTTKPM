interface ToastProps {
  message: string;
}

export function Toast({ message }: ToastProps) {
  return <div className={`toast${message ? ' toast--visible' : ''}`}>{message}</div>;
}
