import { useState } from "react";

import { API_BASE } from "../utils/api";

const API_URL = `${API_BASE}/auth/register`;

export default function Register() {
  const [form, setForm] = useState({
    username: "",
    password: "",
    email: "",
    fullName: "",
    phone: "",
    address: "",
    role: "ORGANIZER",
  });

  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");
  const [serverError, setServerError] = useState("");

  const validate = () => {
    const errs = {};

    if (!form.username || form.username.length < 3)
      errs.username = "Tên đăng nhập tối thiểu 3 ký tự";

    if (!form.password || form.password.length < 6)
      errs.password = "Mật khẩu tối thiểu 6 ký tự";

    if (!form.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))
      errs.email = "Email không hợp lệ";

    if (!form.fullName) errs.fullName = "Vui lòng nhập họ tên";

    if (form.phone && !/^[0-9]{10,11}$/.test(form.phone))
      errs.phone = "Số điện thoại 10-11 chữ số";

    return errs;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));

    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: "",
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setServerError("");
    setSuccess("");

    const errs = validate();

    if (Object.keys(errs).length > 0) {
      setErrors(errs);
      return;
    }

    setLoading(true);

    try {
      const res = await fetch(API_URL, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(form),
      });

      const data = await res.json();

      if (res.ok && data.code === 1000) {
        setSuccess(data.result || "Đăng ký thành công!");

        setForm({
          username: "",
          password: "",
          email: "",
          fullName: "",
          phone: "",
          address: "",
        });
      } else {
        setServerError(data.message || "Đăng ký thất bại.");
      }
    } catch {
      setServerError("Không thể kết nối đến server.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container d-flex justify-content-center align-items-center min-vh-100 py-5">
      <div
        className="card shadow-lg p-4"
        style={{ maxWidth: "700px", width: "100%" }}
      >
        <div className="text-center mb-4">
          <h3>🌼 Đăng ký tài khoản</h3>
          <p className="text-muted">Tạo tài khoản để mua sắm</p>
        </div>

        {success && <div className="alert alert-success">{success}</div>}

        {serverError && <div className="alert alert-danger">{serverError}</div>}

        <form onSubmit={handleSubmit}>
          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Tên đăng nhập</label>
              <input
                name="username"
                value={form.username}
                onChange={handleChange}
                className={`form-control ${errors.username ? "is-invalid" : ""}`}
              />
              <div className="invalid-feedback">{errors.username}</div>
            </div>

            <div className="col-md-6 mb-3">
              <label className="form-label">Họ và tên</label>
              <input
                name="fullName"
                value={form.fullName}
                onChange={handleChange}
                className={`form-control ${errors.fullName ? "is-invalid" : ""}`}
              />
              <div className="invalid-feedback">{errors.fullName}</div>
            </div>

            <div className="col-md-6 mb-3">
              <label className="form-label">Email</label>
              <input
                name="email"
                type="email"
                value={form.email}
                onChange={handleChange}
                className={`form-control ${errors.email ? "is-invalid" : ""}`}
              />
              <div className="invalid-feedback">{errors.email}</div>
            </div>

            <div className="col-md-6 mb-3">
              <label className="form-label">Mật khẩu</label>
              <input
                name="password"
                type="password"
                value={form.password}
                onChange={handleChange}
                className={`form-control ${errors.password ? "is-invalid" : ""}`}
              />
              <div className="invalid-feedback">{errors.password}</div>
            </div>

            <div className="col-md-6 mb-3">
              <label className="form-label">Số điện thoại</label>
              <input
                name="phone"
                value={form.phone}
                onChange={handleChange}
                className={`form-control ${errors.phone ? "is-invalid" : ""}`}
              />
              <div className="invalid-feedback">{errors.phone}</div>
            </div>

            <div className="col-md-6 mb-3">
              <label className="form-label">Địa chỉ</label>
              <input
                name="address"
                value={form.address}
                onChange={handleChange}
                className="form-control"
              />
            </div>
          </div>

          <button className="btn btn-primary w-100" disabled={loading}>
            {loading ? "Đang xử lý..." : "Đăng ký"}
          </button>
        </form>

        <div className="text-center mt-3">
          Đã có tài khoản? <a href="/login">Đăng nhập</a>
        </div>
      </div>
    </div>
  );
}
