import { useState, useEffect } from "react";
import { StatusBadge } from "../../utils/helpers";
import StatCards from "./StatCards";

export default function DashboardPage({ api }) {
  const [events, setEvents] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState([]);

  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true);
      try {
        const [evRes, usRes] = await Promise.all([
          api.get("/events/admin/all?size=1000"),
          api.get("/users?page=1&size=1000"),
        ]);

        const evList = evRes.result?.content ?? [];
        const usList = usRes.result?.content ?? [];
        
        setEvents(evList);
        setUsers(usList);

        // Process professional stats
        let totalRev = 0;
        let totalSold = 0;
        evList.forEach(ev => {
          ev.ticketTypes?.forEach(tt => {
            const sold = (tt.totalQuantity || 0) - (tt.remainingQuantity || 0);
            totalSold += sold;
            totalRev += sold * (tt.price || 0);
          });
        });

        setStats([
          { title: "LỢI NHUẬN NỀN TẢNG", value: (totalRev * 0.25).toLocaleString() + "đ" },
          { title: "VÉ ĐÃ BÁN", value: totalSold.toLocaleString() },
          { title: "NGƯỜI DÙNG", value: usList.length.toString() },
        ]);
      } catch (err) {
        console.error("Dashboard data error:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  return (
    <div className="animate-fade-in">
      <div className="mb-4">
        <h4 className="fw-bold mb-1">Bảng điều khiển</h4>
        <p className="text-secondary small">welcome</p>
      </div>

      <StatCards stats={stats} loading={loading} />

      <div className="card border-0 shadow-sm mt-4 overflow-hidden" style={{ borderRadius: '16px' }}>
        <div className="card-header bg-white p-4 border-0 d-flex justify-content-between align-items-center">
          <h6 className="fw-bold mb-0">Danh sách sự kiện</h6>
          <button className="btn btn-sm btn-light text-primary px-3 rounded-pill fw-bold" onClick={() => window.location.reload()}>Làm mới</button>
        </div>
        <div className="table-responsive">
          <table className="table table-hover align-middle mb-0">
            <thead className="bg-light text-secondary small text-uppercase">
              <tr>
                <th className="px-4 py-2 border-0">Sự kiện</th>
                <th className="border-0">Tổ chức</th>
                <th className="border-0">Tỷ lệ bán</th>
                <th className="border-0">Ngày tạo</th>
                <th className="border-0 text-center">Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan="5" className="text-center py-5 border-0"><div className="spinner-border text-primary spinner-border-sm me-2"></div>Đang tải...</td></tr>
              ) : events.slice(0, 8).map((ev) => {
                let sold = 0, total = 0;
                ev.ticketTypes?.forEach(tt => { sold += (tt.totalQuantity - tt.remainingQuantity); total += tt.totalQuantity; });
                const percent = total > 0 ? Math.round((sold/total)*100) : 0;
                
                return (
                  <tr key={ev.id}>
                    <td className="px-4 border-0">
                      <div className="fw-bold text-dark">{ev.name}</div>
                      <div className="text-muted small">{ev.categoryName}</div>
                    </td>
                    <td className="border-0 text-secondary">{ev.organizerName}</td>
                    <td className="border-0" style={{ minWidth: '120px' }}>
                      <div className="d-flex align-items-center gap-2">
                        <div className="progress flex-grow-1" style={{ height: '4px' }}>
                          <div className="progress-bar bg-primary" style={{ width: `${percent}%` }}></div>
                        </div>
                        <span className="small fw-bold">{percent}%</span>
                      </div>
                    </td>
                    <td className="border-0 text-muted small">{new Date(ev.createdAt).toLocaleDateString()}</td>
                    <td className="text-center border-0">
                      <StatusBadge status={ev.status} />
                    </td>
                  </tr>
                );
              })}
              {!loading && events.length === 0 && (
                <tr><td colSpan="5" className="text-center py-5 text-muted border-0">Chưa có sự kiện nào được tạo.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
