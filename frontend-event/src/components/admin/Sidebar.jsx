import { useState } from "react";
import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";

const MENU = [
  { key: "dashboard",  label: "Tổng quan"},
  { key: "events",     label: "Sự kiện"},
  { key: "users",      label: "Người dùng"},
  { key: "categories", label: "Danh mục"},
  { key: "statis",     label: "Thống kê"},
];

export default function Sidebar({ active, onSelect, onProfileClick }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(true);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div
      className="d-flex flex-column bg-white border-end shadow-sm"
      style={{ width: open ? 260 : 80, minHeight: "100vh", transition: "width 0.3s cubic-bezier(0.4, 0, 0.2, 1)", flexShrink: 0, zIndex: 100 }}
    >
      {/* Logo */}
      <div className="d-flex align-items-center justify-content-between p-3 mb-2" style={{ minHeight: 64 }}>
        {open && (
          <div className="ps-2">
            <div className="fw-bold" style={{ fontSize: 18, color: '#0984e3', letterSpacing: '-0.5px' }}>EVENT MANAGER</div>
            <small className="text-secondary fw-medium" style={{ fontSize: 10, display: 'block', marginTop: '-4px' }}>ADMINISTRATION</small>
          </div>
        )}
        <button className="btn btn-sm btn-light ms-auto rounded-circle border-0 shadow-sm" onClick={() => setOpen((v) => !v)} style={{ width: 32, height: 32 }}>
          {open ? "◀" : "▶"}
        </button>
      </div>

      {/* Nav */}
      <nav className="flex-grow-1 px-3 mt-2">
        {MENU.map((m) => (
          <button
            key={m.key}
            onClick={() => onSelect(m.key)}
            className={`btn w-100 text-start mb-2 d-flex align-items-center gap-3 transition-all ${
              active === m.key ? "btn-primary shadow" : "btn-light border-0 text-secondary"
            }`}
            style={{ 
              padding: '12px 16px', 
              fontSize: 14, 
              borderRadius: 12,
              fontWeight: active === m.key ? 600 : 500
            }}
            title={!open ? m.label : undefined}
          >
            <span style={{ fontSize: 18, filter: active === m.key ? 'none' : 'grayscale(1)' }}>{m.icon}</span>
            {open && <span>{m.label}</span>}
          </button>
        ))}
      </nav>

      {/* Footer */}
      <div className="p-3 border-top bg-light-subtle">
        {open && (
           <div 
             className="d-flex align-items-center gap-2 mb-3 px-2 cursor-pointer transition-all hover-bg-light rounded-3 p-1" 
             style={{ cursor: 'pointer' }}
             onClick={onProfileClick}
           >
              <div className="rounded-circle bg-warning text-dark d-flex align-items-center justify-content-center fw-bold shadow-sm" style={{ width: 34, height: 34, fontSize: 13 }}>A</div>
              <div className="overflow-hidden">
                <div className="small fw-bold text-dark text-truncate">{user?.sub ?? "Administrator"}</div>
                <div className="text-secondary" style={{ fontSize: 10 }}>Admin Profile</div>
              </div>
           </div>
        )}
        <button
          className={`btn btn-outline-danger border-2 ${open ? "w-100" : "w-100 rounded-circle p-2"}`}
          onClick={handleLogout}
          style={{ borderRadius: open ? 10 : '50%', fontSize: 13, fontWeight: 600 }}
          title="Đăng xuất"
        >
          {open ? "Đăng xuất" : "✕"}
        </button>
      </div>
    </div>
  );
}

export { MENU };