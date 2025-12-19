import { API_BASE_URL } from '../config/api';

// Base URL do backend
const FULL_API_URL = `${API_BASE_URL}/api`;

/**
 * Serviço de API para avaliações de veículos
 * Conecta ao endpoint real do backend: GET /api/vehicles/{id}/reviews
 */
export const reviewService = {
  /**
   * Obter reviews de um veículo específico
   * @param {number|string} vehicleId - ID do veículo
   * @returns {Promise<{averageRating: number, totalReviews: number, reviews: Array}>}
   */
  async getVehicleReviews(vehicleId) {
    try {
      const response = await fetch(`${FULL_API_URL}/vehicles/${vehicleId}/reviews`);

      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Veículo não encontrado');
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();

      // Backend retorna: { averageRating, totalReviews, reviews: [{id, reviewerName, rating, comment, createdAt}] }
      // Adaptar para formato compatível com frontend se necessário
      return {
        averageRating: data.averageRating || 0,
        totalReviews: data.totalReviews || 0,
        reviews: (data.reviews || []).map(review => ({
          id: review.id,
          reviewerName: review.reviewerName,
          rating: review.rating,
          comment: review.comment,
          createdAt: review.createdAt,
        })),
      };
    } catch (error) {
      console.error('Erro ao buscar reviews do veículo:', error);
      throw error;
    }
  },

  /**
   * Listar reviews com filtros (mantido para compatibilidade, mas usa getVehicleReviews)
   * @deprecated Use getVehicleReviews(vehicleId) instead
   */
  async list(filters = {}) {
    if (filters.car_id) {
      const data = await this.getVehicleReviews(filters.car_id);
      return { data: data.reviews };
    }
    // Se não tem car_id, retorna array vazio
    return { data: [] };
  },
};
