import api from './api';

const paymentService = {
    confirmPayment: async (bookingId, paymentData) => {
        try {
            const response = await api.post(`/bookings/${bookingId}/confirm-payment`, paymentData);
            return response.data;
        } catch (error) {
            throw error;
        }
    }
};

export default paymentService;
