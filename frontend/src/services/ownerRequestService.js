import { authService } from './authService';

import { API_BASE_URL } from '../config/api';

export const ownerRequestService = {

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