import React from "react";
import { formatDate, StatusBadge } from "../../utils/helpers";
import { getImageUrl } from "../../hooks/useApi";

export default function MyEventsList({ myEvents, openTicketManager }) {
  if (myEvents.length === 0) return <div className="text-center py-5 text-muted">Chưa có sự kiện nào được đăng.</div>;

  return (
    <div className="row g-4 animate__animated animate__fadeIn">
      {myEvents.map(event => (
        <div key={event.id} className="col-md-6 col-lg-4">
          <div className="card h-100 border-0 shadow-sm overflow-hidden" style={{ borderRadius: '20px' }}>
            {/* Event Image */}
            <div className="position-relative" style={{ height: '180px' }}>
              {event.imageUrls?.[0] ? (
                <img 
                  src={getImageUrl(event.imageUrls[0])} 
                  alt={event.name} 
                  className="w-100 h-100" 
                  style={{ objectFit: 'cover' }} 
                />
              ) : (
                <div className="w-100 h-100 bg-light d-flex align-items-center justify-content-center text-muted small px-3 text-center">
                  (Chưa có ảnh mô tả)
                </div>
              )}
              <div className="position-absolute top-0 end-0 p-3">
                <StatusBadge status={event.status} />
              </div>
            </div>

            {/* Event Name & Info */}
            <div className="p-4 d-flex flex-column h-100">
              <h5 className="fw-bold mb-2 flex-grow-1">{event.name}</h5>
              
              <div className="mt-2 mb-3 p-2 bg-light rounded-3" style={{ fontSize: '11px' }}>
                <div className="d-flex justify-content-between mb-1">
                  <span className="text-secondary">Bán vé:</span>
                  <span className="fw-medium">{formatDate(event.saleStartDate)} - {formatDate(event.saleEndDate)}</span>
                </div>
                <div className="d-flex justify-content-between">
                  <span className="text-secondary">Diễn ra:</span>
                  <span className="fw-medium text-primary">{formatDate(event.startTime)} - {formatDate(event.endTime)}</span>
                </div>
              </div>
              
              <div className="small text-secondary mb-3 d-flex align-items-center gap-2">
                 <span>📍</span> {event.location}
              </div>

              {/* Action Button */}
              {event.status === 'UPCOMING' && (
                <button className="btn btn-primary w-100 rounded-pill fw-bold mt-2 shadow-sm" onClick={() => openTicketManager(event)}>
                  Quản lý vé & Số lượng
                </button>
              )}

              {(event.status === 'OPENING' || event.status === 'CLOSED') && (
                <button className="btn btn-outline-primary w-100 rounded-pill fw-bold mt-2" onClick={() => openTicketManager(event)}>
                  Xem số lượng vé
                </button>
              )}

              {event.status === 'COMPLETED' && (
                <button className="btn btn-outline-success w-100 rounded-pill fw-bold mt-2" onClick={() => openTicketManager(event)}>
                  Xem vé & Doanh thu
                </button>
              )}

              {event.status === 'PENDING' && (
                <div className="alert alert-warning py-2 mb-0 border-0 rounded-pill text-center small fw-bold">
                  Đang chờ kiểm duyệt...
                </div>
              )}

              {event.status === 'CANCELLED' && (
                 <div className="alert alert-secondary py-2 mb-0 border-0 rounded-pill text-center small fw-bold opacity-50">
                    Đã hủy bỏ
                 </div>
              )}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
