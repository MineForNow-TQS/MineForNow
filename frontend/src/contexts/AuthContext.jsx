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

  const register = async ({ fullName, email, password, confirmPassword }) => {
    const response = await fetch("http://localhost:8080/api/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        fullName,       // ahora coincide exactamente con el DTO del backend
        email,
        password,
        confirmPassword
      })
    });

    if (!response.ok) {
      const errMsg = await response.text();
      throw new Error(errMsg || "Erro desconhecido no registro");
    }

    const data = await response.json();

    const newUser = {
      id: data.userId,
      fullName,
      email: data.email,
      role: data.role
    };

    setUser(newUser);
    localStorage.setItem("user", JSON.stringify(newUser));

    return newUser;
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
