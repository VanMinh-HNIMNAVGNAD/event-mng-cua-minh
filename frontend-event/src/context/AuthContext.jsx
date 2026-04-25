import { createContext, useContext, useState } from "react";
import { getToken, saveToken, removeToken } from "../utils/token";

// Giải mã JWT để lấy thông tin user 
function parseJwt(token) {
  try {
    return JSON.parse(atob(token.split(".")[1]));
  } catch {
    return null;
  }
}

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  // Khi F5 lại trang, đọc token từ localStorage để không bị mất login
  const [user, setUser] = useState(() => {
    const token = getToken();
    return token ? parseJwt(token) : null;
  });

  const login = (token) => {
    saveToken(token);
    setUser(parseJwt(token));
  };

  const logout = () => {
    removeToken();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// Custom hook dùng trong các component
export function useAuth() {
  return useContext(AuthContext);
}