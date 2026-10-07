# Backend - Microservicios Pedidos360

Todos los microservicios del backend en Spring Boot.

## 📁 Estructura

```
backend/
├── bff/         ✅ Backend for Frontend (API Gateway interno)
├── orders/      ✅ Gestión de pedidos
├── catalog/     🔴 Catálogo de productos y stock (pendiente)
├── notify/      🔴 Notificaciones email/push (pendiente)
├── report/      🔴 Reportería y KPIs (pendiente)
└── audit/       🔴 Auditoría y trazabilidad (pendiente)
```

## 🛠️ Stack Común

- **Java:** 17
- **Framework:** Spring Boot 3.x
- **Build:** Maven
- **Seguridad:** Spring Security + OAuth2 Resource Server
- **Base de Datos:** Oracle Cloud (JPA/Hibernate)

## 🚀 Ejecutar Microservicios

### BFF (Puerto 8080)
```powershell
cd backend/bff
$env:ENTRA_ISSUER_URI="https://login.microsoftonline.com/47c2bee0-5950-430f-9276-bfc083e3d1da/v2.0"
$env:ENTRA_API_CLIENT_ID="faba8741-ba0d-440c-b061-f1aa893eb957"
mvn spring-boot:run
```

### Orders (Puerto 8081)
```powershell
cd backend/orders
mvn spring-boot:run
```

### Catalog (Puerto 8082) - Pendiente
```powershell
cd backend/catalog
mvn spring-boot:run
```

## 📋 Estado de Microservicios

| Servicio | Puerto | Estado | Descripción |
|----------|--------|--------|-------------|
| **bff** | 8080 | ✅ Funcional | API Gateway, valida JWT |
| **orders** | 8081 | ✅ Funcional | CRUD pedidos, estados |
| **catalog** | 8082 | 🔴 Pendiente | CRUD productos/stock |
| **notify** | - | 🔴 Pendiente | Consume RabbitMQ |
| **report** | 8083 | 🔴 Pendiente | KPIs, consume Kafka |
| **audit** | 8084 | 🔴 Pendiente | Timeline, consume Kafka |

## 🔗 Comunicación

```
Frontend (React)
    ↓ JWT Bearer Token
BFF (:8080)
    ↓ Orquesta llamadas
Orders (:8081)  Catalog (:8082)  ...
    ↓
Base de Datos Oracle
```

## 📦 Dependencias Compartidas

Todos los microservicios usan:
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-resource-server`

## 🐳 Docker

Ver `infra/docker/` para docker-compose de todos los servicios.
