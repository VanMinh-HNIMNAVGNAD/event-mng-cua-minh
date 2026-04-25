import React from "react";

export default function OrganizerSidebar({ activeTab, setActiveTab }) {
  const menuItems = [
    { id: "dashboard", icon: "📊", label: "Thống kê" },
    { id: "events", icon: "🎫", label: "Sự kiện của tôi" },
    { id: "create", icon: "📅", label: "Đăng sự kiện" },
    { id: "profile", icon: "👤", label: "Thông tin cá nhân" },
  ];

  return (
    <div className="bg-white shadow-sm border-end flex-shrink-0" style={{ width: "260px", minHeight: "100vh", position: "sticky", top: 0 }}>
      <div className="p-4 border-bottom text-center">
        <h5 className="fw-bold text-primary mb-0">Organizer Hub</h5>
        <small className="text-muted text-uppercase small" style={{ letterSpacing: '1px' }}>Ban tổ chức</small>
      </div>
      <div className="list-group list-group-flush pt-2">
        {menuItems.map((item) => (
          <button
            key={item.id}
            className={`list-group-item list-group-item-action border-0 py-3 px-4 d-flex align-items-center gap-3 ${
              activeTab === item.id || (activeTab === 'tickets' && item.id === 'events') ? "active bg-primary bg-opacity-10 text-primary fw-bold" : "text-secondary"
            }`}
            onClick={() => setActiveTab(item.id)}
            style={{ transition: 'all 0.2s' }}
          >
            <span className="fs-5">{item.icon}</span>
            <span>{item.label}</span>
          </button>
        ))}
        
        <div className="mt-auto px-4 py-4">
             <button 
                className="btn btn-outline-danger w-100 rounded-pill d-flex align-items-center justify-content-center gap-2" 
                onClick={() => window.location.href = "/login"}
             >
                <span>Đăng xuất</span>
             </button>
        </div>
      </div>
    </div>
  );
}
