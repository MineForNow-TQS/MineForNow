import { API_BASE_URL } from '../config/api';

const API_URL = `${API_BASE_URL}/api`;

const paymentService = {
    confirmPayment: async (bookingId, paymentData) => {
        try {
            const token = localStorage.getItem('authToken');

            const response = await fetch(`${API_URL}/bookings/${bookingId}/confirm-payment`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(paymentData),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || `HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Erro ao confirmar pagamento:', error);
            throw error;
        }
    }
};

export default paymentService;
