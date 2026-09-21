# 🚀 Pedidos360 - Plataforma Cloud-Native

Sistema de microservicios enterprise para gestión de pedidos con arquitectura cloud-native completa.

**Stack:** Spring Boot • Keycloak • Traefik • RabbitMQ • Docker • PostgreSQL • MongoDB

---

## 📋 Características Principales

✅ **6 Microservicios** - BFF, Orders, Catalog, Report, Notify, Audit  
✅ **OAuth2/OIDC** - Autenticación centralizada con Keycloak  
✅ **API Gateway** - Traefik como punto de entrada único  
✅ **Mensajería Asíncrona** - RabbitMQ para comunicación entre servicios  
✅ **Circuit Breaker** - Resilience4j para tolerancia a fallos  
✅ **Contenedorización** - Docker Compose con 10+ servicios  
✅ **Bases de datos** - PostgreSQL + MongoDB  

---

## 🏗️ Arquitectura

```
Internet → Traefik (API Gateway :80)
              ↓
          Keycloak (:8080) ← OAuth2/OIDC
              ↓
            BFF (:8081) ← JWT Validation
              ↓
    ┌─────────┼─────────┬─────────┐
    ↓         ↓         ↓         ↓
 Orders   Catalog   Report    Audit
  :8082    :8084     :8085     :8083
    ↓         ↓         ↓         ↓
PostgreSQL MongoDB  PostgreSQL PostgreSQL
    
    BFF → RabbitMQ → Notify (:8086)
```

---

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Ports disponibles: 80, 5432, 5672, 8080-8086, 15672, 27017

### Levantar todo el sistema

```bash
cd infra/docker
docker compose up -d
```

### Verificar servicios

```bash
docker compose ps
docker compose logs -f
```

### Acceder a los servicios

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Traefik Dashboard** | http://localhost/dashboard/ | - |
| **Keycloak** | http://localhost:8080 | admin / admin123 |
| **RabbitMQ Admin** | http://localhost:15672 | admin / admin123 |
| **BFF API** | http://localhost/api | Requiere token |

---

## 🔐 Configuración Keycloak

### Crear Realm y Clients

1. Login en Keycloak (admin/admin123)
2. Crear realm: `pedidos360`
3. Crear client: `pedidos360-api`
4. Crear usuario de prueba

**Obtener token:**
```bash
curl -X POST http://localhost:8080/realms/pedidos360/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=pedidos360-api" \
  -d "username=testuser" \
  -d "password=test123"
```

**Llamar API:**
```bash
curl http://localhost/api/products \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 📦 Microservicios

### BFF (Backend for Frontend)
- **Puerto:** 8081
- **Función:** API Gateway interno, orquestación, Circuit Breaker
- **Tech:** Spring Boot 4.1.1, Resilience4j

### Orders (Gestión de Pedidos)
- **Puerto:** 8082
- **Función:** CRUD pedidos, estados, validaciones
- **BD:** PostgreSQL

### Catalog (Productos)
- **Puerto:** 8084
- **Función:** CRUD productos, stock
- **BD:** MongoDB

### Report (Reportes)
- **Puerto:** 8085
- **Función:** KPIs, analytics, reportes
- **BD:** PostgreSQL

### Notify (Notificaciones)
- **Puerto:** 8086
- **Función:** Consumer RabbitMQ, envío de notificaciones
- **Mensajería:** RabbitMQ

### Audit (Auditoría)
- **Puerto:** 8083
- **Función:** Log de eventos de negocio
- **BD:** PostgreSQL

---

## 🛠️ Stack Tecnológico

| Componente | Tecnología | Versión |
|------------|-----------|---------|
| **Backend** | Spring Boot | 4.1.1 / 3.4.1 |
| **Lenguaje** | Java | 17 |
| **Build** | Maven | 3.9 |
| **Auth** | Keycloak | 26.0 |
| **Gateway** | Traefik | v3.2 |
| **Messaging** | RabbitMQ | 4.0-management |
| **BD SQL** | PostgreSQL | 17-alpine |
| **BD NoSQL** | MongoDB | 8.0 |
| **Circuit Breaker** | Resilience4j | 2.2.0 |
| **Containers** | Docker Compose | v2 |

---

## 📊 Patrones Cloud-Native Implementados

✅ **API Gateway Pattern** - Traefik como punto de entrada  
✅ **Circuit Breaker Pattern** - Resilience4j con fallbacks  
✅ **Service Discovery** - Docker DNS interno  
✅ **Externalized Configuration** - Variables de entorno  
✅ **Health Check Pattern** - Actuator endpoints  
✅ **Async Messaging** - RabbitMQ pub/sub  
✅ **Database per Service** - Cada microservicio su BD  
✅ **Centralized Authentication** - Keycloak OAuth2  

---

## 📚 Documentación Adicional

- **[infra/docker/README.md](infra/docker/README.md)** - Docker Compose setup
- **[infra/aws/INSTRUCCIONES_AWS.md](infra/aws/INSTRUCCIONES_AWS.md)** - Deploy a AWS
- **[backend/bff/CIRCUIT_BREAKER.md](backend/bff/CIRCUIT_BREAKER.md)** - Circuit Breaker guide
- **[docs/](docs/)** - Documentación técnica completa

---

## 🧪 Testing

### Health checks
```bash
curl http://localhost/actuator/health
```

### Probar Circuit Breaker
```bash
# Detener servicio
docker stop pedidos360-catalog

# Llamar API (debe retornar fallback)
curl http://localhost/api/products -H "Authorization: Bearer <TOKEN>"

# Verificar logs
docker logs pedidos360-bff --tail 50
```

### Probar RabbitMQ
```bash
# Enviar mensaje
curl -X POST http://localhost/api/messaging/send \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"message": "Test notification"}'

# Ver logs del consumer
docker logs pedidos360-notify --tail 50
```

---

## 🚀 Deploy a AWS

Ver guía completa: [infra/aws/INSTRUCCIONES_AWS.md](infra/aws/INSTRUCCIONES_AWS.md)

**Quick setup:**
```bash
# En EC2
wget https://raw.githubusercontent.com/TU-USUARIO/proyecto_cloudnative_ev1/main/infra/aws/setup-ec2.sh
chmod +x setup-ec2.sh
./setup-ec2.sh
```

---

## 🐛 Troubleshooting

### Servicios no levantan
```bash
docker compose logs <servicio>
docker compose restart <servicio>
```

### Puerto ocupado
```bash
netstat -ano | findstr :<PUERTO>
```

### Limpiar y reiniciar
```bash
docker compose down -v
docker compose up -d
```

---

## 📄 Licencia

Proyecto académico - DuocUC 2026  
Desarrollo Cloud Native I

**Autor:** Bastian Martinez  
**Fecha:** Septiembre 2026
