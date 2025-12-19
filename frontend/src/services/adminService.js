import { authService } from './authService';
import { API_BASE_URL } from '../config/api';

export const adminService = {
    async getDashboardStats() {
        const token = authService.getToken();
        if (!token) {
            throw new Error('Não autenticado');
        }

        const response = await fetch(`${API_BASE_URL}/api/admin/stats`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            if (response.status === 403) {
                throw new Error('Acesso negado. Apenas administradores podem ver esta página.');
            }
            throw new Error('Erro ao obter estatísticas');
        }

        return await response.json();
    }
};
