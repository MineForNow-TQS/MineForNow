import { authService } from './authService';
import { userService } from './userService';

import { API_BASE_URL } from '../config/api';

export const ownerRequestService = {
  async list() {
    try {
      const { data: user } = await userService.getCurrentUser();
      
      if (user.role === 'PENDING_OWNER') {
        return {
          data: [{
            id: user.id,
            user_email: user.email,
            status: 'pending',
            created_at: new Date().toISOString()
          }]
        };
      }
      return { data: [] }; 
    } catch (error) {
      return { data: [] };
    }
  },

  async create(formData) {
    const token = authService.getToken();
    const response = await fetch(`${API_BASE_URL}/api/users/upgrade`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        phone: formData.phone,
        citizenCardNumber: formData.citizenCardNumber,
        drivingLicense: formData.drivingLicense,
        motivation: formData.motivation
      }),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || 'Erro ao processar upgrade');
    }
    return response;
  }
};