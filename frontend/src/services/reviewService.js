// Mock data para avaliações
const mockReviews = [
  // Reviews for car 1
  {
    id: '1',
    car_id: '1',
    user_id: '1',
    user_name: 'João Silva',
    rating: 5,
    comment: 'Excelente carro! Muito confortável e económico. Recomendo a todos.',
    created_at: '2024-11-15T10:30:00Z',
  },
  {
    id: '2',
    car_id: '1',
    user_id: '2',
    user_name: 'Maria Santos',
    rating: 4,
    comment: 'Bom carro, mas podia ter mais espaço na bagageira.',
    created_at: '2024-11-10T14:20:00Z',
  },
  {
    id: '3',
    car_id: '1',
    user_id: '3',
    user_name: 'Pedro Costa',
    rating: 5,
    comment: 'Perfeito para viagens longas. O ar condicionado funciona muito bem.',
    created_at: '2024-11-05T09:15:00Z',
  },
  // Reviews for car 2
  {
    id: '4',
    car_id: '2',
    user_id: '4',
    user_name: 'Ana Rodrigues',
    rating: 4,
    comment: 'Carro muito bonito e confortável. A transmissão automática é ótima.',
    created_at: '2024-11-12T16:45:00Z',
  },
  {
    id: '5',
    car_id: '2',
    user_id: '5',
    user_name: 'Carlos Ferreira',
    rating: 5,
    comment: 'Adorei a experiência! Carro impecável e atendimento excelente.',
    created_at: '2024-11-08T11:00:00Z',
  },
  {
    id: '6',
    car_id: '2',
    user_id: '6',
    user_name: 'Sofia Almeida',
    rating: 5,
    comment: 'Muito espaçoso e potente. Ideal para a família.',
    created_at: '2024-11-03T13:30:00Z',
  },
  // Reviews for car 3
  {
    id: '7',
    car_id: '3',
    user_id: '7',
    user_name: 'Ricardo Martins',
    rating: 4,
    comment: 'Carro desportivo incrível! A aceleração é fantástica.',
    created_at: '2024-11-14T10:00:00Z',
  },
  {
    id: '8',
    car_id: '3',
    user_id: '8',
    user_name: 'Inês Oliveira',
    rating: 5,
    comment: 'Simplesmente perfeito. Design moderno e muito conforto.',
    created_at: '2024-11-09T15:20:00Z',
  },
  // Reviews for car 4
  {
    id: '9',
    car_id: '4',
    user_id: '9',
    user_name: 'Miguel Pereira',
    rating: 5,
    comment: 'Excelente para cidade. Muito fácil de estacionar e económico.',
    created_at: '2024-11-11T09:30:00Z',
  },
  {
    id: '10',
    car_id: '4',
    user_id: '10',
    user_name: 'Beatriz Sousa',
    rating: 4,
    comment: 'Pequeno mas muito prático. Perfeito para o dia a dia.',
    created_at: '2024-11-06T14:15:00Z',
  },
  // Reviews for car 5
  {
    id: '11',
    car_id: '5',
    user_id: '11',
    user_name: 'Tiago Fernandes',
    rating: 5,
    comment: 'SUV fantástico! Muito espaço e conforto para toda a família.',
    created_at: '2024-11-13T11:45:00Z',
  },
  {
    id: '12',
    car_id: '5',
    user_id: '12',
    user_name: 'Catarina Lopes',
    rating: 4,
    comment: 'Bom carro para viagens. O GPS integrado é muito útil.',
    created_at: '2024-11-07T16:00:00Z',
  },
  // Reviews for car 6
  {
    id: '13',
    car_id: '6',
    user_id: '13',
    user_name: 'Paulo Ribeiro',
    rating: 5,
    comment: 'Carro luxuoso e potente. Valeu cada euro!',
    created_at: '2024-11-15T12:00:00Z',
  },
  {
    id: '14',
    car_id: '6',
    user_id: '14',
    user_name: 'Mariana Cardoso',
    rating: 5,
    comment: 'Simplesmente perfeito. Design elegante e muito confortável.',
    created_at: '2024-11-10T10:30:00Z',
  },
];

// Simular delay de rede
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// Serviço de API para avaliações
export const reviewService = {
  // Listar todas as avaliações com filtros opcionais
  async list(filters = {}) {
    await delay(300);
    let reviews = [...mockReviews];

    if (filters.car_id) {
      reviews = reviews.filter(r => r.car_id === filters.car_id);
    }
    if (filters.user_id) {
      reviews = reviews.filter(r => r.user_id === filters.user_id);
    }

    return { data: reviews };
  },

  // Obter uma avaliação específica
  async get(id) {
    await delay(200);
    const review = mockReviews.find(r => r.id === id);
    if (!review) {
      throw new Error('Avaliação não encontrada');
    }
    return { data: review };
  },

  // Criar uma nova avaliação
  async create(reviewData) {
    await delay(300);
    const newReview = {
      id: Date.now().toString(),
      ...reviewData,
      created_at: new Date().toISOString(),
    };
    mockReviews.push(newReview);
    return { data: newReview };
  },

  // Atualizar uma avaliação
  async update(id, reviewData) {
    await delay(300);
    const index = mockReviews.findIndex(r => r.id === id);
    if (index === -1) {
      throw new Error('Avaliação não encontrada');
    }
    mockReviews[index] = { ...mockReviews[index], ...reviewData };
    return { data: mockReviews[index] };
  },

  // Deletar uma avaliação
  async delete(id) {
    await delay(300);
    const index = mockReviews.findIndex(r => r.id === id);
    if (index === -1) {
      throw new Error('Avaliação não encontrada');
    }
    mockReviews.splice(index, 1);
    return { success: true };
  },
};
