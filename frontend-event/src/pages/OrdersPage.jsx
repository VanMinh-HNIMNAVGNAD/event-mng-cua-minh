import { useState, useEffect } from "react";
import { useApi } from "../hooks/useApi";
import Navbar from "../components/common/Navbar";
import { VND, formatDate } from "../utils/helpers";

export default function OrdersPage() {
  const api = useApi();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      const res = await api.get("/orders?page=1&size=50");
      setOrders(res.result?.content || []);
    } catch (err) {} finally { setLoading(false); }
  };

  return (
    <div className="bg-light" style={{ minHeight: "100vh" }}>
      <Navbar />
      <div className="container py-5">
        <h2 className="fw-bold mb-4">Lịch sử đặt vé</h2>
        
        {loading ? (
          <div className="text-center py-5"><div className="spinner-border text-primary"></div></div>
        ) : orders.length === 0 ? (
          <div className="text-center py-5 bg-white rounded shadow-sm">
            <p className="text-muted">Bạn chưa có đơn hàng nào.</p>
          </div>
        ) : (
          <div className="row g-4">
            {orders.map(order => (
              <div key={order.id} className="col-12">
                <div className="card border-0 shadow-sm overflow-hidden" style={{ borderRadius: '15px' }}>
                  <div className="card-header bg-white p-3 d-flex justify-content-between align-items-center">
                    <span className="fw-bold text-primary">Đơn hàng #{order.id}</span>
                    <span className="badge bg-success">{order.orderStatus}</span>
                  </div>
                  <div className="card-body">
                    {order.items?.map((item, idx) => (
                      <div key={idx} className="d-flex justify-content-between mb-2">
                        <span>{item.eventName} - {item.ticketTypeName} x{item.quantity}</span>
                        <span className="fw-bold">{VND(item.subtotal)}</span>
                      </div>
                    ))}
                    <hr/>
                    <div className="d-flex justify-content-between">
                      <span className="text-muted">Ngày đặt: {formatDate(order.orderDate)}</span>
                      <h5 className="fw-bold">Tổng: {VND(order.totalAmount)}</h5>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
