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
          // Intentar obtener el token silenciosamente con el scope correcto
          const response = await msalInstance.acquireTokenSilent({
            scopes: ['api://faba8741-ba0d-440c-b061-f1aa893eb957/access_as_user'],
            account: accounts[0],
          });
          
          if (response.accessToken) {
            config.headers.Authorization = `Bearer ${response.accessToken}`;
            console.log('Token JWT agregado correctamente');
          }
        }
      }
    } catch (error) {
      console.error('Error al obtener token:', error);
      
      // Si falla silencioso, intentar con popup
      if (error.name === 'InteractionRequiredAuthError' && msalInstance) {
        try {
          const accounts = msalInstance.getAllAccounts();
          const response = await msalInstance.acquireTokenPopup({
            scopes: ['api://faba8741-ba0d-440c-b061-f1aa893eb957/access_as_user'],
            account: accounts[0],
          });
          config.headers.Authorization = `Bearer ${response.accessToken}`;
        } catch (popupError) {
          console.error('Error en popup:', popupError);
        }
      }
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
