import { authService } from './authService';
import { API_BASE_URL } from '../config/api';

/**
 * Helper para configurar los headers con el token JWT
 */
const getAuthHeaders = () => {
  const token = authService.getToken();
  if (!token) throw new Error('Sessão expirada. Por favor, faça login novamente.');
  
  return {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json',
  };
};

export const userService = {
  
  // 1. Obtener perfil del usuario actual (GET /api/users/me)
  async getCurrentUser() {
    const response = await fetch(`${API_BASE_URL}/api/users/me`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });

    if (!response.ok) throw new Error('Erro ao obter dados do utilizador');
    
    const data = await response.json();
    return { data };
  },

  // 2. Actualizar perfil del usuario actual (PUT /api/users/me)
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

  // 3. Solicitar upgrade a OWNER (POST /api/users/upgrade)
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

    // El backend devuelve ResponseEntity.ok().build() (vacío)
    return { success: true };
  },
  // src/services/userService.js

// ... (mantén lo anterior)

  // Añade esta función para arreglar el error de Login
  async getByEmail(email) {
    // Opción A: Si tu backend tiene /api/users/email/{email}
    // Opción B: Si el login solo necesita los datos del usuario actual, usamos /me
    const response = await fetch(`${API_BASE_URL}/api/users/me`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });

    if (!response.ok) throw new Error('Utilizador não encontrado no servidor');
    
    const data = await response.json();
    return { data }; // Devolvemos el objeto con la propiedad data para mantener compatibilidad
  },

  // 4. Listar todos los usuarios (Solo si el Admin tiene este endpoint en el backend)
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