import { useState, useEffect } from "react";

export default function ProfileModal({ api, onClose, onUpdateSuccess }) {
  const [profile, setProfile] = useState({
    username: "",
    fullName: "",
    email: "",
    phone: "",
    address: "",
    password: ""
  });
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);

  useEffect(() => {
    api.get("/users/my-info")
      .then(res => {
        if (res.result) {
          setProfile({ ...res.result, password: "" });
        }
      })
      .finally(() => setLoading(false));
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProfile(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setUpdating(true);
    try {
      const { username, ...updateData } = profile;

      const res = await api.put(`/users/${username}`, updateData);
      if (res.result) {
        alert("Cập nhật thông tin thành công!");
        onUpdateSuccess(res.result);
        onClose();
      }
    } catch (err) {
      alert("Lỗi khi cập nhật thông tin.");
    } finally {
      setUpdating(false);
    }
  };

  if (loading) return null;

  return (
    <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.6)', backdropFilter: 'blur(4px)', zIndex: 1200 }}>
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content border-0 shadow-lg" style={{ borderRadius: '20px' }}>
          <div className="modal-header border-0 p-4 pb-0">
            <h5 className="fw-bold mb-0">👤 Thông tin cá nhân</h5>
            <button type="button" className="btn-close" onClick={onClose}></button>
          </div>
          <form onSubmit={handleSubmit}>
            <div className="modal-body p-4">
              <div className="mb-3">
                <label className="form-label small fw-bold text-secondary">
                  Username <span className="text-muted">(không thể đổi)</span>
                </label>

                <div className="input-group">
                  <span className="input-group-text bg-secondary text-white">
                    🔒
                  </span>
                  <input
                    type="text"
                    className="form-control bg-light text-muted"
                    value={profile.username}
                    disabled
                  />
                </div>
              </div>
              <div className="mb-3">
                <label className="form-label small fw-bold text-muted">Họ và tên</label>
                <input type="text" name="fullName" className="form-control bg-light border-0 shadow-none" value={profile.fullName} onChange={handleChange} required />
              </div>
             <div className="mb-3">
  <label className="form-label small fw-bold text-secondary">
    Email <span className="text-muted">(không thể đổi)</span>
  </label>

  <div className="input-group">
    <span className="input-group-text bg-secondary text-white">
      📧
    </span>
    <input
      type="email"
      className="form-control bg-light text-muted"
      value={profile.email}
      disabled
    />
  </div>
</div>
              
              <div className="row g-3 mb-3">
                <div className="col-md-6">
                  <label className="form-label small fw-bold text-muted">Số điện thoại</label>
                  <input type="text" name="phone" className="form-control bg-light border-0 shadow-none" value={profile.phone} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <label className="form-label small fw-bold text-muted">Mật khẩu mới (Để trống nếu không đổi)</label>
                  <input type="password" name="password" className="form-control bg-light border-0 shadow-none" value={profile.password} onChange={handleChange} />
                </div>
              </div>
              <div className="mb-0">
                <label className="form-label small fw-bold text-muted">Địa chỉ</label>
                <textarea name="address" className="form-control bg-light border-0 shadow-none" rows="2" value={profile.address} onChange={handleChange}></textarea>
              </div>
            </div>
            <div className="modal-footer border-0 p-4 pt-0">
              <button type="button" className="btn btn-light px-4 rounded-pill fw-bold" onClick={onClose} disabled={updating}>Hủy</button>
              <button type="submit" className="btn btn-primary px-5 rounded-pill fw-bold shadow" disabled={updating}>
                {updating ? "Đang lưu..." : "Lưu thay đổi"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
