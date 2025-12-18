import React, { createContext, useContext, useState, useEffect, useMemo, useCallback } from 'react';
import PropTypes from 'prop-types';
import { authService } from '@/services/authService';
import { userService } from '@/services/userService';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const mapUserData = useCallback((apiData) => {
    return {
      id: apiData.id,
      email: apiData.email,
      fullName: apiData.fullName || apiData.full_name, 
      role: apiData.role || apiData.user_role,        
    };
  }, []);


  const refreshUser = useCallback(async () => {
    try {
      const response = await userService.getCurrentUser();
      const updatedData = mapUserData(response.data);
      
      setUser(updatedData);
      localStorage.setItem('user', JSON.stringify(updatedData));
      return updatedData;
    } catch (error) {
      console.error('Erro ao atualizar dados do utilizador:', error);
      return null;
    }
  }, [mapUserData]);

  useEffect(() => {
    const initAuth = async () => {
      const token = authService.getToken();
      if (token) {
        await refreshUser();
      }
      setLoading(false);
    };
    initAuth();
  }, [refreshUser]);

  const login = async (email, password) => {
    const authResponse = await authService.login(email, password);
    authService.setToken(authResponse.token);

    return await refreshUser();
  };

  const register = async (data) => {
    await authService.register(data);
    return await login(data.email, data.password);
  };

  const logout = async () => {
    try {
      await authService.logout();
    } finally {
      setUser(null);
      localStorage.removeItem('user');
      authService.removeToken();
    }
  };

  const value = useMemo(() => ({
    user,
    login,
    register,
    logout,
    refreshUser,
    isAuthenticated: !!user && authService.isAuthenticated(),
    loading,
  }), [user, loading, refreshUser]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

AuthProvider.propTypes = {
  children: PropTypes.node.isRequired,
};

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  return context;
}