// Utilitário para criar URLs de páginas
export function createPageUrl(pageName) {
  const routes = {
    'Home': '/',
    'Cars': '/cars',
    'CarDetails': '/cars/:id',
    'Checkout': '/checkout',
    'Dashboard': '/dashboard',
    'AddCar': '/add-car',
    'EditCar': '/edit-car/:id',
    'BecomeOwner': '/become-owner',
  };
  
  return routes[pageName] || '/';
}

// Utilitário para formatação de moeda
export function formatCurrency(value) {
  return new Intl.NumberFormat('pt-PT', {
    style: 'currency',
    currency: 'EUR',
  }).format(value);
}

// Utilitário para formatação de datas
export function formatDate(date) {
  return new Intl.DateTimeFormat('pt-PT', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(new Date(date));
}

// Utilitário para formatação de datas com hora
export function formatDateTime(date) {
  return new Intl.DateTimeFormat('pt-PT', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(date));
}

// Utilitário para validar email
export function isValidEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

// Utilitário para calcular dias entre datas
export function calculateDays(startDate, endDate) {
  const start = new Date(startDate);
  const end = new Date(endDate);
  const diffTime = Math.abs(end - start);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  return diffDays;
}

// Utilitário para slugify texto
export function slugify(text) {
  return text
    .toString()
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/\s+/g, '-')
    .replace(/[^\w-]+/g, '')
    .replace(/--+/g, '-')
    .replace(/^-+/, '')
    .replace(/-+$/, '');
}
