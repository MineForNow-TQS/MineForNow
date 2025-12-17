const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export const bookingService = {
    async create(bookingData) {
        try {
            const token = localStorage.getItem('authToken');

            const response = await fetch(`${API_BASE_URL}/bookings`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(bookingData),
            });

            if (!response.ok) {
                // Tenta ler a mensagem de erro do corpo da resposta
                const errorText = await response.text();
                throw new Error(errorText || `HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Erro ao criar reserva:', error);
            throw error;
        }
    }
};
