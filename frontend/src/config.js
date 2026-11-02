// Configuración de la API Backend
export const API_BASE_URL = 'http://54.224.56.63:8080/api';

export const API_ENDPOINTS = {
  products: `${API_BASE_URL}/products`,
  orders: `${API_BASE_URL}/orders`,
  audit: `${API_BASE_URL}/audit`,
  data: `${API_BASE_URL}/data`,
  notify: `${API_BASE_URL}/messaging/send-email`,
};
