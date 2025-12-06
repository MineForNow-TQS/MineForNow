// Base URL do backend
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Adaptador: Converte dados do backend (camelCase) para o formato do frontend (snake_case)
const adaptVehicleFromBackend = (vehicle) => ({
  id: vehicle.id,
  brand: vehicle.brand,
  model: vehicle.model,
  year: vehicle.year,
  type: vehicle.type,
  fuel_type: vehicle.fuelType,
  transmission: vehicle.transmission,
  seats: vehicle.seats,
  doors: vehicle.doors,
  air_conditioning: vehicle.hasAC,
  gps: vehicle.hasGPS,
  bluetooth: vehicle.hasBluetooth,
  price_per_day: vehicle.pricePerDay,
  location: vehicle.exactLocation,
  city: vehicle.city,
  description: vehicle.description,
  images: vehicle.imageUrl ? [vehicle.imageUrl] : [],
  image_url: vehicle.imageUrl,
  license_plate: vehicle.licensePlate,
  mileage: vehicle.mileage,
});

export const carService = {
  // Listar todos os carros com filtros opcionais
  async list(filters = {}) {
    try {
      // Construir query parameters
      const params = new URLSearchParams();
      
      if (filters.city) {
        params.append('city', filters.city);
      }
      if (filters.pickupDate) {
        params.append('pickup', filters.pickupDate);
      }
      if (filters.returnDate) {
        params.append('dropoff', filters.returnDate);
      }

      const url = `${API_BASE_URL}/vehicles/search${params.toString() ? '?' + params.toString() : ''}`;
      const response = await fetch(url);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      let cars = await response.json();
      
      // Adaptar dados do backend para o formato do frontend
      cars = cars.map(adaptVehicleFromBackend);

      // Aplicar filtros adicionais do frontend (tipo, combustível, etc.)
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

      return { data: cars };
    } catch (error) {
      console.error('Erro ao buscar carros:', error);
      throw error;
    }
  },

  // Obter um carro específico
  async get(id) {
    try {
      const response = await fetch(`${API_BASE_URL}/vehicles/${id}`);
      
      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Carro não encontrado');
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const car = await response.json();
      return { data: adaptVehicleFromBackend(car) };
    } catch (error) {
      console.error('Erro ao buscar carro:', error);
      throw error;
    }
  },

  // Obter carros de um owner específico
  async getCarsByOwner(ownerId) {
    try {
      // Por enquanto usa o list com filtro
      const result = await this.list({ owner_id: ownerId });
      return result;
    } catch (error) {
      console.error('Erro ao buscar carros do owner:', error);
      throw error;
    }
  },

  // Criar um novo carro
  async create(carData) {
    try {
      const response = await fetch(`${API_BASE_URL}/vehicles`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(carData),
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const newCar = await response.json();
      return { data: newCar };
    } catch (error) {
      console.error('Erro ao criar carro:', error);
      throw error;
    }
  },

  // Atualizar um carro
  async update(id, carData) {
    try {
      const response = await fetch(`${API_BASE_URL}/vehicles/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(carData),
      });
      
      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Carro não encontrado');
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const updatedCar = await response.json();
      return { data: updatedCar };
    } catch (error) {
      console.error('Erro ao atualizar carro:', error);
      throw error;
    }
  },

  // Alias para update (para compatibilidade)
  async updateCar(id, carData) {
    return this.update(id, carData);
  },

  // Deletar um carro
  async delete(id) {
    try {
      const response = await fetch(`${API_BASE_URL}/vehicles/${id}`, {
        method: 'DELETE',
      });
      
      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Carro não encontrado');
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      return { success: true };
    } catch (error) {
      console.error('Erro ao deletar carro:', error);
      throw error;
    }
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
