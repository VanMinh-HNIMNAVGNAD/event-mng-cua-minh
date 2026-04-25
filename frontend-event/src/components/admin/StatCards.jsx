import React from 'react';

const StatCard = ({ title, value, color, trend, loading }) => (
  <div className="card border-0 shadow-sm transition-transform hover-scale h-100" style={{ borderRadius: '15px', overflow: 'hidden' }}>
    <div className="card-body p-4">
      <div className="d-flex align-items-center justify-content-between mb-3">
        {trend !== undefined && !loading && (
          <div className={`small fw-bold ${trend >= 0 ? 'text-success' : 'text-danger'}`}>
            {trend >= 0 ? '↑' : '↓'} {Math.abs(trend)}%
          </div>
        )}
      </div>
      <h6 className="text-secondary mb-1 fw-medium" style={{ fontSize: '0.85rem', letterSpacing: '0.5px' }}>{title}</h6>
      <h3 className="mb-0 fw-bold" style={{ color: '#2d3436' }}>
        {loading ? <span className="placeholder col-6"></span> : value}
      </h3>
    </div>
  </div>
);

export default function StatCards({ stats, loading }) {
  const defaultStats = [
    { title: "TỔNG DOANH THU", value: "0đ"},
    { title: "VÉ ĐÃ BÁN", value: "0"},
    { title: "NGƯỜI DÙNG", value: "0"},
    { title: "SỰ KIỆN", value: "0"},
  ];

  const data = stats || defaultStats;

  return (
    <div className="row g-4">
      {data.map((stat, idx) => (
        <div key={idx} className="col-12 col-md-6 col-xl-3">
          <StatCard {...stat} loading={loading} />
        </div>
      ))}
    </div>
  );
}
