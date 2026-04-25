import React from "react";
import { VND } from "../../utils/helpers";

export default function TicketManager({ event, ticketFormData, setTicketFormData, handleAddTicket, eventTicketTypes, setActiveTab }) {
  if (!event) return null;

  const isEditable = event.status === 'UPCOMING';
  const showRevenue = event.status === 'COMPLETED';

  return (
    <div className="animate__animated animate__fadeIn">
      <button className="btn btn-sm btn-link mb-4 text-decoration-none p-0 d-flex align-items-center gap-2 text-primary fw-bold" onClick={() => setActiveTab("events")}>
        <span>←</span> <span>Quay lại danh sách</span>
      </button>
      
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h4 className="fw-bold mb-1">
                {isEditable ? "Thiết lập vé & Số lượng" : showRevenue ? "Báo cáo Doanh thu & Vé" : "📊 Thống kê Vé bán"}
            </h4>
            <p className="text-muted mb-0">{event.name}</p>
        </div>
        <span className="badge bg-primary bg-opacity-10 text-primary border border-primary p-2 px-3 fw-bold rounded-pill">#{event.id}</span>
      </div>
      
      <div className="row g-4">
        {/* Add Ticket Form - Only for UPCOMING */}
        {isEditable && (
            <div className="col-lg-4">
            <div className="card shadow-sm border-0 p-4 sticky-top" style={{ borderRadius: '20px', top: '20px' }}>
                <h6 className="fw-bold mb-3 d-flex align-items-center gap-2">🎟️ Thêm loại vé mới</h6>
                <form onSubmit={handleAddTicket}>
                <div className="mb-3">
                    <label className="form-label small fw-bold text-muted">Tên hạng vé</label>
                    <input type="text" className="form-control bg-light border-0" required value={ticketFormData.name} onChange={e => setTicketFormData({...ticketFormData, name: e.target.value})} placeholder="Vd: Vé VIP, Vé Sớm..." />
                </div>
                <div className="mb-3">
                    <label className="form-label small fw-bold text-muted">Giá tiền (VNĐ)</label>
                    <input type="number" className="form-control bg-light border-0" required value={ticketFormData.price} onChange={e => setTicketFormData({...ticketFormData, price: e.target.value})} placeholder="Vd: 500000" />
                </div>
                <div className="mb-3">
                    <label className="form-label small fw-bold text-muted">Số lượng phát hành</label>
                    <input type="number" className="form-control bg-light border-0" required value={ticketFormData.totalQuantity} onChange={e => setTicketFormData({...ticketFormData, totalQuantity: e.target.value})} placeholder="Vd: 100" />
                </div>
                <div className="mb-3">
                    <label className="form-label small fw-bold text-muted">Ghi chú (Tùy chọn)</label>
                    <textarea className="form-control bg-light border-0" rows="3" value={ticketFormData.description} onChange={e => setTicketFormData({...ticketFormData, description: e.target.value})} placeholder="Mô tả ưu đãi của vé..."></textarea>
                </div>
                <button type="submit" className="btn btn-primary w-100 rounded-pill py-3 fw-bold shadow-sm">THÊM HẠNG VÉ</button>
                </form>
            </div>
            </div>
        )}

        {/* Ticket Type List */}
        <div className={isEditable ? "col-lg-8" : "col-12"}>
          <div className="card border-0 shadow-sm overflow-hidden" style={{ borderRadius: '20px' }}>
            <div className="table-responsive">
              <table className="table table-hover mb-0 align-middle text-nowrap">
                <thead className="bg-light text-uppercase small">
                  <tr>
                    <th className="px-4 py-3 border-0">Loại vé</th>
                    <th className="py-3 border-0">Giá</th>
                    <th className="py-3 border-0 text-center">Đã bán / Tổng</th>
                    {showRevenue && <th className="py-3 border-0 text-end">Doanh thu</th>}
                    {/* <th className="py-3 text-end px-4 border-0">Trạng thái</th> */}
                  </tr>
                </thead>
                <tbody>
                  {eventTicketTypes.map(tt => {
                    const soldCount = tt.totalQuantity - tt.remainingQuantity;
                    const revenue = soldCount * tt.price;

                    return (
                        <tr key={tt.id}>
                          <td className="px-4 py-3">
                            <div className="fw-bold">{tt.name}</div>
                            <small className="text-muted text-wrap">{tt.description}</small>
                          </td>
                          <td className="py-3 fw-bold text-primary">{VND(tt.price)}</td>
                          <td className="py-3 text-center">
                              <span className="fw-bold">{soldCount}</span> / <span className="text-muted">{tt.totalQuantity}</span>
                              <div className="progress mx-auto mt-1" style={{ height: 4, width: 80 }}>
                                  <div className="progress-bar bg-success" style={{ width: `${(soldCount/tt.totalQuantity)*100}%` }}></div>
                              </div>
                          </td>
                          {showRevenue && <td className="py-3 text-end fw-bold text-success">{VND(revenue)}</td>}
                          {/* <td className="py-3 text-end px-4">
                            <span className="badge bg-success bg-opacity-10 text-success p-2 px-3 border border-success border-opacity-25 rounded-pill small">Active</span>
                          </td> */}
                        </tr>
                    );
                  })}
                  {eventTicketTypes.length === 0 && (
                    <tr>
                      <td colSpan="5" className="text-center py-5 text-muted">
                        {isEditable ? "Chưa có loại vé nào. Vui lòng thêm hạng vé." : "Không có thông tin vé cho sự kiện này."}
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
