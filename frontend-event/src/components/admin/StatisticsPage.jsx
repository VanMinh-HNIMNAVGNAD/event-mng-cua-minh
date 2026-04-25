import { useEffect, useRef, useState } from 'react';
import Chart from 'chart.js/auto';
import StatCards from './StatCards';

export default function StatisticsPage({ api }) {
  const salesChartRef = useRef(null);
  const categoryChartRef = useRef(null);
  const [loading, setLoading] = useState(true);
  const [statsData, setStatsData] = useState([]);
  const [topEvents, setTopEvents] = useState([]);

  useEffect(() => {
    let salesChart = null;
    let categoryChart = null;

    const fetchData = async () => {
      setLoading(true);
      try {
        const now = new Date();
        const monthLabels = [];
        const monthKeys = []; // Định dạng YYYY-MM
        const monthlyProfit = []; // Lợi nhuận Admin (10%)
        
        for (let i = -5; i <= 0; i++) {
          const d = new Date(now.getFullYear(), now.getMonth() + i, 1);
          const m = String(d.getMonth() + 1).padStart(2, '0');
          const y = d.getFullYear();
          monthLabels.push(`Th.${d.getMonth() + 1}/${y.toString().slice(-2)}`);
          monthKeys.push(`${y}-${m}`);
          monthlyProfit.push(0);
        }

        // Fetch data
        const currentQuarter = Math.floor(now.getMonth() / 3) + 1;
        const [eventsRes, statusRes] = await Promise.all([
          api.get("/events/admin/all?size=1000"),
          api.get(`/statistics-event/by-status/${currentQuarter}/${now.getFullYear()}`)
        ]);

        const events = eventsRes.result?.content || [];
        const statusDetails = statusRes.result?.eventStatusStatsDetail || [];

        // Process event stats
        let totalCommission = 0;
        let totalSold = 0;
        
        const processedEvents = events.map(ev => {
          let eventSold = 0;
          let eventRev = 0;
          let totalQty = 0;
          
          ev.ticketTypes?.forEach(tt => {
            const soldCount = (Number(tt.totalQuantity) || 0) - (Number(tt.remainingQuantity) || 0);
            eventSold += soldCount;
            eventRev += soldCount * (Number(tt.price) || 0);
            totalQty += (Number(tt.totalQuantity) || 0);
          });

          const commission = eventRev * 0.25;
          totalCommission += commission;
          totalSold += eventSold;

          // Mapping ra biểu đồ
          const dateStr = ev.startTime || ev.createdAt;
          if (dateStr) {
            const d = new Date(dateStr);
            if (!isNaN(d.getTime())) {
              const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
              const mIndex = monthKeys.indexOf(key);
              if (mIndex !== -1) {
                monthlyProfit[mIndex] += (commission / 1000000); // Đưa vào triệu VNĐ
              }
            }
          }

          return { ...ev, ticketsSold: eventSold, totalRevenue: eventRev, totalTickets: totalQty };
        });

        // Update StatCards
        setStatsData([
          { title: "LỢI NHUẬN NỀN TẢNG", value: totalCommission.toLocaleString() + "đ" },
          { title: "VÉ ĐÃ BÁN", value: totalSold.toLocaleString() },
          { title: "SỰ KIỆN", value: events.length.toString() },
        ]);

        setTopEvents([...processedEvents].sort((a, b) => b.ticketsSold - a.ticketsSold).slice(0, 5));

        // Sales Chart
        if (salesChartRef.current) {
          salesChart = new Chart(salesChartRef.current, {
            type: 'line',
            data: {
              labels: monthLabels,
              datasets: [{
                label: 'Lợi nhuận',
                data: monthlyProfit,
                borderColor: '#0984e3',
                backgroundColor: 'rgba(9, 132, 227, 0.15)',
                fill: true,
                tension: 0.4,
                pointRadius: 6,
                pointHoverRadius: 8,
                pointBackgroundColor: '#fff',
                pointBorderColor: '#0984e3',
                pointBorderWidth: 3,
                borderWidth: 4
              }]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              plugins: { 
                legend: { display: false },
                tooltip: {
                  callbacks: {
                    label: (context) => `Lợi nhuận: ${context.parsed.y.toFixed(2)} Triệu VNĐ`
                  }
                }
              },
              scales: {
                y: { 
                  beginAtZero: true, 
                  grid: { color: '#f1f2f6' },
                  ticks: { 
                    callback: (val) => val + 'M',
                    font: { size: 10, weight: 'bold' }
                  }
                },
                x: { 
                  grid: { display: false },
                  ticks: { font: { size: 10 } }
                }
              }
            }
          });
        }

        const translateStatus = (status) => {
          const map = {
            'PENDING': 'CHỜ DUYỆT',
            'UPCOMING': 'SẮP MỞ BÁN',
            'OPENING': 'ĐANG MỞ BÁN',
            'CLOSED': 'SẮP DIỄN RA',
            'COMPLETED': 'KẾT THÚC',
            'CANCELLED': 'ĐÃ HỦY'
          };
          return map[status] || status;
        };

        const localStatusStats = events.reduce((acc, ev) => {
          acc[ev.status] = (acc[ev.status] || 0) + 1;
          return acc;
        }, {});

        const statusLabels = Object.keys(localStatusStats);
        const statusData = Object.values(localStatusStats);

        // bd tron
        if (categoryChartRef.current) {
          categoryChart = new Chart(categoryChartRef.current, {
            type: 'doughnut',
            data: {
              labels: statusLabels.length > 0 ? statusLabels.map(s => translateStatus(s)) : ['HỆ THỐNG'],
              datasets: [{
                data: statusData.length > 0 ? statusData : [1],
                backgroundColor: ['#00b894', '#6c5ce7', '#0984e3', '#fdcb6e', '#fab1a0'],
                borderWidth: 0,
                hoverOffset: 20
              }]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              cutout: '70%',
              plugins: {
                legend: { position: 'bottom', labels: { boxWidth: 12, padding: 15, font: { size: 11 } } }
              }
            }
          });
        }
      } catch (err) {
        console.error("Lỗi thống kê:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();

    return () => {
      if (salesChart) salesChart.destroy();
      if (categoryChart) categoryChart.destroy();
    };
  }, []);

  const translateStatus = (s) => {
    const map = {
      'PENDING': 'CHỜ DUYỆT',
      'UPCOMING': 'SẮP MỞ BÁN',
      'OPENING': 'ĐANG MỞ BÁN',
      'CLOSED': 'SẮP DIỄN RA',
      'COMPLETED': 'KẾT THÚC',
      'CANCELLED': 'ĐÃ HỦY'
    };
    return map[s] || s;
  };

  return (
    <div className="animate-fade-in px-2">
      <div className="mb-4">
        <h4 className="fw-bold mb-1">Phân tích doanh thu</h4>
        <p className="text-secondary small">Theo dõi xu hướng bán vé qua các tháng</p>
      </div>

      <StatCards stats={statsData} loading={loading} />

      <div className="row g-4 mt-2">
        <div className="col-12 col-lg-8">
          <div className="card p-4 h-100 shadow-sm border-0" style={{ minHeight: '410px', borderRadius: '20px' }}>
            <div className="d-flex justify-content-between align-items-center mb-4">
              <div>
                <h6 className="fw-bold mb-0">Biểu đồ doanh thu</h6>
                <small className="text-muted small">Đơn vị: Triệu VNĐ</small>
              </div>
              {/* <div className="d-flex gap-2">
                 <span className="badge bg-primary px-3 py-2 rounded-pill">DỮ LIỆU THỰC</span>
              </div> */}
            </div>
            <div className="flex-grow-1 position-relative" style={{ height: '280px' }}>
              <canvas ref={salesChartRef}></canvas>
            </div>
          </div>
        </div>
        <div className="col-12 col-lg-4">
          <div className="card p-4 h-100 shadow-sm border-0" style={{ minHeight: '410px', borderRadius: '20px' }}>
            <h6 className="fw-bold mb-4">Trạng thái sự kiện</h6>
            <div className="flex-grow-1 d-flex align-items-center justify-content-center position-relative" style={{ height: '280px' }}>
              <canvas ref={categoryChartRef}></canvas>
            </div>
          </div>
        </div>
      </div>

      <div className="card mt-4 shadow-sm border-0 overflow-hidden" style={{ borderRadius: '20px' }}>
        <div className="card-header bg-white p-4 border-0 d-flex justify-content-between align-items-center">
          <h6 className="fw-bold mb-0">Xếp hạng</h6>
          <button className="btn btn-sm btn-outline-primary border-0 fw-bold" onClick={() => window.location.reload()}>
            LÀM MỚI
          </button>
        </div>
        <div className="table-responsive">
          <table className="table table-hover align-middle mb-0">
            <thead className="bg-light text-secondary small text-uppercase" style={{ fontSize: '11px', letterSpacing: '0.5px' }}>
              <tr>
                <th className="px-4 py-3 border-0">TOP</th>
                <th className="border-0">SỰ KIỆN</th>
                <th className="border-0">VÉ BÁN</th>
                <th className="border-0 text-center">TỶ LỆ LẤY CHỖ</th>
                <th className="border-0">TỔNG TIỀN VÉ</th>
                <th className="border-0 text-primary">HOA HỒNG (25%)</th>
                <th className="border-0 text-center">TRẠNG THÁI</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan="6" className="text-center py-5 border-0"><div className="spinner-border text-primary spinner-border-sm me-2"></div>Đang phân tích dữ liệu...</td></tr>
              ) : topEvents.length === 0 ? (
                <tr><td colSpan="6" className="text-center py-5 text-muted border-0">Chưa có sự kiện nào phát sinh doanh thu.</td></tr>
              ) : topEvents.map((ev, idx) => {
                const percentage = ev.totalTickets > 0 ? Math.round((ev.ticketsSold / ev.totalTickets) * 100) : 0;
                return (
                  <tr key={ev.id}>
                    <td className="px-4 border-0">
                       <span className={`badge rounded-circle ${idx === 0 ? 'bg-warning' : 'bg-light text-dark'}`} style={{ width: '24px', height: '24px', padding: '5px' }}>
                         {idx + 1}
                       </span>
                    </td>
                    <td className="border-0">
                      <div className="fw-bold text-dark">{ev.name}</div>
                      <div className="text-muted" style={{ fontSize: '11px' }}>{ev.categoryName || "Khác"}</div>
                    </td>
                    <td className="fw-bold text-primary border-0">{ev.ticketsSold?.toLocaleString()}</td>
                    <td className="border-0">
                      <div className="d-flex align-items-center justify-content-center gap-2">
                        <div className="progress flex-grow-1" style={{ height: '4px', maxWidth: '70px' }}>
                          <div className="progress-bar bg-primary" style={{ width: `${percentage}%` }}></div>
                        </div>
                        <small className="fw-bold" style={{ fontSize: '10px' }}>{percentage}%</small>
                      </div>
                    </td>
                    <td className="fw-bold border-0 text-dark">{ev.totalRevenue?.toLocaleString()}đ</td>
                    <td className="fw-bold border-0 text-primary">{(ev.totalRevenue * 0.25)?.toLocaleString()}đ</td>
                    <td className="text-center border-0">
                      <span className={`badge ${ev.status === 'PUBLISHED' ? 'bg-success-subtle text-success' : 'bg-light text-muted'} px-3 rounded-pill`} style={{ fontSize: '10px' }}>
                        {translateStatus(ev.status)}
                      </span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      <style>{`
        .animate-fade-in { animation: fadeIn 0.5s ease-out; }
        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(10px); }
          to { opacity: 1; transform: translateY(0); }
        }
      `}</style>
    </div>
  );
}
