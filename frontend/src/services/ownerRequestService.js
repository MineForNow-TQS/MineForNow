import { authService } from './authService';
import { userService } from './userService';

const API_BASE_URL = 'http://localhost:8080';

export const ownerRequestService = {
  // RESTAURA ESTA FUNCIÓN PARA EL DASHBOARD
  async list() {
    try {
      // Obtenemos los datos del usuario real desde el backend
      const { data: user } = await userService.getCurrentUser();
      
      // Si el rol en el backend es PENDING_OWNER, devolvemos un array con un objeto
      // para que el Dashboard muestre el estado "Pendiente".
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