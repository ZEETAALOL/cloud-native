# 🚀 Instrucciones de Setup - Proyecto Pedidos360

## 📋 Prerequisitos

Asegúrate de tener instalado:
- ✅ Node.js 22.12+ y npm
- ✅ Java 17+ (para services/orders) y Java 25 (para services/bff)
- ✅ Maven
- ✅ Una cuenta de Azure AD

## 🔧 Paso 1: Configurar Azure AD

### 1.1 Crear Tenant y Usuario
1. Ve a https://portal.azure.com
2. Abre **Microsoft Entra ID**
3. Copia el **Id. de directorio (inquilino)** → Anótalo como `TENANT_ID`
4. Crea un usuario de prueba (ej: `alumno01@tudominio.onmicrosoft.com`)

### 1.2 Registrar la API (api-fullstack)
1. Ve a **Registros de aplicaciones** → **Nuevo registro**
2. Nombre: `api-fullstack`
3. Tipo: **Cuentas solo de este directorio organizacional**
4. No configurar URI de redirección
5. Copia el **Id. de aplicación (cliente)** → Anótalo como `API_CLIENT_ID`
6. Ve a **Manifiesto** y cambia `requestedAccessTokenVersion` a `2`
7. Ve a **Exponer una API**:
   - Establece URI: `api://<API_CLIENT_ID>`
   - Agrega ámbito: `access_as_user` (Solo administradores)

### 1.3 Registrar la SPA (spa-fullstack)
1. Ve a **Registros de aplicaciones** → **Nuevo registro**
2. Nombre: `spa-fullstack`
3. Tipo: **Cuentas solo de este directorio organizacional**
4. URI de redirección: **Aplicación de página única (SPA)** → `http://localhost:5173/redirect.html`
5. Copia el **Id. de aplicación (cliente)** → Anótalo como `SPA_CLIENT_ID`
6. Ve a **Autenticación** y agrega también: `http://localhost:5173`
7. Ve a **Permisos de API**:
   - Agregar permiso → Mis API → `api-fullstack`
   - Permisos delegados → `access_as_user`
   - **Conceder consentimiento de administrador**

## 📦 Paso 2: Instalar Dependencias del Frontend

```powershell
cd frontend
npm install
```

## ⚙️ Paso 3: Configurar Variables de Entorno

### Frontend
Crea el archivo `frontend/.env.local`:

```env
VITE_ENTRA_TENANT_ID=<tu-TENANT_ID>
VITE_SPA_CLIENT_ID=<tu-SPA_CLIENT_ID>
VITE_API_CLIENT_ID=<tu-API_CLIENT_ID>
VITE_API_BASE_URL=
VITE_BFF_BASE_URL=http://localhost:8080
```

### Backend (PowerShell)
En la terminal donde ejecutarás el BFF:

```powershell
$env:ENTRA_ISSUER_URI="https://login.microsoftonline.com/<tu-TENANT_ID>/v2.0"
$env:ENTRA_API_CLIENT_ID="<tu-API_CLIENT_ID>"
```

Para verificar:
```powershell
echo $env:ENTRA_ISSUER_URI
echo $env:ENTRA_API_CLIENT_ID
```

## 🏃 Paso 4: Ejecutar el Proyecto

Necesitas **3 terminales abiertas**:

### Terminal 1: Microservicio Orders
```powershell
cd services\orders
mvn spring-boot:run
```
Espera a ver: `Started OrdersApplication in X seconds`

### Terminal 2: BFF
```powershell
cd services\bff

# Configurar variables de entorno
$env:ENTRA_ISSUER_URI="https://login.microsoftonline.com/<tu-TENANT_ID>/v2.0"
$env:ENTRA_API_CLIENT_ID="<tu-API_CLIENT_ID>"

# Ejecutar
mvn spring-boot:run
```
Espera a ver: `Started ApiApplication in X seconds`

### Terminal 3: Frontend React
```powershell
cd frontend
npm run dev
```
Espera a ver: `Local: http://localhost:5173/`

## ✅ Paso 5: Probar el Flujo Completo

1. Abre tu navegador en **http://localhost:5173**
2. Haz clic en **"Iniciar Sesión"** → Login con tu usuario de Azure AD
3. Haz clic en **"Obtener Token"** → Deberías ver un token JWT
4. Haz clic en **"Consultar API"** → Deberías ver:

```json
{
  "rut": "12345678-9",
  "nombre": "Wacoldo",
  "apellido": "Soto",
  "direccion": "Calle Falsa 123",
  "comuna": "Santiago"
}
```

## 🎯 Arquitectura del Flujo

```
Usuario → Frontend (5173)
    ↓ Login Azure AD
    ↓ Obtiene Token JWT
    ↓ GET /api/data + Bearer Token
    ↓
BFF (8080)
    ↓ Valida JWT con Azure AD
    ↓ GET http://localhost:8081/clientes
    ↓
Orders (8081)
    ↓ Retorna datos Wacoldo Soto
    ↓
BFF → Frontend → Usuario ve datos
```

## 🐛 Troubleshooting

### Error: "Invalid token" en BFF
- Verifica que `$env:ENTRA_ISSUER_URI` esté configurado correctamente
- Verifica que `$env:ENTRA_API_CLIENT_ID` coincida con el API_CLIENT_ID
- Revisa que el token tenga el scope `access_as_user`

### Error: "CORS" en Frontend
- Verifica que el BFF esté corriendo en puerto 8080
- Revisa `SecurityConfig.java` tenga `http://localhost:5173` permitido

### Error: "Connection refused" en BFF
- Verifica que el microservicio Orders esté corriendo en puerto 8081
- Revisa `application.yml` del BFF tenga `order-service.url: http://localhost:8081`

### Frontend no carga
- Verifica que `.env.local` esté en la carpeta `frontend/` (no en la raíz)
- Verifica que las variables empiecen con `VITE_`
- Reinicia el servidor de Vite después de crear `.env.local`

## 📂 Estructura del Proyecto

```
pedidos360/
├── frontend/                     # React + MSAL (puerto 5173)
│   ├── src/
│   │   ├── authConfig.ts        # Configuración MSAL
│   │   ├── token.ts             # Función obtenerToken()
│   │   ├── api.ts               # Función consultarApi()
│   │   └── App.tsx              # Componente principal
│   └── .env.local               # Variables de entorno (crear)
├── services/
│   ├── bff/                     # Backend for Frontend (puerto 8080)
│   │   └── src/main/
│   │       ├── java/cl/duoc/api/
│   │       │   ├── SecurityConfig.java
│   │       │   └── controller/DataController.java
│   │       └── resources/
│   │           └── application.yml
│   └── orders/                  # Microservicio Orders (puerto 8081)
│       └── src/main/
│           ├── java/cl/duoc/orders/
│           │   └── controller/ClienteController.java
│           └── resources/
│               └── application.yml
└── docs/
    ├── README.md
    ├── INSTRUCCIONES_SETUP.md   # Este archivo
    └── ESTADO_PROYECTO.md
```

## 🎓 Siguientes Pasos

Una vez que el flujo básico funcione:
1. Expandir microservicios (catalog, notify, reports, audit)
2. Agregar base de datos (PostgreSQL/MySQL)
3. Configurar mensajería (RabbitMQ, Kafka)
4. Dockerizar todo con Docker Compose
5. Desplegar en AWS con API Gateway

---

**¿Problemas?** Revisa los logs en cada terminal para identificar errores específicos.
