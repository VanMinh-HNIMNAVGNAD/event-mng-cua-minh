import { useState, useEffect } from "react";
import { RoleBadge, Pagination } from "../../utils/helpers";

// ── Quick Stat Card ──────────────────────────────────────────────────────────
const UserStat = ({ label, value, color, icon }) => (
  <div className="card border-0 shadow-sm h-100" style={{ borderRadius: '12px', borderLeft: `4px solid ${color}` }}>
    <div className="card-body p-3 d-flex align-items-center justify-content-between">
      <div>
        <p className="text-muted small mb-0 fw-medium text-uppercase" style={{ fontSize: '10px' }}>{label}</p>
        <h5 className="fw-bold mb-0">{value}</h5>
      </div>
      <span style={{ fontSize: '20px' }}>{icon}</span>
    </div>
  </div>
);

// ── Avatar Component ────────────────────────────────────────────────────────
const UserAvatar = ({ name, size = 35 }) => {
  const initial = name ? name.charAt(0).toUpperCase() : '?';
  const colors = ['#6c5ce7', '#00b894', '#0984e3', '#fdcb6e', '#e17055'];
  const bgColor = colors[name ? name.length % colors.length : 0];

  return (
    <div className="rounded-circle d-flex align-items-center justify-content-center text-white fw-bold shadow-sm"
      style={{ width: size, height: size, backgroundColor: bgColor, fontSize: size * 0.4 }}>
      {initial}
    </div>
  );
};

export default function UsersPage({ api }) {
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [roleFilter, setRoleFilter] = useState("");
  const [disablingId, setDisablingId] = useState(null);
  const [refetch, setRefetch] = useState(0);

  useEffect(() => {
    setLoading(true);
    // Fetch with potential server-side search if backend supports it, else handle client-side
    api.get(`/users?page=${page}&size=10`).then((res) => {
      setUsers(res.result?.content ?? []);
      setTotalPages(res.result?.totalPages ?? 1);
      setLoading(false);
    });
  }, [page, refetch]);

  const handleDisable = async (username) => {
    if (!window.confirm(`Xác nhận khóa tài khoản "${username}"?`)) return;
    setDisablingId(username);
    try {
      await api.del(`/users/${username}`);
      setRefetch(n => n + 1);
    } finally {
      setDisablingId(null);
    }
  };

  const handleUnlock = async (username) => {
    if (!window.confirm(`Xác nhận mở khóa tài khoản "${username}"?`)) return;
    setDisablingId(username);
    try {
      await api.patch(`/users/${username}/unlock`);
      setRefetch(n => n + 1);
    } finally {
      setDisablingId(null);
    }
  };

  const filteredUsers = users.filter(u => {
    const matchesSearch = u.username.toLowerCase().includes(search.toLowerCase()) || 
                          (u.fullName ?? "").toLowerCase().includes(search.toLowerCase());
    const matchesRole = roleFilter === "" || u.role === roleFilter;
    return matchesSearch && matchesRole;
  });

  return (
    <div className="animate-fade-in">
      <div className="mb-4 d-flex justify-content-between align-items-end">
        <div>
          <h4 className="fw-bold mb-1">Quản lý người dùng</h4>
          <p className="text-secondary small mb-0">Quản lý và cấp quyền truy cập hệ thống.</p>
        </div>
      </div>

      {/* Stats Row */}
      <div className="row g-3 mb-4">
        <div className="col-md-6">
          <UserStat label="Tổng người dùng" value={users.length} color="#6c5ce7" icon="" />
        </div>
        <div className="col-md-6">
          <UserStat label="Ban tổ chức (Organizer)" value={users.filter(u => u.role === 'ORGANIZER').length} color="#00b894" icon="" />
        </div>
      </div>

      <div className="card border-0 shadow-sm" style={{ borderRadius: '16px', overflow: 'hidden' }}>
        <div className="card-header bg-white p-4 border-0">
          <div className="row g-3">
            <div className="col-md-6">
              <div className="input-group input-group-sm border rounded-pill px-2 py-1 bg-light">
                <span className="input-group-text bg-transparent border-0 text-muted">🔍</span>
                <input
                  type="text"
                  className="form-control bg-transparent border-0 shadow-none"
                  placeholder="Tìm theo tên hoặc username..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
              </div>
            </div>
            <div className="col-md-3">
              <select 
                className="form-select form-select-sm border-0 bg-light rounded-pill px-3 shadow-none"
                value={roleFilter}
                onChange={(e) => setRoleFilter(e.target.value)}
              >
                <option value="">Tất cả vai trò</option>
                <option value="CUSTOMER">Khách hàng</option>
                <option value="ORGANIZER">Ban tổ chức</option>
                <option value="ADMIN">Quản trị viên</option>
              </select>
            </div>
            <div className="col-md-3 text-md-end">
              <button className="btn btn-sm btn-outline-secondary rounded-pill px-4 fw-bold" onClick={() => setRefetch(n => n + 1)}>
                Làm mới ↻
              </button>
            </div>
          </div>
        </div>

        <div className="table-responsive">
          <table className="table table-hover align-middle mb-0">
            <thead className="bg-light text-secondary small text-uppercase">
              <tr>
                <th className="px-4 py-3 border-0">Người dùng</th>
                <th className="border-0">Liên hệ</th>
                <th className="border-0 text-center">Vai trò</th>
                <th className="border-0">Ngày tham gia</th>
                <th className="border-0 text-end px-4">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan="5" className="text-center py-5 text-muted border-0">
                  <div className="spinner-border text-primary spinner-border-sm me-2"></div> Đang đồng bộ...
                </td></tr>
              ) : filteredUsers.length === 0 ? (
                <tr><td colSpan="5" className="text-center py-5 text-muted border-0">Không tìm thấy người dùng phù hợp.</td></tr>
              ) : filteredUsers.map((u) => (
                <tr key={u.id}>
                  <td className="px-4 border-0">
                    <div className="d-flex align-items-center gap-3">
                      <UserAvatar name={u.fullName || u.username} />
                      <div>
                        <div className="fw-bold text-dark">@{u.username}</div>
                        <div className="text-muted small">{u.fullName || "Chưa cập nhật tên"}</div>
                      </div>
                    </div>
                  </td>
                  <td className="border-0">
                    <div className="small fw-medium text-dark">{u.email}</div>
                    <div className="text-muted small">{u.phone || "—"}</div>
                    {!u.enabled && <span className="badge bg-danger-subtle text-danger mt-1">Đã khóa</span>}
                  </td>
                  <td className="border-0 text-center">
                    <RoleBadge role={u.role} />
                  </td>
                  <td className="border-0 text-muted small">
                    {u.createdAt ? new Date(u.createdAt).toLocaleDateString() : '—'}
                  </td>
                  <td className="border-0 text-end px-4">
                    <div className="d-flex justify-content-end gap-2">
                       {u.enabled ? (
                         <button
                           className="btn btn-sm btn-outline-danger px-3 rounded-1"
                           disabled={disablingId === u.username || u.role === "ADMIN"}
                           onClick={() => handleDisable(u.username)}
                         >
                           {disablingId === u.username ? "..." : "Khóa"}
                         </button>
                       ) : (
                         <button
                           className="btn btn-sm btn-outline-success px-3 rounded-1"
                           disabled={disablingId === u.username}
                           onClick={() => handleUnlock(u.username)}
                         >
                           {disablingId === u.username ? "..." : "Mở khóa"}
                         </button>
                       )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="p-4 border-top">
          <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>
    </div>
  );
}