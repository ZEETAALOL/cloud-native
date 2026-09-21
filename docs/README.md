# Pedidos360 - Plataforma Cloud-Native para Gestión de Pedidos

Proyecto basado en arquitectura de microservicios para gestión de pedidos y despacho de PyMEs.

## 📋 Descripción

Plataforma unificada para 20 PyMEs (panaderías/cafés) que permite:
- Tomar pedidos web, administrar stock básico y coordinar despachos
- Notificar a clientes (email/webpush) y a la cocina (tickets)
- Generar reportes de ventas en tiempo real
- Auditar eventos de negocio

## 🏗️ Arquitectura

### Frontend
- **React 18+ con TypeScript** y MSAL para autenticación
- Integración con Azure AD (Entra ID)

### Backend
- **Spring Boot 3+** con Java 21
- Microservicios independientes
- AWS API Gateway con JWT Authorizer
- Spring Security con OAuth2 Resource Server

### Microservicios
- `bff`: Backend for Frontend (puerto 8080)
- `orders`: Gestión de pedidos (puerto 8081)
- `catalog`: Catálogo de productos (puerto 8082)
- `notify`: Servicio de notificaciones (puerto 8083)
- `reports`: Reportería y KPIs (puerto 8084)
- `audit`: Auditoría de eventos (puerto 8085)

### Mensajería
- **RabbitMQ**: Para comandos/tareas (notificaciones)
- **Kafka + Zookeeper**: Para eventos (analítica/auditoría en tiempo real)

### Despliegue
- AWS EC2 con Docker/Docker-Compose
- AWS API Gateway HTTP API
- Azure AD (Entra External ID)

## 📁 Estructura del Proyecto

```
pedidos360/
├── frontend/                   # React + MSAL + TypeScript
├── services/
│   ├── bff/                   # Backend for Frontend (Spring Boot)
│   ├── orders/                # Microservicio de Pedidos
│   ├── catalog/               # Microservicio de Catálogo
│   ├── notify/                # Microservicio de Notificaciones
│   ├── reports/               # Microservicio de Reportes
│   └── audit/                 # Microservicio de Auditoría
├── infra/                     # Docker Compose, Kafka, RabbitMQ
└── docs/                      # Documentación
```

## 🚀 Configuración Inicial

### Prerrequisitos
- Node.js 22.12+ y npm
- Java 25 (JDK 25)
- Maven
- Docker y Docker Compose
- Cuenta de Azure AD (Entra External ID)
- Cuenta de AWS

### Variables de Entorno Necesarias

#### Azure AD
- `TENANT_ID`: ID del directorio de Azure
- `API_CLIENT_ID`: ID de aplicación de api-fullstack
- `SPA_CLIENT_ID`: ID de aplicación de spa-fullstack

#### Aplicaciones en Azure AD
1. **api-fullstack**: Registro de API con scope `access_as_user`
2. **spa-fullstack**: Registro de SPA con permisos delegados

## 📦 Instalación y Ejecución Local

### 1. Frontend (React + MSAL)
```powershell
cd frontend
npm install
npm run dev
```

### 2. BFF (Spring Boot)
```powershell
cd services\bff
$env:ENTRA_ISSUER_URI="https://login.microsoftonline.com/<TENANT_ID>/v2.0"
$env:ENTRA_API_CLIENT_ID="<API_CLIENT_ID>"
.\mvnw.cmd spring-boot:run
```

### 3. Microservicios
Cada microservicio se ejecuta de forma similar al BFF, en su propio puerto.

### 4. RabbitMQ (Docker)
```powershell
cd infra/mq
docker-compose up -d
```

### 5. Kafka + Zookeeper (Docker)
```powershell
cd infra/kafka
docker-compose up -d
```

## 🔐 Seguridad e Identidad

### Azure AD (Entra External ID)
- App Registration "Pedidos360"
- Authority: `https://login.microsoftonline.com/<TENANT_ID>/`
- Scope: `api://<API_CLIENT_ID>/access_as_user`

### AWS API Gateway
- JWT Authorizer con:
  - Issuer: `https://login.microsoftonline.com/<TENANT_ID>/v2.0`
  - Audiences: `api://<API_CLIENT_ID>`

### Spring Security
- Validación JWT con `oauth2.resourceserver.jwt.issuer-uri`
- Autorización basada en roles del token

## 🎯 Módulos Funcionales

| Módulo | Descripción | Actores | Reglas Clave |
|--------|-------------|---------|--------------|
| **Gestión de Pedidos** | CRUD de pedidos, cambio de estado | Cliente, Operador | Estados: CREADO → ACEPTADO → EN_PREPARACIÓN → DESPACHADO → ENTREGADO/CANCELADO |
| **Catálogo** | CRUD de productos y stock | Admin | Stock decrece al aceptar pedido |
| **Notificaciones** | Email/push a cliente | Operador, Cliente | Envío asíncrono (RabbitMQ) |
| **Reportería** | Panel de KPIs en tiempo real | Admin | Datos por streaming (Kafka) |
| **Auditoría** | Timeline de eventos de negocio | Auditor | Solo lectura |

## 🔄 Mensajería

### RabbitMQ - Topología
**Colas principales (con DLQ)**:
- `q.cmd.email` → `q.cmd.email.dlq`: Envío de correos/push
- `q.cmd.kitchen` → `q.cmd.kitchen.dlq`: Tickets de cocina
- `q.cmd.invoice` → `q.cmd.invoice.dlq`: Generación de factura

**Exchanges**:
- `cmd.direct`: Enrutamiento exacto
- `cmd.topic`: Enrutamiento por patrones
- `cmd.dead.dlx`: DLQ exchange

### Kafka - Topología
| Tópico | Particiones | Retención | Propósito |
|--------|-------------|-----------|-----------|
| `orders.events` | 3 | 3-7 días | Eventos de negocio |
| `audit.timeline` | 3 | 14-30 días | Auditoría/Timeline |
| `*.DLT` | 3 | 7-14 días | Dead Letter Topics |

## 🧪 Testing

### Pruebas Parte 1 (Local)
1. **Sin token**: `curl.exe -i http://localhost:8080/api/data` → Debe retornar **401**
2. **Con token válido**: Usar token de MSAL → Debe retornar **200**

### Pruebas Parte 2 (BFF + Microservicios)
1. Verificar comunicación BFF → ms-clientes
2. Validar respuesta combinada con datos de Wacoldo Soto

## 📚 Documentación de Referencia

- [Tutorial Parte 1 - Autenticación y Backend Local](docs/Tutorial-Entra-ID-React-SpringBoot-Parte1.pdf)
- [Tutorial Parte 2 - BFF y Microservicios en Local](docs/Tutorial-Parte-2-BFF-Microservicios-Local.pdf)
- [Caso EFT - Instrucciones](docs/EFT-Caso-Instrucciones.pdf)

## 👥 Roles y Permisos

- **Admin**: Acceso completo a catálogo, reportes y auditoría
- **Operador**: Gestión de pedidos y notificaciones
- **Cliente**: Creación y consulta de propios pedidos

## 🛠️ Tecnologías

- Frontend: Angular 18+, MSAL Angular, TypeScript
- Backend: Spring Boot 3+, Java 21, Maven
- Seguridad: Azure AD, OAuth 2.0, OpenID Connect, JWT
- API Gateway: AWS API Gateway HTTP API
- Mensajería: RabbitMQ, Kafka, Zookeeper
- Base de Datos: Oracle
- Contenedores: Docker, Docker Compose
- Infraestructura: AWS EC2

## 📝 Notas de Implementación

- Se sigue el patrón Controller-Service-Repository (CSR)
- BFF orquesta llamadas a microservicios sin exponerlos directamente
- React no conoce las direcciones de los microservicios
- Todos los microservicios validan JWT antes de procesar solicitudes
- Mensajería asíncrona para desacoplar componentes

## 🚧 Estado del Proyecto

- [x] Parte 1: Autenticación local completada
- [ ] Parte 2: BFF y microservicios en local
- [ ] Parte 3: Despliegue en AWS con API Gateway

---

**Autor**: Proyecto académico DSY1107 - Desarrollo Cloud Native I  
**Fecha**: Septiembre 2026
