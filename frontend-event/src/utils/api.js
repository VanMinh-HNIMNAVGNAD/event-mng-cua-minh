const BASE = "https://be-event-mng-v2.onrender.com/event-mng";

export async function apiFetch(path, options = {}) {
  const token = localStorage.getItem("token");
  const authHeader = (token && token !== "null" && token !== "undefined") ? `Bearer ${token}` : "";
  
  const res = await fetch(`${BASE}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      Authorization: authHeader,
      ...options.headers,
    },
  });
  
  if (res.status === 401) {
    // Optionally handle logout or token refresh
  }

  return res.json();
}

export const API_BASE = BASE;
