import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/axios';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('token') || null);
  const [loading, setLoading] = useState(false);

  const login = async (usernameOrEmail, password) => {
    setLoading(true);
    try {
      const response = await api.post('/auth/login', { usernameOrEmail, password });
      const data = response.data;
      setToken(data.accessToken);
      const userData = {
        id: data.id,
        username: data.username,
        email: data.email,
        roles: data.roles
      };
      setUser(userData);
      localStorage.setItem('token', data.accessToken);
      localStorage.setItem('user', JSON.stringify(userData));
      return { success: true };
    } catch (err) {
      return { success: false, message: err.message || 'Login failed' };
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const hasRole = (roleName) => {
    return user?.roles?.includes(roleName);
  };

  return (
    <AuthContext.Provider value={{
      user,
      token,
      login,
      logout,
      loading,
      isAdmin: hasRole('ROLE_ADMIN'),
      isOfficer: hasRole('ROLE_COMPLIANCE_OFFICER'),
      isMemberUser: hasRole('ROLE_MEMBER_USER')
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
