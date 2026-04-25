import { Link } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

export default function Navbar() {
  const { user, logout } = useAuth();

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow">
      <div className="container">
        <Link className="navbar-brand fw-bold" to="/">🎟️ EVENT-MNG</Link>
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav me-auto">
            <li className="nav-item">
              <Link className="nav-link" to="/">Sự kiện</Link>
            </li>
          </ul>
          <div className="d-flex align-items-center gap-3">
            {user ? (
              <>
                <Link to="/cart" className="btn btn-outline-light position-relative">
                  🛒 Giỏ hàng
                </Link>
                <div className="dropdown">
                  <button className="btn btn-primary dropdown-toggle rounded-pill" type="button" data-bs-toggle="dropdown">
                    👤 {user.username}
                  </button>
                  <ul className="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
                    {user.scope === "ADMIN" && <li><Link className="dropdown-menu-item dropdown-item" to="/admin">Admin Panel</Link></li>}
                    {user.scope === "ORGANIZER" && <li><Link className="dropdown-menu-item dropdown-item" to="/organizer">Organizer Manager</Link></li>}
                    <li><hr className="dropdown-divider" /></li>
                    <li><button className="dropdown-item text-danger" onClick={logout}>Đăng xuất</button></li>
                  </ul>
                </div>
              </>
            ) : (
              <Link to="/login" className="btn btn-primary rounded-pill px-4">Đăng nhập</Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
