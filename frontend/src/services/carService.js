// Mock data para carros
const mockCars = [
  {
    id: '1',
    // CORRIGIDO: Era Volkswagen Golf, agora Mercedes-AMG GT
    brand: 'Mercedes-Benz',
    model: 'AMG GT',
    year: 2021,
    // Tipo: Desportivo / Coupé
    type: 'desportivo',
    fuel_type: 'gasoline',
    transmission: 'automatic',
    seats: 2,
    doors: 2,
    air_conditioning: true,
    gps: true,
    bluetooth: true,
    // Preço ajustado para um desportivo de luxo
    price_per_day: 850,
    location: 'Lisboa',
    city: 'Lisboa',
    description: 'Mercedes-AMG GT de luxo. Design deslumbrante e performance inigualável. Perfeito para uma experiência exclusiva.',
    // Imagem do Mercedes-AMG GT (preto mate)
    images: ['/Images/photo-1617814076367-b759c7d7e738.jpeg'],
    owner_id: 'admin',
    owner_email: 'admin@minefornow.com',
    status: 'available',
    unavailable_dates: [],
    average_rating: 4.8,
    total_reviews: 12,
    mileage: 18000,
  },
  {
    id: '2',
    // CORRIGIDO: Era BMW X3, agora Mercedes-AMG GT (versão ligeiramente diferente)
    brand: 'Mercedes-Benz',
    model: 'AMG GT R',
    year: 2022,
    // Tipo: Desportivo / Coupé
    type: 'desportivo',
    fuel_type: 'gasoline',
    transmission: 'automatic',
    seats: 2,
    doors: 2,
    air_conditioning: true,
    gps: true,
    bluetooth: true,
    // Preço ajustado para um desportivo de luxo de alta performance
    price_per_day: 1100,
    location: 'Porto',
    city: 'Porto',
    description: 'Mercedes-AMG GT R, a máquina de performance definitiva. Edição especial com detalhes amarelos.',
    // Imagem do Mercedes-AMG GT (cinza/grafite com detalhes amarelos)
    images: ['/Images/photo-1618843479313-40f8afb4b4d8.jpeg'],
    owner_id: 'owner',
    owner_email: 'owner@minefornow.com',
    status: 'available',
    unavailable_dates: [],
    average_rating: 4.9,
    total_reviews: 8,
    mileage: 10000,
  },
  {
    id: '3',
    brand: 'Tesla',
    model: 'Model 3',
    year: 2023,
    // Corrigido anteriormente
    type: 'sedan', 
    fuel_type: 'electric',
    transmission: 'automatic',
    seats: 5,
    doors: 4,
    air_conditioning: true,
    gps: true,
    bluetooth: true,
    price_per_day: 85,
    location: 'Faro',
    city: 'Faro',
    description: 'Tesla Model 3 elétrico, tecnologia de ponta e sustentável.',
    images: ['/Images/photo-1560958089-b8a1929cea89.jpeg'],
    owner_id: 'admin',
    owner_email: 'admin@minefornow.com',
    status: 'available',
    unavailable_dates: [],
    average_rating: 5.0,
    total_reviews: 15,
    mileage: 10000,
  },
  {
    id: '4',
    // CORRIGIDO: Era Renault Clio, agora Nissan Juke
    brand: 'Nissan',
    model: 'Juke',
    year: 2020,
    // Tipo: Crossover / SUV
    type: 'suv',
    fuel_type: 'gasoline', // Assumindo gasolina ou híbrido para o Juke
    transmission: 'manual',
    seats: 5,
    doors: 5,
    air_conditioning: true,
    gps: false,
    bluetooth: true,
    // Preço ajustado para um Crossover
    price_per_day: 42,
    location: 'Coimbra',
    city: 'Coimbra',
    description: 'Nissan Juke, um crossover compacto e distinto, perfeito para o dia a dia e pequenas aventuras.',
    // Imagem do Nissan Juke
    images: ['/Images/photo-1609521263047-f8f205293f24.jpeg'],
    owner_id: 'owner',
    owner_email: 'owner@minefornow.com',
    status: 'available',
    unavailable_dates: [],
    average_rating: 4.5,
    total_reviews: 20,
    mileage: 45000,
  },
  {
    id: '5',
    // Corrigido anteriormente: Fiat 500
    brand: 'Fiat',
    model: '500',
    year: 2023,
    type: 'citadino',
    fuel_type: 'gasoline',
    transmission: 'automatic',
    seats: 4,
    doors: 3,
    air_conditioning: true,
    gps: true,
    bluetooth: true,
    price_per_day: 40,
    location: 'Cascais',
    city: 'Cascais',
    description: 'Fiat 500 charmoso e compacto, ideal para passeios na cidade e estacionamento fácil.',
    images: ['/Images/photo-1549317661-bd32c8ce0db2.jpeg'],
    owner_id: 'admin',
    owner_email: 'admin@minefornow.com',
    status: 'available',
    unavailable_dates: [],
    average_rating: 4.9,
    total_reviews: 10,
    mileage: 5000,
  },
  {
    id: '6',
    // Corrigido anteriormente: Ferrari Roma
    brand: 'Ferrari',
    model: 'Roma',
    year: 2024,
    type: 'desportivo',
    fuel_type: 'gasoline',
    transmission: 'automatic',
    seats: 2,
    doors: 2,
    air_conditioning: true,
    gps: true,
    bluetooth: true,
    price_per_day: 950,
    location: 'Lisboa',
    city: 'Lisboa',
    description: 'Ferrari Roma desportivo de luxo, uma experiência de condução inesquecível.',
    images: ['/Images/photo-1606220838315-056192d5e927.jpeg'],
    owner_id: 'owner',
    owner_email: 'owner@minefornow.com',
    status: 'available',
    unavailable_dates: [],
    average_rating: 4.7,
    total_reviews: 14,
    mileage: 1000,
  },
];

// Simular delay de rede
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// Serviço de API para carros
export const carService = {
  // Listar todos os carros com filtros opcionais
  async list(filters = {}) {
    await delay(300);
    let cars = [...mockCars];

    // Aplicar filtros
    if (filters.city) {
      cars = cars.filter(car => car.city.toLowerCase() === filters.city.toLowerCase());
    }
    if (filters.type) {
      cars = cars.filter(car => car.type === filters.type);
    }
    if (filters.fuel_type) {
      cars = cars.filter(car => car.fuel_type === filters.fuel_type);
    }
    if (filters.transmission) {
      cars = cars.filter(car => car.transmission === filters.transmission);
    }
    if (filters.minPrice) {
      cars = cars.filter(car => car.price_per_day >= filters.minPrice);
    }
    if (filters.maxPrice) {
      cars = cars.filter(car => car.price_per_day <= filters.maxPrice);
    }
    if (filters.owner_id) {
      cars = cars.filter(car => car.owner_id === filters.owner_id);
    }
    if (filters.status) {
      cars = cars.filter(car => car.status === filters.status);
    }

    return { data: cars };
  },

  // Obter um carro específico
  async get(id) {
    await delay(200);
    const car = mockCars.find(c => c.id === id);
    if (!car) {
      throw new Error('Carro não encontrado');
    }
    return { data: car };
  },

  // Obter carros de um owner específico
  async getCarsByOwner(ownerId) {
    await delay(300);
    const cars = mockCars.filter(car => 
      car.owner_id === ownerId || car.owner_email === ownerId
    );
    return { data: cars };
  },

  // Criar um novo carro
  async create(carData) {
    await delay(300);
    const newCar = {
      id: Date.now().toString(),
      ...carData,
      status: 'pending',
      unavailable_dates: [],
      average_rating: 0,
      total_reviews: 0,
      images: carData.images || [],
    };
    mockCars.push(newCar);
    return { data: newCar };
  },

  // Atualizar um carro
  async update(id, carData) {
    await delay(300);
    const index = mockCars.findIndex(c => c.id === id);
    if (index === -1) {
      throw new Error('Carro não encontrado');
    }
    mockCars[index] = { ...mockCars[index], ...carData };
    return { data: mockCars[index] };
  },

  // Alias para update (para compatibilidade)
  async updateCar(id, carData) {
    return this.update(id, carData);
  },

  // Deletar um carro
  async delete(id) {
    await delay(300);
    const index = mockCars.findIndex(c => c.id === id);
    if (index === -1) {
      throw new Error('Carro não encontrado');
    }
    mockCars.splice(index, 1);
    return { success: true };
  },

  // Alias para delete (para compatibilidade)
  async deleteCar(id) {
    return this.delete(id);
  },

  // Alias para list (para compatibilidade com AdminStatsDashboard)
  async searchCars(filters = {}) {
    return this.list(filters);
  },
};