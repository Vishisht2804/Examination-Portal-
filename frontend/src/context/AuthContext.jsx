import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { loginApi, meApi } from '../api/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem('user');
    return raw ? JSON.parse(raw) : null;
  });

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token || user) return;
    meApi().then(({ data }) => {
      setUser(data);
      localStorage.setItem('user', JSON.stringify(data));
    }).catch(() => {
      logout();
    });
  }, []);

  const login = async (username, password) => {
    const { data } = await loginApi({ username, password });
    localStorage.setItem('token', data.token);
    const profile = {
      id: data.userId,
      role: data.role,
      fullName: data.fullName,
      username
    };
    setUser(profile);
    localStorage.setItem('user', JSON.stringify(profile));
    return profile;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  const value = useMemo(() => ({ user, login, logout, isAuthenticated: Boolean(user) }), [user]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
