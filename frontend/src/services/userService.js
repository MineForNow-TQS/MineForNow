import { authService } from './authService';
import { API_BASE_URL } from '../config/api';

const getAuthHeaders = () => {
  const token = authService.getToken();
  if (!token) throw new Error('Sessão expirada. Por favor, faça login novamente.');
  
  return {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json',
  };
};

export const userService = {
  

  async getCurrentUser() {
    const response = await fetch(`${API_BASE_URL}/api/users/me`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });

    if (!response.ok) throw new Error('Erro ao obter dados do utilizador');
    
    const data = await response.json();
    return { data };
  },


  async updateCurrentUserProfile(profileData) {
    const response = await fetch(`${API_BASE_URL}/api/users/me`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(profileData),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || 'Erro ao atualizar perfil');
    }

    const data = await response.json();
    return { data };
  },


  async requestOwnerUpgrade(upgradeData) {
    const response = await fetch(`${API_BASE_URL}/api/users/upgrade`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({
        phone: upgradeData.phone,
        citizenCardNumber: upgradeData.citizenCardNumber,
        drivingLicense: upgradeData.drivingLicense,
        motivation: upgradeData.motivation
      }),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || 'Erro ao submeter pedido de upgrade');
    }

    return { success: true };
  },
 
  async getByEmail(email) {
    const response = await fetch(`${API_BASE_URL}/api/users/me`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });

    if (!response.ok) throw new Error('Utilizador não encontrado no servidor');
    
    const data = await response.json();
    return { data }; 
  },
  async list() {
    const response = await fetch(`${API_BASE_URL}/api/users`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });

    if (!response.ok) throw new Error('Erro ao listar utilizadores');
    const data = await response.json();
    return { data };
  }
};