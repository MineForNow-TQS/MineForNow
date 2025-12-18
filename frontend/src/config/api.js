/**
 * API Configuration
 * Detecta o ambiente e define a base URL apropriada
 */

// Em produção, o frontend é servido pela mesma aplicação Spring
// Portanto, usamos URLs relativas
const getApiBaseUrl = () => {
  // Se estamos em desenvolvimento (npm run dev)
  if (import.meta.env.DEV) {
    // Usa localhost
    return import.meta.env.VITE_API_URL || 'http://localhost:8080';
  }

  // Em produção, usa URL relativa (a mesma origem)
  return '';
};

export const API_BASE_URL = getApiBaseUrl();

// Função helper para fazer requests relativas
export const makeRequest = async (endpoint, options = {}) => {
  const url = API_BASE_URL ? `${API_BASE_URL}${endpoint}` : endpoint;
  return fetch(url, options);
};

// Função para processar URLs de imagens
export const getImageUrl = (imageUrl) => {
  if (!imageUrl) return null;
  
  // Se a URL é absoluta, retorna como está
  if (imageUrl.startsWith('http')) {
    return imageUrl;
  }

  // Se é relativa, adiciona a base URL
  if (API_BASE_URL) {
    return `${API_BASE_URL}${imageUrl}`;
  }

  // Em produção sem base URL, retorna relativa
  return imageUrl;
};
