import axios from 'axios';
import { API_ENDPOINTS } from '../config';

// Configuración de axios
const api = axios.create({
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Servicios de la API
export const apiService = {
  // Obtener productos
  getProducts: async () => {
    try {
      const response = await api.get(API_ENDPOINTS.products);
      return response.data;
    } catch (error) {
      console.error('Error al obtener productos:', error);
      throw error;
    }
  },

  // Obtener auditoría
  getAudit: async () => {
    try {
      const response = await api.get(API_ENDPOINTS.audit);
      return response.data;
    } catch (error) {
      console.error('Error al obtener auditoría:', error);
      throw error;
    }
  },

  // Obtener datos
  getData: async () => {
    try {
      const response = await api.get(API_ENDPOINTS.data);
      return response.data;
    } catch (error) {
      console.error('Error al obtener datos:', error);
      throw error;
    }
  },

  // Enviar notificación
  sendNotification: async (data) => {
    try {
      const response = await api.post(API_ENDPOINTS.notify, data);
      return response.data;
    } catch (error) {
      console.error('Error al enviar notificación:', error);
      throw error;
    }
  },
};

export default apiService;
