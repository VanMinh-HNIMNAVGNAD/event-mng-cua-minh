import { useState, useEffect } from "react";
import { StatusBadge, Pagination, formatDate } from "../../utils/helpers";
import { getImageUrl } from "../../hooks/useApi";

// Quick Stat Card
const QuickStat = ({ label, value, color }) => (
  <div className="card border-0 shadow-sm h-100" style={{ borderRadius: '12px', borderLeft: `4px solid ${color}` }}>
    <div className="card-body p-3 d-flex align-items-center justify-content-between">
      <div>
        <p className="text-muted small mb-0 fw-medium text-uppercase" style={{ fontSize: '10px', letterSpacing: '0.5px' }}>{label}</p>
        <h5 className="fw-bold mb-0" style={{ color: '#2d3436' }}>{value}</h5>
      </div>
    </div>
  </div>
);

// Modal Chi tiết
function EventDetailModal({ event, onClose }) {
  if (!event) return null;
  return (
    <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.6)', backdropFilter: 'blur(4px)', zIndex: 1100 }}>
      <div className="modal-dialog modal-lg modal-dialog-centered">
        <div className="modal-content border-0 shadow-lg" style={{ borderRadius: '20px', overflow: 'hidden' }}>
          <div className="row g-0">
            <div className="col-md-5 position-relative">
              {event.imageUrls?.[0] ? (
                <img src={getImageUrl(event.imageUrls[0])} alt={event.name} className="h-100 w-100" style={{ objectFit: 'cover', minHeight: '300px' }} />
              ) : (
                <div className="bg-light h-100 w-100 d-flex align-items-center justify-content-center text-muted">No Image</div>
              )}
              <div className="position-absolute top-3 start-3">
                <StatusBadge status={event.status} />
              </div>
            </div>
            <div className="col-md-7 p-4 bg-white">
              <div className="d-flex justify-content-between align-items-start mb-3">
                <h4 className="fw-bold text-dark">{event.name}</h4>
                <button type="button" className="btn-close shadow-none" onClick={onClose}></button>
              </div>
              
              <div className="row g-3 mb-4">
                <div className="col-6">
                  <small className="text-muted d-block uppercase small fw-bold">📅 Thời gian</small>
                  <span className="small">{formatDate(event.startTime)}</span>
                </div>
                <div className="col-6">
                  <small className="text-muted d-block uppercase small fw-bold">📍 Địa điểm</small>
                  <span className="small text-truncate d-block">{event.location}</span>
                </div>
                <div className="col-6">
                  <small className="text-muted d-block uppercase small fw-bold">📁 Danh mục</small>
                  <span className="badge bg-light text-dark border">{event.categoryName}</span>
                </div>
                <div className="col-6">
                  <small className="text-muted d-block uppercase small fw-bold">🏢 Ban tổ chức</small>
                  <span className="small fw-medium">{event.organizerName}</span>
                </div>
              </div>

              <h6 className="fw-bold mb-3 small text-uppercase" style={{ letterSpacing: '1px' }}>Quản lý vé</h6>
              <div className="p-3 bg-light rounded-3">
                {event.ticketTypes?.length > 0 ? event.ticketTypes.map(tt => {
                  const sold = tt.totalQuantity - tt.remainingQuantity;
                  const percent = tt.totalQuantity > 0 ? Math.round((sold/tt.totalQuantity)*100) : 0;
                  return (
                    <div key={tt.id} className="mb-3 last-child-mb-0">
                      <div className="d-flex justify-content-between small mb-1">
                        <span className="fw-bold">{tt.name}</span>
                        <span className="text-muted">{sold}/{tt.totalQuantity} vé</span>
                      </div>
                      <div className="progress" style={{ height: '6px' }}>
                        <div className="progress-bar bg-primary rounded-pill" style={{ width: `${percent}%` }}></div>
                      </div>
                    </div>
                  )
                }) : <div className="text-muted small">Chưa thiết lập loại vé</div>}
              </div>

              <div className="mt-4 pt-3 border-top">
                <button className="btn btn-primary w-100 rounded-pill fw-bold" onClick={onClose}>Đóng cửa sổ</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

// Modal Tạo sự kiện
// ///////////
// function CreateEventModal({ onClose, onSave, categories }) {
//   const [formData, setFormData] = useState({
//     name: "", categoryId: "", location: "",
//     startTime: "", endTime: "", saleStartDate: "", saleEndDate: "",
//     description: "", files: null
//   });

//   const handleChange = (e) => {
//     const { name, value, files } = e.target;
//     setFormData(prev => ({ ...prev, [name]: files ? files : value }));
//   };

//   const handleSubmit = (e) => {
//     e.preventDefault();
//     onSave(formData);
//   };

//   return (
//     <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.6)', backdropFilter: 'blur(4px)', zIndex: 1100 }}>
//       <div className="modal-dialog modal-lg modal-dialog-centered">
//         <div className="modal-content border-0 shadow-lg" style={{ borderRadius: '20px' }}>
//           <div className="modal-header border-0 p-4 pb-0">
//             <h5 className="fw-bold mb-0">📅 Tạo sự kiện mới</h5>
//             <button type="button" className="btn-close" onClick={onClose}></button>
//           </div>
//           <form onSubmit={handleSubmit}>
//             <div className="modal-body p-4">
//               <div className="row g-3">
//                 <div className="col-md-12">
//                   <label className="form-label small fw-bold text-muted">Tên sự kiện</label>
//                   <input type="text" name="name" className="form-control shadow-none bg-light border-0" required value={formData.name} onChange={handleChange} placeholder="VD: Liveshow Chillies" />
//                 </div>
//                 <div className="col-md-6">
//                   <label className="form-label small fw-bold text-muted">Danh mục</label>
//                   <select name="categoryId" className="form-select shadow-none bg-light border-0" required value={formData.categoryId} onChange={handleChange}>
//                     <option value="">Chọn danh mục</option>
//                     {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
//                   </select>
//                 </div>
//                 <div className="col-md-6">
//                   <label className="form-label small fw-bold text-muted">Địa điểm</label>
//                   <input type="text" name="location" className="form-control shadow-none bg-light border-0" required value={formData.location} onChange={handleChange} placeholder="VD: TP.HCM" />
//                 </div>
                
//                 <div className="col-md-6">
//                   <div className="p-3 bg-primary-subtle rounded-3">
//                     <label className="form-label small fw-bold text-primary">🛒 Ngày Bắt đầu bán vé</label>
//                     <input type="datetime-local" name="saleStartDate" className="form-control border-0 shadow-sm" required value={formData.saleStartDate} onChange={handleChange} />
//                   </div>
//                 </div>
//                 <div className="col-md-6">
//                   <div className="p-3 bg-danger-subtle rounded-3">
//                     <label className="form-label small fw-bold text-danger">🛑 Ngày Kết thúc bán vé</label>
//                     <input type="datetime-local" name="saleEndDate" className="form-control border-0 shadow-sm" required value={formData.saleEndDate} onChange={handleChange} />
//                   </div>
//                 </div>

//                 <div className="col-md-6">
//                   <div className="p-3 bg-light rounded-3">
//                     <label className="form-label small fw-bold text-secondary">🎉 Thời gian diễn ra</label>
//                     <input type="datetime-local" name="startTime" className="form-control border-0 shadow-sm" required value={formData.startTime} onChange={handleChange} />
//                   </div>
//                 </div>
//                 <div className="col-md-6">
//                   <div className="p-3 bg-light rounded-3">
//                     <label className="form-label small fw-bold text-secondary">🏁 Thời gian kết thúc</label>
//                     <input type="datetime-local" name="endTime" className="form-control border-0 shadow-sm" required value={formData.endTime} onChange={handleChange} />
//                   </div>
//                 </div>

//                 <div className="col-12">
//                   <label className="form-label small fw-bold text-muted">Hình ảnh sự kiện</label>
//                   <input type="file" name="files" multiple className="form-control bg-light border-0" onChange={handleChange} accept="image/*" />
//                 </div>
//               </div>
//             </div>
//             <div className="modal-footer border-0 p-4 pt-0">
//               <button type="button" className="btn btn-light px-4 rounded-pill fw-bold" onClick={onClose}>Hủy</button>
//               <button type="submit" className="btn btn-primary px-5 rounded-pill fw-bold shadow">Tạo sự kiện</button>
//             </div>
//           </form>
//         </div>
//       </div>
//     </div>
//   );
// }

// Card sự kiện──
function EventCard({ event, onDetail, onChangeStatus, changingId }) {
  let totalSold = 0, totalQty = 0;
  event.ticketTypes?.forEach(tt => { totalSold += (tt.totalQuantity - tt.remainingQuantity); totalQty += tt.totalQuantity; });
  const percent = totalQty > 0 ? Math.round((totalSold/totalQty)*100) : 0;

  return (
    <div className="card h-100 border-0 shadow-sm transition-all hover-translate" style={{ borderRadius: '16px', overflow: 'hidden' }}>
      <div className="position-relative" style={{ height: '160px' }}>
        {event.imageUrls?.[0] ? (
          <img src={getImageUrl(event.imageUrls[0])} alt={event.name} className="w-100 h-100" style={{ objectFit: 'cover' }} />
        ) : (
          <div className="w-100 h-100 bg-secondary-subtle d-flex align-items-center justify-content-center text-secondary">No Image</div>
        )}
        <div className="position-absolute top-0 end-0 p-2">
          <StatusBadge status={event.status} />
        </div>
        <div className="position-absolute bottom-0 start-0 w-100 p-2 bg-gradient-dark">
           <span className="badge bg-white text-dark small">{event.categoryName}</span>
        </div>
      </div>

      <div className="card-body p-3 d-flex flex-column">
        <h6 className="fw-bold text-dark text-truncate mb-0" title={event.name}>{event.name}</h6>
        
        <div className="mt-2 mb-2 p-2 bg-light rounded-3" style={{ fontSize: '10px' }}>
          <div className="d-flex justify-content-between mb-1">
            <span className="text-secondary">Bán vé:</span>
            <span className="fw-medium">{formatDate(event.saleStartDate)} - {formatDate(event.saleEndDate)}</span>
          </div>
          <div className="d-flex justify-content-between">
            <span className="text-secondary">Diễn ra:</span>
            <span className="fw-medium text-primary">{formatDate(event.startTime)} - {formatDate(event.endTime)}</span>
          </div>
        </div>
        
        <p className="text-muted small mb-3 text-truncate" style={{ fontSize: '11px' }}>{event.organizerName}</p>
        
        <div className="mt-auto">
          <div className="d-flex justify-content-between small mb-1">
            <span className="text-muted">Tỷ lệ bán</span>
            <span className="fw-bold">{percent}%</span>
          </div>
          <div className="progress mb-3" style={{ height: '4px' }}>
            <div className="progress-bar bg-success" style={{ width: `${percent}%` }}></div>
          </div>

          <div className="d-flex gap-2">
            <button className="btn btn-sm btn-outline-primary flex-grow-1 border-2 fw-bold" style={{ fontSize: '11px', borderRadius: '8px' }} onClick={() => onDetail(event)}>
              CHI TIẾT
            </button>
            {(event.status === 'PENDING') && (
              <div className="d-flex gap-1 flex-grow-1">
                <button 
                  className="btn btn-sm btn-success border-0 shadow-sm flex-grow-1"
                  style={{ fontSize: '10px', borderRadius: '8px' }}
                  disabled={changingId === event.id}
                  onClick={() => onChangeStatus(event, 'UPCOMING')}
                >
                  {changingId === event.id ? '...' : 'DUYỆT'}
                </button>
                <button 
                  className="btn btn-sm btn-danger border-0 shadow-sm flex-grow-1"
                  style={{ fontSize: '10px', borderRadius: '8px' }}
                  disabled={changingId === event.id}
                  onClick={() => onChangeStatus(event, 'CANCELLED')}
                >
                  TỪ CHỐI
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

// Trang chính───
const STATUSES = [
  { value: "",          label: "Tất cả trạng thái" },
  { value: "PENDING",   label: "Chờ duyệt"         },
  { value: "UPCOMING",  label: "Sắp mở bán"       },
  { value: "OPENING",   label: "Đang bán vé"       },
  { value: "CLOSED",    label: "Hết thời gian bán vé"        },
  { value: "COMPLETED", label: "Đã kết thúc sự kiện"       },
  { value: "CANCELLED", label: "Đã hủy/Từ chối"    },
];

export default function EventsPage({ api }) {
  const [events, setEvents]           = useState([]);
  const [allEventsForStats, setAllEventsForStats] = useState([]); // Thêm để đếm tổng số
  const [page, setPage]               = useState(1);
  const [totalPages, setTotalPages]   = useState(1);
  const [loading, setLoading]         = useState(true);
  const [searchInput, setSearchInput] = useState("");
  const [search, setSearch]           = useState("");
  const [status, setStatus]           = useState("");
  const [refetch, setRefetch]         = useState(0);
  const [detail, setDetail]           = useState(null);
  const [showCreate, setShowCreate]   = useState(false);
  const [categories, setCategories]   = useState([]);
  const [changingId, setChangingId]   = useState(null);

  useEffect(() => {
    api.get("/categories").then(res => setCategories(res.result || []));
  }, [api]);

  // Fetch all for stats
  useEffect(() => {
    api.get("/events/admin/all?size=1000").then(res => {
      setAllEventsForStats(res.result?.content ?? []);
    });
  }, [refetch, api]);

  useEffect(() => {
    setLoading(true);
    const params = new URLSearchParams({ page, size: 12 });
    if (search) params.set("search", search);
    if (status) params.set("status", status);

    api.get(`/events/admin/all?${params}`).then((res) => {
      setEvents(res.result?.content ?? []);
      setTotalPages(res.result?.totalPages ?? 1);
      setLoading(false);
    });
  }, [page, search, status, refetch, api]);

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(1);
    setSearch(searchInput);
  };

  const handleChangeStatus = async (event, newStatus) => {
    if (!window.confirm(`Xác nhận thay đổi trạng thái sự kiện "${event.name}"?`)) return;
    setChangingId(event.id);
    try {
      await api.patch(`/events/${event.id}/status?status=${newStatus}`);
      setRefetch(n => n + 1);
    } catch (e) {
      alert("Lỗi khi thay đổi trạng thái!" + e);
    } finally {
      setChangingId(null);
    }
  };

  const handleSaveEvent = async (formData) => {
    try {
      const data = new FormData();
      Object.keys(formData).forEach(key => {
        if (key === 'files' && formData.files) {
          for (let i = 0; i < formData.files.length; i++) {
            data.append("files", formData.files[i]);
          }
        } else if (formData[key]) {
          data.append(key, formData[key]);
        }
      });

      await api.post("/events", data);
      setShowCreate(false);
      setRefetch(n => n + 1);
      alert("Tạo sự kiện thành công!");
    } catch (e) {
      alert("Lỗi khi tạo sự kiện. Vui lòng kiểm tra lại."+ e);
    }
  };

  return (
    <div className="animate-fade-in">
      {detail && <EventDetailModal event={detail} onClose={() => setDetail(null)} />}
      {showCreate && <CreateEventModal categories={categories} onClose={() => setShowCreate(false)} onSave={handleSaveEvent} />}

      <div className="mb-4 d-flex justify-content-between align-items-center">
        <div>
          <h4 className="fw-bold mb-1 font-inter">Quản lý sự kiện</h4>
          <p className="text-secondary small">Theo dõi, duyệt và quản lý các sự kiện.</p>
        </div>
        {/* <button className="btn btn-primary px-4 rounded-pill fw-bold shadow" onClick={() => setShowCreate(true)}>
           + Tạo sự kiện
        </button> */}
      </div>

      {/* Quick Stats Row */}
      <div className="row g-3 mb-4">
        <div className="col-md-3"><QuickStat label="Đang chờ duyệt" value={allEventsForStats.filter(e => e.status === 'PENDING').length} color="#fdcb6e" /></div>
        <div className="col-md-3"><QuickStat label="Đang mở bán" value={allEventsForStats.filter(e => e.status === 'OPENING').length} color="#00b894" /></div>
        <div className="col-md-3"><QuickStat label="Sắp diễn ra" value={allEventsForStats.filter(e => e.status === 'CLOSED').length} color="#0984e3" /></div>
        <div className="col-md-3"><QuickStat label="Đã hoàn thành" value={allEventsForStats.filter(e => e.status === 'COMPLETED').length} color="#6c5ce7" /></div>
      </div>

      <div className="card border-0 shadow-sm" style={{ borderRadius: '16px' }}>
        <div className="card-header bg-white p-4 border-0">
          <div className="row g-3 align-items-center">
            <div className="col-md-4">
              <div className="input-group input-group-sm border rounded-pill px-3 py-1" style={{ backgroundColor: '#f8f9fa' }}>
                <input
                  type="text"
                  className="form-control bg-transparent border-0 shadow-none"
                  placeholder="Tìm tên sự kiện..."
                  value={searchInput}
                  onChange={(e) => setSearchInput(e.target.value)}
                  onKeyDown={e => e.key === 'Enter' && handleSearch(e)}
                />
              </div>
            </div>
            <div className="col-md-4">
              <select
                className="form-select form-select-sm border-0 bg-light rounded-pill px-3 shadow-none"
                value={status}
                onChange={(e) => { setStatus(e.target.value); setPage(1); }}
              >
                {STATUSES.map((s) => (
                  <option key={s.value} value={s.value}>{s.label}</option>
                ))}
              </select>
            </div>
            <div className="col-md-4 text-md-end">
              <button className="btn btn-sm btn-primary rounded-pill px-4 fw-bold shadow-sm" onClick={() => setRefetch(n => n +1)}>LÀM MỚI ↻</button>
            </div>
          </div>
        </div>

        <div className="card-body p-4 pt-0">
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status"></div>
              <p className="mt-3 text-muted small fw-medium">Đang đồng bộ dữ liệu...</p>
            </div>
          ) : events.length === 0 ? (
            <div className="text-center py-5">
               <span style={{ fontSize: '48px' }}>....</span>
               <p className="mt-3 text-muted">Không tìm thấy sự kiện nào khớp với bộ lọc.</p>
            </div>
          ) : (
            <div className="row g-4 transition-all">
              {events.map((ev) => (
                <div key={ev.id} className="col-12 col-md-6 col-lg-4 col-xl-3">
                  <EventCard
                    event={ev}
                    onDetail={setDetail}
                    onChangeStatus={handleChangeStatus}
                    changingId={changingId}
                  />
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="p-4 border-top">
          <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>

      <style>{`
        .bg-gradient-dark {
          background: linear-gradient(to top, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0) 100%);
        }
        .hover-translate:hover {
          transform: translateY(-5px);
        }
        .last-child-mb-0:last-child {
          margin-bottom: 0 !important;
        }
        .font-inter { font-family: 'Inter', sans-serif; }
      `}</style>
    </div>
  );
}