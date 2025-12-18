const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export const bookingService = {
    async create(bookingData) {
        try {
            const token = localStorage.getItem('authToken');

            if (!token) {
                throw new Error('Não autenticado. Por favor, faça login novamente.');
            }

            console.log('Creating booking with token:', token ? 'Token exists' : 'No token');
            console.log('Booking data:', bookingData);

            const response = await fetch(`${API_BASE_URL}/bookings`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(bookingData),
            });

            if (!response.ok) {
                if (response.status === 401) {
                    throw new Error('Sessão expirada. Por favor, faça login novamente.');
                }
                // Tenta ler a mensagem de erro do corpo da resposta
                const errorText = await response.text();
                throw new Error(errorText || `HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Erro ao criar reserva:', error);
            throw error;
        }
    },

    async getMyBookings() {
        try {
            const token = localStorage.getItem('authToken');

            if (!token) {
                throw new Error('Não autenticado. Por favor, faça login novamente.');
            }

            const response = await fetch(`${API_BASE_URL}/bookings/my-bookings`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                if (response.status === 401) {
                    throw new Error('Sessão expirada. Por favor, faça login novamente.');
                }
                const errorText = await response.text();
                throw new Error(errorText || `HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar reservas:', error);
            throw error;
        }
    }
};
