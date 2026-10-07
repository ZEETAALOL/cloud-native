import { LogLevel } from '@azure/msal-browser';

// Configuración de Azure AD / Microsoft Entra ID
export const msalConfig = {
  auth: {
    clientId: 'faba8741-ba0d-440c-b061-f1aa893eb957', // Application (client) ID
    authority: 'https://login.microsoftonline.com/47c2bee0-5950-430f-9276-bfc083e3d1da', // Tenant ID
    redirectUri: window.location.origin, // Usa la URL actual automáticamente
    postLogoutRedirectUri: window.location.origin,
  },
  cache: {
    cacheLocation: 'sessionStorage', // Guardar tokens en sessionStorage
    storeAuthStateInCookie: false,
  },
  system: {
    loggerOptions: {
      loggerCallback: (level, message, containsPii) => {
        if (containsPii) {
          return;
        }
        switch (level) {
          case LogLevel.Error:
            console.error(message);
            return;
          case LogLevel.Info:
            console.info(message);
            return;
          case LogLevel.Verbose:
            console.debug(message);
            return;
          case LogLevel.Warning:
            console.warn(message);
            return;
        }
      },
    },
  },
};

// Scopes requeridos para la API
export const loginRequest = {
  scopes: ['api://faba8741-ba0d-440c-b061-f1aa893eb957/access_as_user'],
};

// Configuración para obtener token de acceso
export const tokenRequest = {
  scopes: ['api://faba8741-ba0d-440c-b061-f1aa893eb957/access_as_user'],
  forceRefresh: false,
};
