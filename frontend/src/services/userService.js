import { authService } from './authService';
import { API_BASE_URL } from '../config/api';

// Mock data para Utilizadors - carrega do localStorage se existir
const loadUsers = () => {
  const stored = localStorage.getItem('mockUsers');
  if (stored) {
    return JSON.parse(stored);
  }
  return [
    {
      id: '1',
      fullName: 'Admin User',
      email: 'admin@minefornow.com',
      full_name: 'Admin User',
      role: 'admin',
      status: 'active',
      created_at: new Date('2024-01-01').toISOString()
    },
    {
      id: '2',
      fullName: 'Owner User',
      email: 'owner@minefornow.com',
      full_name: 'Owner User',
      role: 'owner',
      status: 'active',
      created_at: new Date('2024-01-15').toISOString()
    },
    {
      id: '3',
      fullName: 'Rental User',
      email: 'rental@minefornow.com',
      full_name: 'Rental User',
      role: 'rental',
      status: 'active',
      created_at: new Date('2024-02-01').toISOString()
    }
  ];
};

let mockUsers = loadUsers();

// Salvar no localStorage sempre que modificar
const saveUsers = () => {
  localStorage.setItem('mockUsers', JSON.stringify(mockUsers));
};

// Simular delay de rede
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// Serviço de API para Utilizadors
export const userService = {
  // ============================================
  // REAL API METHODS (Backend endpoints)
  // ============================================

  // Obter dados do utilizador atual (GET /api/users/me)
  async getCurrentUser() {
    const token = authService.getToken();
    if (!token) {
      throw new Error('Não autenticado');
    }

    const response = await fetch(`${API_BASE_URL}/api/users/me`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error('Erro ao obter dados do utilizador');
    }

    const data = await response.json();
    return { data };
  },

  // Atualizar dados do utilizador atual (PUT /api/users/me)
  async updateCurrentUserProfile(profileData) {
    const token = authService.getToken();
    if (!token) {
      console.error('No auth token found in localStorage');
      throw new Error('Não autenticado - token não encontrado');
    }

    const response = await fetch(`${API_BASE_URL}/api/users/me`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(profileData),
    });

    if (!response.ok) {
      if (response.status === 401) {
        throw new Error('Sessão expirada. Por favor, faça login novamente.');
      }
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || 'Erro ao atualizar perfil');
    }

    const data = await response.json();
    return { data };
  },

  // ============================================
  // MOCK METHODS (Temporary for other features)
  // ============================================

  // Listar todos os Utilizadors
  async list() {
    await delay(200);
    return { data: [...mockUsers] };
  },

  // Obter um Utilizador específico
  async get(id) {
    await delay(200);
    const user = mockUsers.find(u => u.id === id);
    if (!user) {
      throw new Error('Utilizador não encontrado');
    }
    return { data: user };
  },

  // Obter Utilizador por email
  async getByEmail(email) {
    await delay(200);
    const user = mockUsers.find(u => u.email === email);
    if (!user) {
      throw new Error('Utilizador não encontrado');
    }
    return { data: user };
  },

  // Criar um novo Utilizador
  async create(userData) {
    await delay(300);
    const newUser = {
      id: Date.now().toString(),
      ...userData,
      status: 'active',
      created_at: new Date().toISOString(),
    };
    mockUsers.push(newUser);
    saveUsers();
    return { data: newUser };
  },

  // Atualizar um Utilizador
  async update(id, userData) {
    await delay(300);
    const index = mockUsers.findIndex(u => u.id === id);
    if (index === -1) {
      throw new Error('Utilizador não encontrado');
    }
    mockUsers[index] = { ...mockUsers[index], ...userData };
    saveUsers();
    return { data: mockUsers[index] };
  },

  // Atualizar role do Utilizador
  async updateRole(id, newRole) {
    await delay(300);
    const index = mockUsers.findIndex(u => u.id === id);
    if (index === -1) {
      throw new Error('Utilizador não encontrado');
    }
    mockUsers[index] = { ...mockUsers[index], role: newRole };
    saveUsers();
    return { data: mockUsers[index] };
  },

  // Bloquear/desbloquear Utilizador
  async toggleStatus(id) {
    await delay(300);
    const index = mockUsers.findIndex(u => u.id === id);
    if (index === -1) {
      throw new Error('Utilizador não encontrado');
    }
    const newStatus = mockUsers[index].status === 'active' ? 'blocked' : 'active';
    mockUsers[index] = { ...mockUsers[index], status: newStatus };
    saveUsers();
    return { data: mockUsers[index] };
  },

  // Estatísticas
  async getStats() {
    await delay(200);
    const totalUsers = mockUsers.length;
    const totalOwners = mockUsers.filter(u => u.role === 'owner' || u.role === 'admin').length;
    const totalRentals = mockUsers.filter(u => u.role === 'rental').length;

    return {
      data: {
        totalUsers,
        totalOwners,
        totalRentals
      }
    };
  }
};
