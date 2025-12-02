import React, { createContext, useContext, useState, useEffect } from 'react';
import { userService } from '@/services/userService';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Verificar se existe usuário no localStorage
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    // Verificar se o usuário já existe no sistema
    try {
      const existingUser = await userService.getByEmail(email);
      const mockUser = {
        id: existingUser.data.id,
        email: existingUser.data.email,
        full_name: existingUser.data.full_name || existingUser.data.name,
        user_role: existingUser.data.role,
      };
      
      setUser(mockUser);
      localStorage.setItem('user', JSON.stringify(mockUser));
      return mockUser;
    } catch (error) {
      // Se não existir, criar novo usuário
      const newUserData = {
        name: email.split('@')[0],
        email: email,
        full_name: email.split('@')[0],
        role: 'rental', // Novos usuários sempre começam como rental
      };
      
      const createdUser = await userService.create(newUserData);
      const mockUser = {
        id: createdUser.data.id,
        email: createdUser.data.email,
        full_name: createdUser.data.full_name,
        user_role: createdUser.data.role,
      };
      
      setUser(mockUser);
      localStorage.setItem('user', JSON.stringify(mockUser));
      return mockUser;
    }
  };

  const register = async (userData) => {
    // Criar novo usuário no sistema
    const newUserData = {
      name: userData.full_name,
      email: userData.email,
      full_name: userData.full_name,
      role: 'rental',
    };
    
    const createdUser = await userService.create(newUserData);
    const mockUser = {
      id: createdUser.data.id,
      email: createdUser.data.email,
      full_name: createdUser.data.full_name,
      user_role: createdUser.data.role,
    };
    
    setUser(mockUser);
    localStorage.setItem('user', JSON.stringify(mockUser));
    return mockUser;
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('user');
  };

  const isAuthenticated = () => {
    return !!user;
  };

  const value = {
    user,
    login,
    register,
    logout,
    isAuthenticated,
    loading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
}
