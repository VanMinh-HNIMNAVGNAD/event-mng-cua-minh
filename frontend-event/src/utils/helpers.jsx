// Format tiền VNĐ
export const VND = (n) =>
  n == null
    ? "—"
    : Number(n).toLocaleString("vi-VN", { style: "currency", currency: "VND" });

export const formatDate = (d) => {
  if (!d) return "—";
  return new Date(d).toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  });
};

// Badge màu theo role
export function RoleBadge({ role }) {
  const map = { ADMIN: "danger", ORGANIZER: "warning", CUSTOMER: "primary" };
  return <span className={`badge bg-${map[role] ?? "secondary"}`}>{role}</span>;
}

// Badge màu theo status
export function StatusBadge({ status }) {
  switch (status) {
    case 'UPCOMING':  return <span className="badge bg-info-subtle text-info px-3 rounded-pill text-uppercase">Sắp mở bán</span>;
    case 'OPENING':   return <span className="badge bg-success-subtle text-success px-3 rounded-pill text-uppercase">Mở bán vé</span>;
    case 'CLOSED':    return <span className="badge bg-secondary-subtle text-secondary px-3 rounded-pill text-uppercase">Sắp diễn ra</span>;
    case 'COMPLETED': return <span className="badge bg-primary-subtle text-primary px-3 rounded-pill text-uppercase">Kết thúc</span>;
    case 'CANCELLED': return <span className="badge bg-danger-subtle text-danger px-3 rounded-pill text-uppercase">Đã hủy</span>;
    case 'PENDING':   return <span className="badge bg-warning-subtle text-warning px-3 rounded-pill text-uppercase">Chờ kiểm duyệt</span>;
    default:          return <span className="badge bg-light text-dark px-3 rounded-pill">{status}</span>;
  }
}

// Component phân trang dùng chung
export function Pagination({ page, totalPages, onPageChange }) {
  return (
    <div className="card-footer bg-white d-flex justify-content-between align-items-center">
      <small className="text-muted">Trang {page} / {totalPages}</small>
      <div>
        <button
          className="btn btn-sm btn-outline-secondary me-1"
          disabled={page === 1}
          onClick={() => onPageChange(page - 1)}
        >
          ‹ Trước
        </button>
        <button
          className="btn btn-sm btn-outline-secondary"
          disabled={page === totalPages}
          onClick={() => onPageChange(page + 1)}
        >
          Sau ›
        </button>
      </div>
    </div>
  );
}