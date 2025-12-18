import { API_BASE_URL, getImageUrl } from '../config/api';

// Base URL do backend
const FULL_API_URL = `${API_BASE_URL}/api`;

// Imagens placeholder para veículos sem imagem
const PLACEHOLDER_IMAGES = [
  '/Images/photo-1449965408869-eaa3f722e40d.jpeg',
  '/Images/photo-1494976388531-d1058494cdd8.jpeg',
  '/Images/photo-1503376780353-7e6692767b70.jpeg',
  '/Images/photo-1549317661-bd32c8ce0db2.jpeg',
  '/Images/photo-1560958089-b8a1929cea89.jpeg',
  '/Images/photo-1606220838315-056192d5e927.jpeg',
  '/Images/photo-1609521263047-f8f205293f24.jpeg',
  '/Images/photo-1617814076367-b759c7d7e738.jpeg',
  '/Images/photo-1618843479313-40f8afb4b4d8.jpeg',
];

// Obtém URL da imagem do carro - usa imageUrl se existir, senão usa placeholder
const getCarImageUrl = (imageUrl, carId) => {
  // Se tem imageUrl válida
  if (imageUrl && imageUrl.trim() !== '') {
    // Se a URL começa com /api, adiciona o host do backend
    if (imageUrl.startsWith('/api')) {
      return getImageUrl(imageUrl);
    }
    return imageUrl;
  }
  // Usa placeholder baseado no ID do carro para consistência
  const index = carId ? Math.abs(carId) % PLACEHOLDER_IMAGES.length : Math.floor(Math.random() * PLACEHOLDER_IMAGES.length);
  return PLACEHOLDER_IMAGES[index];
};

// Adaptador: Converte dados do backend (camelCase) para o formato do frontend (snake_case)
const adaptVehicleFromBackend = (vehicle) => {
  const imageUrl = getCarImageUrl(vehicle.imageUrl, vehicle.id);
  return {
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
    images: [imageUrl],
    image_url: imageUrl,
    license_plate: vehicle.licensePlate,
    mileage: vehicle.mileage,
    display_name: vehicle.displayName,
    formatted_price: vehicle.formattedPrice,
    owner_name: vehicle.ownerName,
    owner_email: vehicle.ownerEmail,
  };
};

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

      const url = `${FULL_API_URL}/vehicles/search${params.toString() ? '?' + params.toString() : ''}`;
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
      const response = await fetch(`${FULL_API_URL}/vehicles/${id}`);

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

  // Obter carros do owner autenticado
  async getCarsByOwner() {
    try {
      const token = localStorage.getItem('authToken');
      const headers = {};

      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      const response = await fetch(`${FULL_API_URL}/vehicles/my-vehicles`, {
        headers,
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      let cars = await response.json();
      // Adaptar dados do backend para o formato do frontend
      cars = cars.map(adaptVehicleFromBackend);
      return { data: cars };
    } catch (error) {
      console.error('Erro ao buscar carros do owner:', error);
      throw error;
    }
  },

  // Adaptador: Converte dados do frontend (snake_case) para o formato do backend (camelCase)
  adaptVehicleToBackend(carData) {
    return {
      brand: carData.brand,
      model: carData.model,
      year: carData.year,
      type: carData.type,
      fuelType: carData.fuel_type,
      transmission: carData.transmission,
      seats: carData.seats,
      doors: carData.doors,
      hasAC: carData.air_conditioning,
      hasGPS: carData.gps,
      hasBluetooth: carData.bluetooth,
      pricePerDay: carData.price_per_day,
      exactLocation: carData.location,
      city: carData.city,
      description: carData.description,
      licensePlate: carData.license_plate,
      mileage: carData.mileage,
      imageUrl: carData.image_url,
    };
  },

  // Criar um novo carro
  async create(carData) {
    try {
      const token = localStorage.getItem('authToken');
      const headers = {
        'Content-Type': 'application/json',
      };

      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      // Converter para o formato do backend
      const backendData = this.adaptVehicleToBackend(carData);

      const response = await fetch(`${FULL_API_URL}/vehicles`, {
        method: 'POST',
        headers,
        body: JSON.stringify(backendData),
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
      const token = localStorage.getItem('authToken');
      const headers = {
        'Content-Type': 'application/json',
      };

      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      // Converter para o formato do backend
      const backendData = this.adaptVehicleToBackend(carData);

      const response = await fetch(`${FULL_API_URL}/vehicles/${id}`, {
        method: 'PUT',
        headers,
        body: JSON.stringify(backendData),
      });

      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Carro não encontrado');
        }
        if (response.status === 403) {
          throw new Error('Não tem permissão para editar este carro');
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
      const token = localStorage.getItem('authToken');
      const headers = {};

      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      const response = await fetch(`${FULL_API_URL}/vehicles/${id}`, {
        method: 'DELETE',
        headers,
      });

      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Carro não encontrado');
        }
        if (response.status === 403) {
          throw new Error('Não tem permissão para eliminar este carro');
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

  // Upload de imagem
  async uploadImage(file) {
    try {
      const token = localStorage.getItem('authToken');
      const formData = new FormData();
      formData.append('file', file);

      const headers = {};
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      const response = await fetch(`${FULL_API_URL}/files/upload`, {
        method: 'POST',
        headers,
        body: formData,
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || `HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      return { url: result.url };
    } catch (error) {
      console.error('Erro ao fazer upload da imagem:', error);
      throw error;
    }
  },
};
