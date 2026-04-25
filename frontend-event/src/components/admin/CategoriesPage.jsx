import { useState, useEffect } from "react";

export default function CategoriesPage({ api }) {
  const [cats, setCats] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState({ text: "", type: "" });
  const [refetch, setRefetch] = useState(0);

  const EMPTY = { id: null, name: "", description: "" };
  const [form, setForm] = useState(EMPTY);
  const isEditing = form.id !== null;

  useEffect(() => {
    setLoading(true);
    api.get("/categories").then((res) => {
      setCats(res.result ?? []);
      setLoading(false);
    });
  }, [refetch]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMsg({ text: "", type: "" });

    try {
      const body = { name: form.name, description: form.description };
      const res = isEditing
        ? await api.put(`/categories/${form.id}`, body)
        : await api.post("/categories", body);

      if (res.code === 1000) {
        setMsg({ text: `${isEditing ? "Cập nhật" : "Tạo mới"} danh mục thành công!`, type: "success" });
        setForm(EMPTY);
        setRefetch((n) => n + 1);
      } else {
        setMsg({ text: `${res.message}`, type: "danger" });
      }
    } catch (err) {
      setMsg({ text: "Lỗi kết nối hệ thống", type: "danger" });
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (cat) => {
    if (!window.confirm(`Xác nhận xóa danh mục "${cat.name}"? Hành động này không thể hoàn tác.`)) return;
    try {
      const res = await api.del(`/categories/${cat.id}`);
      if (res.code === 1000 || res.status === 200) {
        setMsg({ text: "Đã xóa danh mục thành công.", type: "success" });
        setRefetch((n) => n + 1);
      } else {
        setMsg({ text: `Không thể xóa: ${res.message}`, type: "danger" });
      }
    } catch (err) {
      setMsg({ text: "Lỗi khi thực hiện xóa", type: "danger" });
    }
  };

  const filtered = cats.filter((c) =>
    c.name.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="animate-fade-in p-2">
      <div className="mb-4">
        <h4 className="fw-bold mb-1">Quản lý danh mục</h4>
        <p className="text-secondary small">Thiết lập các loại hình sự kiện để người dùng dễ dàng tìm kiếm.</p>
      </div>

      <div className="row g-4">
        {/* Form Card */}
        <div className="col-12 col-lg-4">
          <div className="card shadow-sm border-0" style={{ borderRadius: '16px' }}>
            <div className="card-body p-4">
              <h6 className="fw-bold mb-3 d-flex align-items-center">
                <span className="me-2">{isEditing ? "Chỉnh sửa" : "Thêm mới"}</span>
              </h6>
              
              {msg.text && (
                <div className={`alert alert-${msg.type} alert-dismissible fade show small py-2`} role="alert">
                  {msg.text}
                  <button type="button" className="btn-close" style={{ padding: '0.7rem' }} onClick={() => setMsg({text: "", type: ""})}></button>
                </div>
              )}

              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label className="form-label small fw-bold text-secondary">Tên danh mục</label>
                  <input
                    className="form-control border-0 bg-light shadow-none rounded-3"
                    placeholder="VD: Âm nhạc, Hội thảo..."
                    value={form.name}
                    onChange={(e) => setForm((p) => ({ ...p, name: e.target.value }))}
                    required
                    minLength={2}
                  />
                </div>
                <div className="mb-4">
                  <label className="form-label small fw-bold text-secondary">Mô tả chi tiết</label>
                  <textarea
                    className="form-control border-0 bg-light shadow-none rounded-3"
                    rows={4}
                    placeholder="Mô tả ngắn gọn về loại hình này..."
                    value={form.description}
                    onChange={(e) => setForm((p) => ({ ...p, description: e.target.value }))}
                  />
                </div>
                
                <div className="d-grid gap-2">
                  <button className={`btn ${isEditing ? 'btn-warning' : 'btn-primary'} fw-bold rounded-pill`} disabled={saving}>
                    {saving ? <span className="spinner-border spinner-border-sm me-2"></span> : null}
                    {isEditing ? "CẬP NHẬT" : "XÁC NHẬN TẠO"}
                  </button>
                  {isEditing && (
                    <button type="button" className="btn btn-link text-secondary text-decoration-none shadow-none btn-sm"
                      onClick={() => { setForm(EMPTY); setMsg({text:"", type:""}); }}>
                      Hủy bỏ & quay lại
                    </button>
                  )}
                </div>
              </form>
            </div>
          </div>
        </div>

        {/* List Card */}
        <div className="col-12 col-lg-8">
          <div className="card shadow-sm border-0" style={{ borderRadius: '16px', overflow: 'hidden' }}>
            <div className="card-header bg-white p-4 border-0 pb-2">
              <div className="d-flex justify-content-between align-items-center">
                <h6 className="fw-bold mb-0">Danh sách hiện có</h6>
                <div className="position-relative" style={{ width: '200px' }}>
                  <input
                    className="form-control form-control-sm border-0 bg-light rounded-pill px-3 shadow-none"
                    placeholder="Tìm tên..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                  />
                </div>
              </div>
            </div>

            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="bg-light text-secondary small text-uppercase">
                  <tr>
                    <th className="px-4 py-3 border-0">Tên danh mục</th>
                    <th className="border-0">Mô tả</th>
                    <th className="border-0 text-end px-4">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {loading ? (
                    <tr><td colSpan="3" className="text-center py-5">
                      <div className="spinner-border text-primary spinner-border-sm"></div>
                    </td></tr>
                  ) : filtered.map((c) => (
                    <tr key={c.id}>
                      <td className="px-4 border-0">
                        <span className="badge bg-primary-subtle text-primary px-3 py-2 rounded-pill fw-bold border border-primary-subtle" style={{ letterSpacing: '0.5px' }}>
                          {c.name}
                        </span>
                      </td>
                      <td className="border-0">
                        <p className="mb-0 text-muted small" style={{ maxWidth: '300px' }}>{c.description || "—"}</p>
                      </td>
                      <td className="border-0 text-end px-4">
                        <button
                          className="btn btn-sm btn-outline-primary rounded-1 me-2 px-3 fw-bold"
                          title="Sửa"
                          onClick={() => {
                            setForm({ id: c.id, name: c.name, description: c.description ?? "" });
                            setMsg({ text: "", type: "" });
                            window.scrollTo({ top: 0, behavior: "smooth" });
                          }}
                        >
                          Sửa
                        </button>
                        <button
                          className="btn btn-sm btn-outline-danger rounded-1 px-3 fw-bold"
                          title="Xóa"
                          onClick={() => handleDelete(c)}
                        >
                          Xóa
                        </button>
                      </td>
                    </tr>
                  ))}
                  {filtered.length === 0 && (
                    <tr><td colSpan="3" className="text-center py-5 text-muted">Không tìm thấy danh mục nào.</td></tr>
                  )}
                </tbody>
              </table>
            </div>
            
            <div className="p-3 bg-light-subtle text-center">
               <small className="text-muted">Tổng cộng: <strong>{filtered.length}</strong> danh mục</small>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}