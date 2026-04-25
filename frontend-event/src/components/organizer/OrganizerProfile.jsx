import React from "react";

export default function OrganizerProfile({ profile, setProfile, handleProfileUpdate }) {
  if (!profile) return <div className="text-center py-5">Đang tải hồ sơ...</div>;
  
  return (
    <div className="card shadow-sm border-0 p-4 col-xl-6 mx-auto animate__animated animate__fadeIn" style={{ borderRadius: '20px' }}>
      <h4 className="fw-bold mb-4 d-flex align-items-center gap-3">
        Thông tin cá nhân
      </h4>
      <form onSubmit={handleProfileUpdate}>
        <div className="mb-4">
          <label className="form-label small fw-bold text-muted text-uppercase">Tên đăng nhập (Username)</label>
          <input type="text" className="form-control form-control-lg bg-light border-0" value={profile.username} disabled style={{ cursor: 'not-allowed' }} />
        </div>
        
        <div className="mb-4">
          <label className="form-label small fw-bold text-muted text-uppercase">Họ và tên</label>
          <input type="text" className="form-control form-control-lg bg-light border-0" value={profile.fullName} onChange={e => setProfile({...profile, fullName: e.target.value})} placeholder="Họ và tên của bạn" />
        </div>
        
        <div className="mb-4">
          <label className="form-label small fw-bold text-muted text-uppercase">Địa chỉ Email</label>
          <input type="email" className="form-control form-control-lg bg-light border-0" value={profile.email} onChange={e => setProfile({...profile, email: e.target.value})} placeholder="Vd: example@gmail.com" />
        </div>
        
        <div className="mb-4">
          <label className="form-label small fw-bold text-muted text-uppercase">Số điện thoại liên hệ</label>
          <input type="text" className="form-control form-control-lg bg-light border-0" value={profile.phone} onChange={e => setProfile({...profile, phone: e.target.value})} placeholder="Vd: 0987xxx" />
        </div>

        <hr className="my-4" />
        <h6 className="fw-bold mb-3 text-primary">Đổi mật khẩu (Bỏ trống nếu không đổi)</h6>
        
        <div className="mb-4">
          <label className="form-label small fw-bold text-muted text-uppercase">Mật khẩu mới</label>
          <input type="password" name="password" className="form-control form-control-lg bg-light border-0" onChange={e => setProfile({...profile, password: e.target.value})} placeholder="Nhập mật khẩu mới..." />
        </div>

        <button type="submit" className="btn btn-primary w-100 py-3 rounded-pill fw-bold shadow-lg mt-2">LƯU</button>
      </form>
    </div>
  );
}
