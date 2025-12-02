// Mock data para reservas
const mockReservations = [];

// Simular delay de rede
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// Serviço de API para reservas
export const reservationService = {
  // Listar todas as reservas com filtros opcionais
  async list(filters = {}) {
    await delay(300);
    let reservations = [...mockReservations];

    if (filters.user_id) {
      reservations = reservations.filter(r => r.user_id === filters.user_id);
    }
    if (filters.car_id) {
      reservations = reservations.filter(r => r.car_id === filters.car_id);
    }
    if (filters.status) {
      reservations = reservations.filter(r => r.status === filters.status);
    }

    return { data: reservations };
  },

  // Obter uma reserva específica
  async get(id) {
    await delay(200);
    const reservation = mockReservations.find(r => r.id === id);
    if (!reservation) {
      throw new Error('Reserva não encontrada');
    }
    return { data: reservation };
  },

  // Criar uma nova reserva
  async create(reservationData) {
    await delay(300);
    const newReservation = {
      id: Date.now().toString(),
      ...reservationData,
      status: 'pending',
      created_at: new Date().toISOString(),
    };
    mockReservations.push(newReservation);
    return { data: newReservation };
  },

  // Atualizar uma reserva
  async update(id, reservationData) {
    await delay(300);
    const index = mockReservations.findIndex(r => r.id === id);
    if (index === -1) {
      throw new Error('Reserva não encontrada');
    }
    mockReservations[index] = { ...mockReservations[index], ...reservationData };
    return { data: mockReservations[index] };
  },

  // Deletar uma reserva
  async delete(id) {
    await delay(300);
    const index = mockReservations.findIndex(r => r.id === id);
    if (index === -1) {
      throw new Error('Reserva não encontrada');
    }
    mockReservations.splice(index, 1);
    return { success: true };
  },
};
