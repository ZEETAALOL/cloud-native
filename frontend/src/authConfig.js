import { LogLevel } from '@azure/msal-browser';

// Configuración de Azure AD / Microsoft Entra ID
export const msalConfig = {
  auth: {
    clientId: 'faba8741-ba0d-440c-b061-f1aa893eb957', // Application (client) ID
    authority: 'https://login.microsoftonline.com/47c2bee0-5950-430f-9276-bfc083e3d1da', // Tenant ID
    redirectUri: 'http://localhost:5173', // Redirect URI configurado en Azure
    postLogoutRedirectUri: 'http://localhost:5173',
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
  scopes: ['User.Read', 'openid', 'profile'], // Scopes básicos de Microsoft Graph
};

// Configuración para obtener token de acceso
export const tokenRequest = {
  scopes: [`api://${msalConfig.auth.clientId}/access_as_user`],
  forceRefresh: false,
};
