import { useCallback } from "react";

const BASE = "https://be-event-mng-v2.onrender.com/event-mng";

export const getImageUrl = (url) => {
  if (!url) return "";
  if (url.startsWith("http")) return url;
  return `${BASE}${url.startsWith("/") ? "" : "/"}${url}`;
};

// export const getImageUrl = (url) => {
//     if (!url) return "";
//     if (url.startsWith("http")) {
//         // Thay localhost bằng IP thực của server
//         return url.replace(
//             "http://localhost:8080",
//             "http://192.168.0.110:8080",
//         );
//     }
//     return `${BASE}${url.startsWith("/") ? "" : "/"}${url}`;
// };


export function useApi() {
  const rawToken = localStorage.getItem("token");
  const token = (rawToken && rawToken !== "null" && rawToken !== "undefined") ? rawToken : null;

  const get = useCallback(
    (path) =>
      fetch(`${BASE}${path}`, {
        headers: { ...(token ? { Authorization: `Bearer ${token}` } : {}) },
      }).then((r) => r.json()),
    [token]
  );

  const post = useCallback(
    (path, body) => {
      const isFormData = body instanceof FormData;
      return fetch(`${BASE}${path}`, {
        method: "POST",
        headers: {
          ...(isFormData ? {} : { "Content-Type": "application/json" }),
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: isFormData ? body : JSON.stringify(body),
      }).then((r) => r.json());
    },
    [token]
  );

  const put = useCallback(
    (path, body) => {
      const isFormData = body instanceof FormData;
      return fetch(`${BASE}${path}`, {
        method: "PUT",
        headers: {
          ...(isFormData ? {} : { "Content-Type": "application/json" }),
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: isFormData ? body : JSON.stringify(body),
      }).then((r) => r.json());
    },
    [token]
  );

  const patch = useCallback(
    (path) =>
      fetch(`${BASE}${path}`, {
        method: "PATCH",
        headers: { ...(token ? { Authorization: `Bearer ${token}` } : {}) },
      }).then((r) => r.json()),
    [token]
  );

  const del = useCallback(
    (path) =>
      fetch(`${BASE}${path}`, {
        method: "DELETE",
        headers: { ...(token ? { Authorization: `Bearer ${token}` } : {}) },
      }).then((r) => r.json()),
    [token]
  );

  return { get, post, put, patch, del };
}