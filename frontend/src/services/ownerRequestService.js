import { authService } from './authService';
import { userService } from './userService';

import { API_BASE_URL } from '../config/api';

export const ownerRequestService = {
  async list() {
    try {
      // Fetch all users using the admin endpoint
      const { data: users } = await userService.list();

      // Filter users with PENDING_OWNER or OWNER role
      const relevantUsers = users.filter(user =>
        user.role === 'PENDING_OWNER' || user.role === 'OWNER'
      );

      // Map to the format expected by the dashboard
      const requests = relevantUsers.map(user => ({
        id: user.id,
        user_name: user.fullName,
        user_email: user.email,
        phone: user.phone,
        citizenCardNumber: user.citizenCardNumber,
        drivingLicense: user.drivingLicense,
        motivation: user.ownerMotivation,
        status: user.role === 'OWNER' ? 'approved' : 'pending',
        created_at: new Date().toISOString() // Fallback as we don't track request date yet
      }));

      return { data: requests };
    } catch (error) {
      console.error('Error fetching owner requests:', error);
      return { data: [] };
    }
  },

  async create(formData) {
    const token = authService.getToken();
    const response = await fetch(`${API_BASE_URL}/api/admin/upgrade`, {
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
  },

  async approve(userId) {
    const token = authService.getToken();
    const response = await fetch(`${API_BASE_URL}/api/admin/${userId}/approve-owner`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) throw new Error('Erro ao aprovar pedido');
    return response;
  },

  async reject(userId) {
    const token = authService.getToken();
    const response = await fetch(`${API_BASE_URL}/api/admin/${userId}/reject-owner`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) throw new Error('Erro ao rejeitar pedido');
    return response;
  }
};