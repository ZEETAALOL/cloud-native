# Frontend - Pedidos360

Aplicación web React con autenticación Azure AD (MSAL).

## 🛠️ Stack Tecnológico

- **Framework:** React 18
- **Lenguaje:** TypeScript
- **Build:** Vite
- **Autenticación:** MSAL (Microsoft Authentication Library)
- **Estilos:** CSS
- **HTTP Client:** Axios

## 🚀 Inicio Rápido

### Instalación
```powershell
npm install
```

### Desarrollo
```powershell
npm run dev
```
Abre http://localhost:5173

### Build Producción
```powershell
npm run build
```

## 🔐 Configuración Azure AD

Archivo: `.env.local`

```env
# Azure AD Configuration
VITE_ENTRA_TENANT_ID=47c2bee0-5950-430f-9276-bfc083e3d1da
VITE_SPA_CLIENT_ID=89afc56b-284c-4afa-a6bf-442c94b7eb44
VITE_API_CLIENT_ID=faba8741-ba0d-440c-b061-f1aa893eb957

# API Configuration
VITE_BFF_BASE_URL=http://localhost:8080
```

## 📁 Estructura

```
frontend/
├── src/
│   ├── main.tsx          # Entry point
│   ├── App.tsx           # Componente principal
│   ├── authConfig.ts     # Configuración MSAL
│   ├── token.ts          # Lógica de tokens
│   └── api.ts            # Cliente HTTP
├── index.html
├── package.json
├── vite.config.ts
└── .env.local
```

## 🔄 Flujo de Autenticación

1. Usuario hace click en "Iniciar sesión"
2. MSAL redirige a Azure AD
3. Usuario ingresa credenciales
4. Azure AD redirige de vuelta con código
5. MSAL obtiene access token
6. Token se envía en cada request al BFF

## 🌐 Comunicación con Backend

```typescript
// En api.ts
const response = await axios.get(`${BFF_URL}/api/data`, {
  headers: {
    Authorization: `Bearer ${accessToken}`
  }
});
```

## 📋 Scripts Disponibles

| Script | Descripción |
|--------|-------------|
| `npm run dev` | Servidor desarrollo (puerto 5173) |
| `npm run build` | Build para producción |
| `npm run preview` | Preview del build |

## 🐛 Troubleshooting

### Login no funciona
- Verifica `.env.local` con IDs correctos
- Revisa que la app esté registrada en Azure AD
- Verifica redirect URI en Azure: `http://localhost:5173`

### Error 401 en llamadas API
- Verifica que BFF esté corriendo en puerto 8080
- Limpia session storage (F12 → Application → Clear)
- Verifica que el token tenga el scope correcto

### CORS Error
- Verifica configuración CORS en BFF
- Debe permitir origen `http://localhost:5173`
