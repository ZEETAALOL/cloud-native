# 🚀 Pedidos360 - Sistema Cloud-Native de Gestión de Pedidos

Plataforma de microservicios enterprise desarrollada con arquitectura cloud-native completa, desplegada en AWS EC2.

**Stack:** Spring Boot • Azure AD • RabbitMQ • Docker • PostgreSQL • MongoDB • React

---

## 📋 Características Principales

✅ **6 Microservicios** - BFF, Orders, Catalog, Report, Notify, Audit  
✅ **OAuth2 con Azure AD** - Autenticación Microsoft (Entra ID)  
✅ **Frontend React** - Interfaz moderna con tema gamer dark purple  
✅ **Mensajería Asíncrona** - RabbitMQ para comunicación entre servicios  
✅ **Circuit Breaker** - Resilience4j para tolerancia a fallos  
✅ **Contenedorización** - Docker Compose con todos los servicios  
✅ **Bases de datos** - PostgreSQL + MongoDB  
✅ **Despliegue Cloud** - AWS EC2 t3.medium

---

## 🏗️ Arquitectura del Sistema

```
Usuario → Frontend React (localhost:5173)
              ↓ OAuth2
         Azure AD (Microsoft Entra ID)
              ↓ JWT Token
            BFF (:8081) ← JWT Validation + Circuit Breaker
              ↓
    ┌─────────┼─────────┬─────────┬─────────┐
    ↓         ↓         ↓         ↓         ↓
 Orders   Catalog   Report    Audit     Notify
  :8082    :8084     :8085     :8083      :8086
    ↓         ↓         ↓         ↓         ↓
PostgreSQL MongoDB PostgreSQL PostgreSQL  RabbitMQ
    
    BFF → RabbitMQ → Notify (Email/Notificaciones)
```

**Ubicación:**
- Backend: AWS EC2 (54.242.196.191)
- Frontend: Local (localhost:5173)
- Autenticación: Azure AD (cloud)

---

## 🚀 Quick Start

### Opción 1: Sistema Completo en AWS (Producción)

El backend ya está desplegado y funcionando en AWS EC2.

**URLs de acceso:**
```
Backend API (BFF):        http://54.242.196.191:8081
RabbitMQ Management:      http://54.242.196.191:15672 (admin/admin123)
Orders Service:           http://54.242.196.191:8082
Catalog Service:          http://54.242.196.191:8084
Report Service:           http://54.242.196.191:8085
Audit Service:            http://54.242.196.191:8083
Notify Service:           http://54.242.196.191:8086
```

**Frontend local:**
```bash
cd frontend
npm install
npm run dev
# Abre http://localhost:5173
```

### Opción 2: Local Development

```bash
cd infra/docker
docker compose -f docker-compose.full.yml up -d
```

---

## 🔐 Autenticación OAuth2 con Azure AD

El sistema utiliza **Microsoft Entra ID (Azure AD)** para autenticación OAuth2.

### Configuración Azure AD

**Tenant ID:** `47c2bee0-5950-430f-9276-bfc083e3d1da`  
**Client ID:** `faba8741-ba0d-440c-b061-f1aa893eb957`  
**Redirect URI:** `http://localhost:5173` (Frontend React)

### Flujo de Login

1. Usuario accede a `http://localhost:5173`
2. Click en "INICIAR SESIÓN CON MICROSOFT"
3. Redirige a Microsoft para autenticación
4. Usuario ingresa credenciales de Microsoft
5. Microsoft valida y redirige de vuelta con token
6. MSAL procesa el token
7. Usuario accede al Dashboard

### Acceder a la API

El frontend ya maneja automáticamente los tokens. Para llamadas manuales:

```bash
# Los tokens se obtienen automáticamente via MSAL en el frontend
# Para testing directo del backend sin frontend, necesitarías obtener
# un token válido de Azure AD manualmente
```

---

## 📦 Microservicios

### BFF (Backend for Frontend)
- **Puerto:** 8081
- **Función:** API Gateway, validación JWT, orquestación, Circuit Breaker
- **Tech:** Spring Boot 4.1.1, Resilience4j, OAuth2 Resource Server
- **Endpoints:** `/api/products`, `/api/audit`, `/api/notify`, `/api/data/clientes`

### Orders (Gestión de Pedidos y Clientes)
- **Puerto:** 8082
- **Función:** Gestión de pedidos y clientes
- **BD:** PostgreSQL
- **Endpoints:** `/clientes`, `/pedidos`

### Catalog (Catálogo de Productos)
- **Puerto:** 8084
- **Función:** CRUD de productos, gestión de inventario
- **BD:** MongoDB
- **Endpoints:** `/products`

### Report (Reportes y Analytics)
- **Puerto:** 8085
- **Función:** Generación de reportes, KPIs, datos analíticos
- **BD:** PostgreSQL
- **Endpoints:** `/reports/data`

### Notify (Notificaciones)
- **Puerto:** 8086
- **Función:** Consumer RabbitMQ, envío de emails y notificaciones
- **Mensajería:** RabbitMQ
- **Protocolo:** AMQP

### Audit (Auditoría)
- **Puerto:** 8083
- **Función:** Registro y consulta de eventos de auditoría del sistema
- **BD:** PostgreSQL
- **Endpoints:** `/audit`

---

## 🛠️ Stack Tecnológico

### Backend
| Componente | Tecnología | Versión |
|------------|-----------|---------|
| **Framework** | Spring Boot | 4.1.1 / 3.4.1 |
| **Lenguaje** | Java | 17 / 25 |
| **Build Tool** | Maven | 3.9 |
| **Auth** | Azure AD (Entra ID) | OAuth2/OIDC |
| **Circuit Breaker** | Resilience4j | 2.2.0 |
| **Messaging** | RabbitMQ | 3.13-management |
| **BD SQL** | PostgreSQL | 15 |
| **BD NoSQL** | MongoDB | 7 |
| **Containers** | Docker | Latest |

### Frontend
| Componente | Tecnología | Versión |
|------------|-----------|---------|
| **Framework** | React | 18 |
| **Build Tool** | Vite | 6 |
| **Auth Library** | MSAL | 3.x |
| **HTTP Client** | Axios | 1.7 |
| **Router** | React Router DOM | 7 |
| **Styling** | Bootstrap 5 + Custom CSS | 5.3 |
| **Fonts** | Orbitron + Rajdhani | Google Fonts |

### Infraestructura
| Componente | Tecnología | Detalles |
|------------|-----------|----------|
| **Cloud Provider** | AWS EC2 | t3.medium (2 vCPU, 4GB RAM) |
| **OS** | Ubuntu Server | 24.04 LTS |
| **Orchestration** | Docker Compose | v2 |
| **Networking** | AWS Security Groups | Puertos 22, 8081-8086, 15672 |

---

## 📊 Patrones Cloud-Native Implementados

✅ **BFF Pattern** - Backend for Frontend como capa de agregación  
✅ **Circuit Breaker Pattern** - Resilience4j con fallbacks automáticos  
✅ **Event-Driven Architecture** - RabbitMQ para mensajería asíncrona  
✅ **Database per Service** - Cada microservicio con su base de datos independiente  
✅ **Externalized Configuration** - Variables de entorno y archivos YAML  
✅ **Health Check Pattern** - Endpoints de salud en todos los servicios  
✅ **OAuth2/OIDC Authentication** - Azure AD para autenticación centralizada  
✅ **API Composition** - BFF agrega respuestas de múltiples servicios  
✅ **CORS Management** - Configuración centralizada en BFF  

---

## 📚 Documentación

- **[INFORME_ESTADO_PROYECTO.md](INFORME_ESTADO_PROYECTO.md)** - Estado completo del proyecto
- **[backend/bff/CIRCUIT_BREAKER.md](backend/bff/CIRCUIT_BREAKER.md)** - Guía de Circuit Breaker
- **[backend/bff/README.md](backend/bff/README.md)** - Documentación del BFF
- **[frontend/README.md](frontend/README.md)** - Setup del frontend React

---

## 🧪 Testing y Demostración

### Frontend - Flujo Completo

1. Acceder a `http://localhost:5173`
2. Login con Microsoft (Azure AD)
3. Ver Dashboard con métricas
4. Crear un producto en Catálogo
5. Ver eventos en Auditoría
6. Enviar notificación por email
7. Logout

### Backend - Health Checks

```bash
# Verificar todos los servicios
curl http://54.242.196.191:8081/actuator/health
curl http://54.242.196.191:8082/actuator/health
curl http://54.242.196.191:8083/actuator/health
curl http://54.242.196.191:8084/actuator/health
curl http://54.242.196.191:8085/actuator/health
curl http://54.242.196.191:8086/actuator/health
```

### Circuit Breaker - Prueba de Tolerancia a Fallos

```bash
# 1. Detener servicio de Catalog
ssh ubuntu@54.242.196.191
cd ~/cloud-native/infra/docker
sudo docker-compose -f docker-compose.full.yml stop catalog

# 2. Llamar API desde frontend
# La app debe mostrar mensaje de fallback sin fallar

# 3. Ver logs del Circuit Breaker
sudo docker-compose -f docker-compose.full.yml logs bff | grep -i circuit

# 4. Reiniciar servicio
sudo docker-compose -f docker-compose.full.yml start catalog
```

### RabbitMQ - Mensajería Asíncrona

```bash
# 1. Acceder a RabbitMQ Management
# http://54.242.196.191:15672 (admin/admin123)

# 2. Enviar notificación desde frontend
# Ver en la cola que el mensaje se procesa

# 3. Ver logs del servicio Notify
sudo docker-compose -f docker-compose.full.yml logs notify --tail=50
```

---

## 🚀 Despliegue en AWS

### Infraestructura Actual

**Instancia EC2:**
- Tipo: t3.medium (2 vCPU, 4GB RAM)
- OS: Ubuntu Server 24.04 LTS
- IP Pública: 54.242.196.191 ⚠️ *Cambia al reiniciar*
- Región: us-east-1

**Security Group:**
- SSH: 22
- Backend Services: 8081-8086
- RabbitMQ Management: 15672

### Comandos Útiles AWS

```bash
# Conectar por SSH
ssh -i tu-clave.pem ubuntu@54.242.196.191

# Ver estado de servicios
cd ~/cloud-native/infra/docker
sudo docker-compose -f docker-compose.full.yml ps

# Ver logs
sudo docker-compose -f docker-compose.full.yml logs -f [servicio]

# Reiniciar servicios
sudo docker-compose -f docker-compose.full.yml restart

# Reiniciar un servicio específico
sudo docker-compose -f docker-compose.full.yml restart bff
```

### Actualizar Código en AWS

```bash
# En AWS EC2
cd ~/cloud-native
git pull origin main
sudo docker-compose -f infra/docker/docker-compose.full.yml up -d --build
```

---

## 🐛 Troubleshooting

### Frontend no conecta al backend

```bash
# Verificar IP del backend en frontend/src/config.js
# Debe ser: http://54.242.196.191:8081
```

### Error de CORS

```bash
# Verificar configuración CORS en BFF
# El BFF debe permitir origen: http://localhost:5173
```

### Login de Azure AD falla

```bash
# Verificar Redirect URI en Azure Portal
# Debe estar configurado: http://localhost:5173
# Tipo: Single-page application (SPA)
# Tokens: Access tokens + ID tokens habilitados
```

### Servicios en AWS no responden

```bash
# Conectar por SSH
ssh -i tu-clave.pem ubuntu@54.242.196.191

# Ver estado
cd ~/cloud-native/infra/docker
sudo docker-compose -f docker-compose.full.yml ps

# Ver logs
sudo docker-compose -f docker-compose.full.yml logs [servicio] --tail=100

# Reiniciar todo
sudo docker-compose -f docker-compose.full.yml restart
```

### Puerto ocupado localmente

```powershell
# Ver qué proceso usa el puerto
netstat -ano | findstr :5173

# Matar proceso
taskkill /PID <PID> /F
```

---

## 📊 Estructura del Proyecto

```
proyecto_cloudnative_ev1/
├── backend/                    # Microservicios Java Spring Boot
│   ├── audit/                 # ✅ Servicio de auditoría
│   ├── bff/                   # ✅ Backend for Frontend
│   ├── catalog/               # ✅ Catálogo de productos
│   ├── notify/                # ✅ Notificaciones
│   ├── orders/                # ✅ Pedidos y clientes
│   └── report/                # ✅ Reportes
├── frontend/                   # ✅ React + Vite + OAuth2
│   ├── src/
│   │   ├── components/        # Navbar
│   │   ├── pages/             # Login, Dashboard, Products, etc.
│   │   ├── services/          # API client (Axios)
│   │   ├── authConfig.js      # MSAL config
│   │   └── config.js          # Backend URL
│   └── package.json
├── infra/
│   ├── aws/                   # Scripts y docs de AWS
│   └── docker/                # Docker Compose
│       └── docker-compose.full.yml  # ✅ En uso en AWS
├── INFORME_ESTADO_PROYECTO.md # 📄 Estado completo
└── README.md                  # 📄 Este archivo
```

---

## 📄 Licencia y Créditos

**Proyecto Académico** - DuocUC 2026  
**Asignatura:** Desarrollo Cloud Native I  
**Autor:** Benjamín Martínez  
**Email:** bae.martinez@duocuc.cl  
**Fecha:** Septiembre 2026

### Características Destacadas

- ✅ **100% Funcional** - Sistema completo desplegado y operacional
- ✅ **OAuth2 Real** - Autenticación con Azure AD (no mock)
- ✅ **Cloud Deployment** - AWS EC2 en producción
- ✅ **Patrones Enterprise** - Circuit Breaker, BFF, Event-Driven
- ✅ **Frontend Moderno** - React 18 con tema gamer profesional
- ✅ **Arquitectura Escalable** - 6 microservicios independientes

### Repositorio

**GitHub:** https://github.com/ZEETAALOL/cloud-native

---

**⚠️ Nota Importante:** La IP pública de AWS (54.242.196.191) cambia al reiniciar la instancia EC2. Actualizar `frontend/src/config.js` con la nueva IP cuando sea necesario.
