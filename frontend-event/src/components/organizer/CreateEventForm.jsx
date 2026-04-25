import React from "react";

export default function CreateEventForm({ 
  formData, handleChange, handleSubmit, categories, loading,
  handleAddTicketType, handleRemoveTicketType, handleTicketFieldChange 
}) {
  return (
    <div className="card border-0 shadow-sm p-4 col-xl-10 mx-auto animate__animated animate__fadeIn" style={{ borderRadius: '24px' }}>
      <div className="d-flex align-items-center gap-3 mb-4">
        <h4 className="fw-bold mb-0">Tạo sự kiện mới</h4>
      </div>
      
      <form onSubmit={handleSubmit}>
        <div className="row g-4">
          <div className="col-12">
            <label className="form-label small fw-bold text-muted text-uppercase">Tên sự kiện</label>
            <input type="text" name="name" className="form-control form-control-lg bg-light border-0" required value={formData.name} onChange={handleChange} placeholder="Ví dụ: Đêm ca nhạc Chillies" />
          </div>
          
          <div className="col-md-6">
            <label className="form-label small fw-bold text-muted text-uppercase">Danh mục</label>
            <select name="categoryId" className="form-select form-select-lg bg-light border-0 shadow-none text-muted" required value={formData.categoryId} onChange={handleChange}>
              <option value="">Chọn danh mục</option>
              {categories.map(c => <option key={c.id} value={c.id} className="text-dark">{c.name}</option>)}
            </select>
          </div>
          
          <div className="col-md-6">
            <label className="form-label small fw-bold text-muted text-uppercase">Địa điểm tổ chức</label>
            <input type="text" name="location" className="form-control form-control-lg bg-light border-0" required value={formData.location} onChange={handleChange} placeholder="Địa chỉ cụ thể nơi diễn ra" />
          </div>

          {/* Lịch bán vé */}
          <div className="col-md-6">
            <div className="p-3 bg-primary bg-opacity-10 rounded-4 border border-primary border-opacity-10">
              <label className="form-label small fw-bold text-primary text-uppercase mb-2">Ngày Bắt đầu bán vé</label>
              <input type="datetime-local" name="saleStartDate" className="form-control border-white shadow-sm" required value={formData.saleStartDate} onChange={handleChange} />
            </div>
          </div>
          <div className="col-md-6">
            <div className="p-3 bg-danger bg-opacity-10 rounded-4 border border-danger border-opacity-10">
              <label className="form-label small fw-bold text-danger text-uppercase mb-2">Ngày Kết thúc bán vé</label>
              <input type="datetime-local" name="saleEndDate" className="form-control border-white shadow-sm" required value={formData.saleEndDate} onChange={handleChange} />
            </div>
          </div>

          {/* Lịch diễn ra sự kiện */}
          <div className="col-md-6">
            <div className="p-3 bg-light rounded-4 border">
              <label className="form-label small fw-bold text-secondary text-uppercase mb-2">Thời gian diễn ra</label>
              <input type="datetime-local" name="startTime" className="form-control border-white shadow-sm" required value={formData.startTime} onChange={handleChange} />
            </div>
          </div>
          <div className="col-md-6">
            <div className="p-3 bg-light rounded-4 border">
              <label className="form-label small fw-bold text-secondary text-uppercase mb-2">Thời gian kết thúc</label>
              <input type="datetime-local" name="endTime" className="form-control border-white shadow-sm" required value={formData.endTime} onChange={handleChange} />
            </div>
          </div>
          
          <div className="col-12">
            <label className="form-label small fw-bold text-muted text-uppercase">Ảnh mô tả</label>
            <div className="p-2 bg-light rounded-3 border border-dashed text-center">
                <input type="file" name="files" multiple className="form-control bg-transparent border-0 shadow-none mt-1" onChange={handleChange} accept="image/*" />
                {/* <small className="text-muted small mt-2 d-block">Có thể chọn nhiều tệp (.jpg, .png)</small> */}
            </div>
          </div>
          
          <div className="col-12">
            <label className="form-label small fw-bold text-muted text-uppercase">Mô tả chi tiết</label>
            <textarea name="description" className="form-control bg-light border-0 rounded-4" rows="4" value={formData.description} onChange={handleChange} placeholder="mô tả sự kiện (nếu có)"></textarea>
          </div>

          <div className="col-12 mt-4">
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h5 className="fw-bold mb-0">🎟️ Cấu hình vé</h5>
              <button type="button" className="btn btn-sm btn-outline-primary rounded-pill px-3" onClick={handleAddTicketType}>+ Thêm loại vé</button>
            </div>
            
            {formData.ticketTypes.map((tt, index) => (
              <div key={index} className="p-4 bg-white border rounded-4 mb-3 position-relative">
                {formData.ticketTypes.length > 1 && (
                  <button type="button" className="btn-close position-absolute top-0 end-0 m-3" onClick={() => handleRemoveTicketType(index)}></button>
                )}
                <div className="row g-3">
                  <div className="col-md-5">
                    <label className="small fw-bold text-muted">Tên vé (VD: Vé VIP)</label>
                    <input type="text" className="form-control bg-light border-0" required value={tt.name} onChange={(e) => handleTicketFieldChange(index, 'name', e.target.value)} />
                  </div>
                  <div className="col-md-4">
                    <label className="small fw-bold text-muted">Giá vé (VNĐ)</label>
                    <input type="number" className="form-control bg-light border-0" required value={tt.price} onChange={(e) => handleTicketFieldChange(index, 'price', e.target.value)} />
                  </div>
                  <div className="col-md-3">
                    <label className="small fw-bold text-muted">Số lượng</label>
                    <input type="number" className="form-control bg-light border-0" required value={tt.totalQuantity} onChange={(e) => handleTicketFieldChange(index, 'totalQuantity', e.target.value)} />
                  </div>
                </div>
              </div>
            ))}
          </div>
          
          <div className="col-12 pt-2">
            <button type="submit" className="btn btn-primary w-100 py-3 rounded-pill fw-bold shadow-lg" disabled={loading}>
              {loading ? (
                  <><span className="spinner-border spinner-border-sm me-2"></span> ĐANG XỬ LÝ...</>
              ) : (
                  "ĐĂNG KÝ DUYỆT SỰ KIỆN"
              )}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
