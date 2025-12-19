import { authService } from './authService';
import { API_BASE_URL } from '../config/api';

const getHeaders = () => ({
    'Authorization': `Bearer ${authService.getToken()}`,
    'Content-Type': 'application/json'
});

export const adminService = {
    // CORREGIDO: Añadido /api/admin
    getPendingRequests: async () => {
        const response = await fetch(`${API_BASE_URL}/api/admin/requests/pending`, {
            headers: getHeaders()
        });
        if (!response.ok) throw new Error('Erro ao carregar pedidos pendentes');
        return response.json();
    },

    // ESTE ESTABA BIEN
    approveRequest: async (userId) => {
        const response = await fetch(`${API_BASE_URL}/api/admin/requests/${userId}/approve`, {
            method: 'PUT',
            headers: getHeaders()
        });
        if (!response.ok) throw new Error('Erro ao aprovar pedido');
        return response.json();
    },

    // CORREGIDO: Añadido /api/admin
    rejectRequest: async (userId) => {
        const response = await fetch(`${API_BASE_URL}/api/admin/requests/${userId}/reject`, {
            method: 'PUT',
            headers: getHeaders()
        });
        if (!response.ok) throw new Error('Erro ao rejeitar pedido');
        return response.json();
    }
};