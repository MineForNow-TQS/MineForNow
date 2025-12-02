// Mock data para pedidos de proprietário
const mockOwnerRequests = [];

// Simular delay de rede
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// Serviço de API para pedidos de proprietário
export const ownerRequestService = {
  // Listar todos os pedidos com filtros opcionais
  async list(filters = {}) {
    await delay(300);
    let requests = [...mockOwnerRequests];

    if (filters.user_email) {
      requests = requests.filter(r => r.user_email === filters.user_email);
    }
    if (filters.status) {
      requests = requests.filter(r => r.status === filters.status);
    }

    return { data: requests };
  },

  // Obter um pedido específico
  async get(id) {
    await delay(200);
    const request = mockOwnerRequests.find(r => r.id === id);
    if (!request) {
      throw new Error('Pedido não encontrado');
    }
    return { data: request };
  },

  // Criar um novo pedido
  async create(requestData) {
    await delay(300);
    const newRequest = {
      id: Date.now().toString(),
      ...requestData,
      status: 'pending',
      created_at: new Date().toISOString(),
    };
    mockOwnerRequests.push(newRequest);
    return { data: newRequest };
  },

  // Atualizar um pedido
  async update(id, requestData) {
    await delay(300);
    const index = mockOwnerRequests.findIndex(r => r.id === id);
    if (index === -1) {
      throw new Error('Pedido não encontrado');
    }
    mockOwnerRequests[index] = { ...mockOwnerRequests[index], ...requestData };
    return { data: mockOwnerRequests[index] };
  },

  // Deletar um pedido
  async delete(id) {
    await delay(300);
    const index = mockOwnerRequests.findIndex(r => r.id === id);
    if (index === -1) {
      throw new Error('Pedido não encontrado');
    }
    mockOwnerRequests.splice(index, 1);
    return { success: true };
  },
};
