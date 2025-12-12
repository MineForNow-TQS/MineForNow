import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '@/services/authService';
import { userService } from '@/services/userService';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user exists in localStorage
    const storedUser = localStorage.getItem('user');
    const token = authService.getToken();

    if (storedUser && token) {
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    // Call the backend login API
    const authResponse = await authService.login(email, password);

    // Store the JWT token
    authService.setToken(authResponse.token);

    // Get user details from backend
    try {
      const userResponse = await userService.getByEmail(email);
      const userData = {
        id: userResponse.data.id,
        email: userResponse.data.email,
        full_name: userResponse.data.full_name || userResponse.data.name,
        user_role: userResponse.data.role,
      };

      setUser(userData);
      localStorage.setItem('user', JSON.stringify(userData));
      return userData;
    } catch (error) {
      // If user details fetch fails, use basic info from email
      const basicUser = {
        email: email,
        full_name: email.split('@')[0],
        user_role: 'RENTER',
      };
      setUser(basicUser);
      localStorage.setItem('user', JSON.stringify(basicUser));
      return basicUser;
    }
  };

  const register = async (userData) => {
    // Create new user in the system
    const newUserData = {
      name: userData.full_name,
      email: userData.email,
      password: userData.password,
      full_name: userData.full_name,
      role: 'RENTER',
    };

    const createdUser = await userService.create(newUserData);

    // After registration, login the user
    const authResponse = await authService.login(userData.email, userData.password);
    authService.setToken(authResponse.token);

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
    authService.removeToken();
  };

  const isAuthenticated = () => {
    return !!user && authService.isAuthenticated();
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

