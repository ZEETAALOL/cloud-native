import axios from 'axios';
import { API_ENDPOINTS } from '../config';

// Esta variable se inicializará desde el componente App
let msalInstance = null;

// Función para configurar la instancia de MSAL
export const setMsalInstance = (instance) => {
  msalInstance = instance;
};

// Configuración de axios
const api = axios.create({
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para agregar el token JWT a todas las requests
api.interceptors.request.use(
  async (config) => {
    try {
      if (msalInstance) {
        const accounts = msalInstance.getAllAccounts();
        
        if (accounts.length > 0) {
          // Intentar obtener el token silenciosamente
          const response = await msalInstance.acquireTokenSilent({
            scopes: ['openid', 'profile', 'User.Read'],
            account: accounts[0],
          });
          
          if (response.accessToken) {
            config.headers.Authorization = `Bearer ${response.accessToken}`;
            console.log('Token agregado a la request');
          }
        }
      }
    } catch (error) {
      console.error('Error al obtener token:', error);
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

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

  // Crear producto
  createProduct: async (productData) => {
    try {
      const response = await api.post(API_ENDPOINTS.products, productData);
      return response.data;
    } catch (error) {
      console.error('Error al crear producto:', error);
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
