import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useApi } from "../hooks/useApi";

export default function ForgotPassword() {
  const [step, setStep] = useState(1); // 1: Email, 2: OTP & New Password
  const [email, setEmail] = useState("");
  const [otp, setOtp] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ text: "", type: "" });
  const navigate = useNavigate();
  const api = useApi();

  const handleRequestOtp = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ text: "", type: "" });
    try {
      const res = await api.post("/auth/forgot-password", { email });
      if (res.code === 1000) {
        setStep(2);
        setMessage({ text: "Mã OTP đã được gửi đến email của bạn.", type: "success" });
      } else {
        setMessage({ text: res.message || "Có lỗi xảy ra, vui lòng thử lại.", type: "danger" });
      }
    } catch (error) {
      console.error("Lỗi:", error);
      setMessage({ text: "Lỗi kết nối máy chủ hoặc hệ thống đang khởi động lại. Vui lòng thử lại sau.", type: "danger" });
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ text: "", type: "" });
    try {
      const res = await api.post("/auth/reset-password", { email, otp, newPassword });
      if (res.code === 1000) {
        alert("Đổi mật khẩu thành công. Vui lòng đăng nhập lại.");
        navigate("/login");
      } else {
        setMessage({ text: res.message || "Mã OTP không đúng hoặc đã hết hạn.", type: "danger" });
      }
    } catch (error) {
      setMessage({ text: "Lỗi kết nối hoặc mã OTP sai." + error, type: "danger" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="d-flex align-items-center justify-content-center min-vh-100 py-5" style={{ background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)" }}>
      <div className="card shadow-lg border-0" style={{ width: "100%", maxWidth: "400px", borderRadius: "16px", margin: "15px" }}>
        <div className="card-body p-5">
          <div className="text-center mb-4">
            <h3 className="fw-bold" style={{ color: "#764ba2" }}>Quên Mật Khẩu</h3>
            <p className="text-muted small">Cập nhật lại mật khẩu của bạn để truy cập</p>
          </div>

          {message.text && (
            <div className={`alert alert-${message.type} alert-dismissible fade show p-2 small`} role="alert">
              {message.text}
              <button type="button" className="btn-close p-2" onClick={() => setMessage({ text: "", type: "" })}></button>
            </div>
          )}

          {step === 1 ? (
            <form onSubmit={handleRequestOtp}>
              <div className="mb-4">
                <label className="form-label small fw-bold text-secondary">Địa chỉ Email</label>
                <input
                  type="email"
                  className="form-control"
                  style={{ borderRadius: "10px", padding: "10px 15px" }}
                  placeholder="Nhập email của bạn..."
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
              <button type="submit" className="btn btn-primary w-100 fw-bold shadow-sm" style={{ borderRadius: "10px", padding: "10px", backgroundColor: "#764ba2", border: "none" }} disabled={loading}>
                {loading ? <span className="spinner-border spinner-border-sm me-2"></span> : null}
                Gửi mã xác nhận OTP
              </button>
            </form>
          ) : (
            <form onSubmit={handleResetPassword}>
              <div className="mb-3">
                <label className="form-label small fw-bold text-secondary">Mã OTP</label>
                <input
                  type="text"
                  className="form-control"
                  style={{ borderRadius: "10px", padding: "10px 15px" }}
                  placeholder="Nhập 6 số OTP trong email..."
                  value={otp}
                  onChange={(e) => setOtp(e.target.value)}
                  required
                  maxLength={6}
                />
              </div>
              <div className="mb-4">
                <label className="form-label small fw-bold text-secondary">Mật khẩu mới</label>
                <input
                  type="password"
                  className="form-control"
                  style={{ borderRadius: "10px", padding: "10px 15px" }}
                  placeholder="Tạo mật khẩu mới..."
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  required
                  minLength={6}
                />
              </div>
              <button type="submit" className="btn btn-success w-100 fw-bold shadow-sm" style={{ borderRadius: "10px", padding: "10px" }} disabled={loading}>
                {loading ? <span className="spinner-border spinner-border-sm me-2"></span> : null}
                Đổi mật khẩu
              </button>
              <div className="text-center mt-3">
                <button type="button" className="btn btn-link text-decoration-none small" onClick={() => { setStep(1); setOtp(""); setNewPassword(""); }}>
                  Gửi lại mã OTP
                </button>
              </div>
            </form>
          )}

          <div className="text-center mt-4 border-top pt-3">
            <span className="small text-muted">Nhớ mật khẩu? </span>
            <Link to="/login" className="small fw-bold text-decoration-none" style={{ color: "#764ba2" }}>Đăng nhập ngay</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
