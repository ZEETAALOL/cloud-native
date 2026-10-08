# Pedidos360 - Sistema Cloud-Native de Gestión de Pedidos

Sistema de microservicios para gestión completa del ciclo de vida de pedidos, desarrollado con arquitectura cloud-native y desplegado en AWS EC2.

**Evaluación Final Transversal (EFT) - DSY1107**  
**Fecha:** Octubre 2026  
**Estudiante:** Bastián Martínez

## 🎯 Caso de Uso

Plataforma unificada para 20 PyMEs (panaderías/cafés) que permite:
- ✅ Tomar pedidos web y administrar stock
- ✅ Coordinar despachos
- ✅ Notificar a clientes (email/webpush)
- ✅ Generar reportes de ventas en tiempo real
- ✅ Auditar eventos de negocio

## 🏗️ Arquitectura

### Microservicios Implementados

El sistema está compuesto por **6 microservicios independientes**:

#### 1. **BFF (Backend For Frontend)** - Puerto 8080
- Punto de entrada único para el frontend
- Valida JWT de Azure AD
- Circuit Breaker (Resilience4j) para tolerancia a fallos
- Delega peticiones a microservicios internos

#### 2. **Orders** - Puerto 8081
- **Gestión completa de pedidos**
- CRUD de pedidos con validación de estados
- Estados: CREADO → ACEPTADO → EN_PREPARACION → DESPACHADO → ENTREGADO / CANCELADO
- Regla: No se puede "despachar" sin "aceptar"
- Filtros: por estado, fecha, cliente
- Estadísticas en tiempo real

#### 3. **Catalog** - Puerto 8084
- **Catálogo de productos**
- CRUD completo con validaciones
- Gestión de stock (INCREMENT/DECREMENT)
- Filtros por categoría y estado activo
- Integración con Orders para decrementar stock

#### 4. **Audit** - Puerto 8083
- **Timeline de eventos del sistema**
- Registro de auditoría (quién/qué/cuándo/desde dónde)
- Endpoints read-only
- Filtros avanzados: usuario, acción, entidad, rango de fechas
- Timeline completo de pedidos

#### 5. **Report** - Puerto 8085
- **Reportería y KPIs en tiempo real**
- Panel de KPIs: ventas por hora, lead time, estados activos
- Top productos más vendidos
- Ingresos y métricas de rendimiento
- Preparado para consumir eventos de Kafka

#### 6. **Notify** - Puerto 8086
- **Sistema de notificaciones**
- Email, WebPush
- Consumidor de RabbitMQ (preparado)
- Estadísticas de notificaciones enviadas

## 📊 Tecnologías

- **Backend:** Spring Boot 3.2.0 (Java 17)
- **Frontend:** React 18 + Vite
- **Autenticación:** OAuth2 con Azure AD (Microsoft Entra ID)
- **Mensajería:** RabbitMQ (preparado para colas cmd.email, cmd.kitchen, cmd.invoice)
- **Streaming:** Kafka + Zookeeper (próxima integración)
- **Bases de datos:** En memoria (listo para PostgreSQL/MongoDB)
- **Contenedores:** Docker + Docker Compose
- **Cloud:** AWS EC2
- **Resiliencia:** Resilience4j (Circuit Breaker, Retry)

## 🚀 Despliegue Actual

### Arquitectura AWS (2 instancias EC2)

#### **EC2-1 (ec2-apps)** - Microservicios + Bases de Datos
- **Tipo:** t2.medium (4 GB RAM)
- **IP Pública:** `54.221.95.141`
- **IP Privada:** `172.31.42.221`

**Servicios en ejecución:**
- ✅ `orders` (Puerto 8081) - Gestión de Pedidos - **HEALTHY**
- ✅ `catalog` (Puerto 8084) - Catálogo de Productos - **HEALTHY**
- ✅ `audit` (Puerto 8083) - Auditoría - **HEALTHY**
- ✅ `report` (Puerto 8085) - Reportes y KPIs - **HEALTHY**
- ✅ `postgres` (Puerto 5432) - Base de datos relacional - **HEALTHY**
- ✅ `mongodb` (Puerto 27017) - Base de datos documental - **HEALTHY**
- ⚠️ `bff` (Puerto 8080) - Backend For Frontend - **UNHEALTHY** (ver limitaciones)
- ⚠️ `notify` (Puerto 8086) - Notificaciones - **UNHEALTHY** (ver limitaciones)

#### **EC2-2 (ec2-mq)** - RabbitMQ
- **Tipo:** t2.micro (1 GB RAM)
- **IP Pública:** `34.239.139.42`
- **IP Privada:** `172.31.45.236`

**Servicios en ejecución:**
- ✅ `rabbitmq` (Puertos 5672 AMQP, 15672 Management) - **HEALTHY**

### URLs de Acceso

**Orders Service:**
```
http://54.221.95.141:8081
```

**Catalog Service:**
```
http://54.221.95.141:8084
```

**Audit Service:**
```
http://54.221.95.141:8083
```

**Report Service:**
```
http://54.221.95.141:8085
```

**RabbitMQ Management UI:**
```
http://34.239.139.42:15672
Usuario: admin
Password: admin123
```

**Health Checks (Servicios Funcionando):**
```bash
# Orders
curl http://54.221.95.141:8081/actuator/health

# Catalog
curl http://54.221.95.141:8084/actuator/health

# Audit
curl http://54.221.95.141:8083/actuator/health

# Report
curl http://54.221.95.141:8085/actuator/health
```

## 📋 Endpoints Principales

Ver documentación completa en **[ENDPOINTS.md](./ENDPOINTS.md)**

### Orders (Gestión de Pedidos)
```http
POST   /api/orders                 # Crear pedido
GET    /api/orders/{id}           # Obtener pedido
PUT    /api/orders/{id}/status    # Cambiar estado
GET    /api/orders?status=CREADO  # Filtrar pedidos
DELETE /api/orders/{id}           # Cancelar pedido
GET    /api/orders/stats          # Estadísticas
```

### Catalog (Productos)
```http
GET    /api/products              # Listar productos
POST   /api/products              # Crear producto
PUT    /api/products/{id}         # Actualizar producto
PATCH  /api/products/{id}/stock   # Actualizar stock
DELETE /api/products/{id}         # Eliminar producto
```

### Report (KPIs y Reportes)
```http
GET /api/report/kpis?range=last24h
GET /api/report/top-products?range=last7d
GET /api/report/lead-time
GET /api/report/active-orders
GET /api/report/revenue
```

### Audit (Timeline de Eventos)
```http
GET /api/audit                    # Todos los eventos
GET /api/audit/order/{orderId}   # Timeline de pedido
GET /api/audit/user/{userId}     # Por usuario
GET /api/audit/recent?limit=50   # Más recientes
```

## 🔐 Autenticación

### Azure AD (Microsoft Entra ID)

**Tenant ID:** `47c2bee0-5950-430f-9276-bfc083e3d1da`  
**Client ID:** `faba8741-ba0d-440c-b061-f1aa893eb957`  
**Application ID URI:** `api://faba8741-ba0d-440c-b061-f1aa893eb957`  
**Scope:** `access_as_user`

**Flujo:**
1. Frontend solicita token a Azure AD
2. Usuario hace login con Microsoft
3. Azure AD devuelve JWT
4. Frontend envía JWT en header `Authorization: Bearer <token>`
5. BFF valida JWT antes de procesar
6. BFF delega a microservicios con Circuit Breaker

## 🔄 Flujo de un Pedido Completo

```
1. Cliente crea pedido (CREADO)
   ↓
2. Operador acepta (ACEPTADO) → Stock se decrementa
   ↓
3. Cocina prepara (EN_PREPARACION)
   ↓
4. Despachado (DESPACHADO)
   ↓
5. Entregado (ENTREGADO)

En cualquier momento → CANCELADO
```

**Eventos generados:**
- OrderCreated
- OrderAccepted
- OrderPreparing
- OrderDispatched
- OrderDelivered
- OrderCancelled

**Notificaciones:**
- Email al cliente en cada cambio de estado
- Ticket a cocina al ACEPTAR
- Confirmación de entrega

## 🛠️ Desarrollo Local

### Requisitos
- Java 17+
- Maven 3.8+
- Node.js 20+
- Docker & Docker Compose

### Compilar Backend

```bash
# Compilar todos los microservicios
cd backend/orders && mvn clean package
cd backend/catalog && mvn clean package
cd backend/audit && mvn clean package
cd backend/report && mvn clean package
cd backend/notify && mvn clean package
cd backend/bff && mvn clean package
```

### Ejecutar Localmente

```bash
# Orders
java -jar backend/orders/target/ms-pedidos360-orders-1.0.0.jar

# Catalog
java -jar backend/catalog/target/ms-pedidos360-catalog-1.0.0.jar

# Audit
java -jar backend/audit/target/ms-pedidos360-audit-1.0.0.jar

# Report
java -jar backend/report/target/ms-pedidos360-report-1.0.0.jar

# Notify
java -jar backend/notify/target/ms-pedidos360-notify-1.0.0.jar

# BFF
java -jar backend/bff/target/ms-pedidos360-bff-1.0.0.jar
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## 🐳 Docker

### Construir Imágenes

```bash
# Orders
docker build -t pedidos360/orders:latest backend/orders

# Catalog
docker build -t pedidos360/catalog:latest backend/catalog

# Y así para cada microservicio...
```

### Docker Compose

```bash
# Levantar todos los servicios
cd infra/docker
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener
docker-compose down
```

## 📊 Monitoreo

### Actuator Endpoints

Todos los microservicios exponen:

```
/actuator/health       # Estado del servicio
/actuator/info         # Información del servicio
/actuator/metrics      # Métricas
```

### RabbitMQ Management

```
http://34.239.139.42:15672
Usuario: admin
Password: admin123
```

## ⚠️ Limitaciones Conocidas

### Integración RabbitMQ (BFF y Notify)

**Estado:** Los microservices BFF y Notify quedan en estado UNHEALTHY debido a un problema en la integración con RabbitMQ.

**Síntoma:**
```
java.net.ConnectException: Connection refused
at com.rabbitmq.client.impl.SocketFrameHandlerFactory.create
```

**Verificaciones realizadas:**
- ✅ Conectividad de red confirmada (telnet desde EC2-1 a EC2-2 puerto 5672 exitoso)
- ✅ RabbitMQ healthy en EC2-2 y logs muestran intentos de conexión desde EC2-1
- ✅ Security groups configurados correctamente (puerto 5672 abierto)
- ✅ Docker bridge networking configurado

**Configuraciones intentadas:**
1. Aumento de timeouts en docker-compose.yml:
   ```
   SPRING_RABBITMQ_CONNECTION_TIMEOUT=60000
   SPRING_RABBITMQ_REQUESTED_HEARTBEAT=60
   ```

2. Configuración de RabbitMQ en EC2-2:
   ```
   handshake_timeout = 60000
   channel_max = 2047
   heartbeat = 60
   ```

3. Upgrade de instancia EC2-1 a t2.medium (4 GB RAM)

**Diagnóstico:**
- El error ocurre durante el handshake del cliente RabbitMQ de Java/Spring
- No es un problema de OS-level networking sino de Docker bridge networking
- Los logs de RabbitMQ muestran: `{handshake_timeout,handshake}` al cerrar conexiones
- El cliente Spring AMQP no completa el handshake antes del timeout

**Workaround temporal:**
Los microservicios Orders, Catalog, Audit y Report funcionan correctamente de forma independiente sin necesidad de RabbitMQ. BFF y Notify pueden omitirse para testing de los otros servicios.

**Próximos pasos para resolución:**
1. Investigar configuración de Docker networking (host vs bridge)
2. Probar con variables de entorno adicionales de Spring AMQP
3. Considerar despliegue de RabbitMQ en la misma instancia EC2-1
4. Verificar versiones de compatibilidad entre Spring Boot 3.2.0 y RabbitMQ client

## ✅ Checklist de Funcionalidades Completadas

### ✨ Microservicios Core
- [x] **Orders** - CRUD completo + Estados + Validaciones
- [x] **Catalog** - CRUD + Gestión de stock
- [x] **Audit** - Timeline con filtros avanzados
- [x] **Report** - KPIs y métricas en tiempo real
- [x] **Notify** - Sistema de notificaciones
- [x] **BFF** - Delegación con Circuit Breaker

### 🔐 Seguridad e Identidad
- [x] Azure AD OAuth2 configurado
- [x] JWT validation en BFF
- [x] Application ID URI y scope personalizados
- [x] Frontend con MSAL

### 🏗️ Infraestructura
- [x] Dockerfiles para todos los servicios
- [x] Docker Compose configurado
- [x] Despliegue en AWS EC2
- [x] RabbitMQ levantado
- [x] Health checks

### 📊 Endpoints Según Caso PDF
- [x] POST /api/orders (crear pedido)
- [x] GET /api/orders/{id}
- [x] PUT /api/orders/{id}/status
- [x] GET /api/orders?status=...&from=...&to=...
- [x] GET /api/catalog/products
- [x] POST /api/catalog/products
- [x] PUT /api/catalog/products/{id}
- [x] GET /api/report/kpis?range=last24h
- [x] GET /api/report/top-products?range=last7d
- [x] GET /api/audit/* (read-only con filtros)

## 🔜 Próximas Integraciones

### Fase 2: Mensajería y Streaming
- [ ] Topología RabbitMQ completa (colas, exchanges, DLQs)
- [ ] Kafka + Zookeeper (3 brokers, 3 nodos ZK)
- [ ] Publicar eventos OrderCreated a Kafka
- [ ] Consumir eventos en Audit desde Kafka
- [ ] Consumir eventos en Report desde Kafka
- [ ] Enviar notificaciones a RabbitMQ
- [ ] Consumir notificaciones desde RabbitMQ en Notify

### Fase 3: Integración Avanzada
- [ ] Decrementar stock en Catalog al ACEPTAR pedido
- [ ] Restaurar stock al CANCELAR pedido
- [ ] Microservicio admin RabbitMQ
- [ ] Microservicio admin Kafka
- [ ] Kafka-UI para administración
- [ ] Clúster RabbitMQ (2 nodos)

### Fase 4: Cloud
- [ ] AWS API Gateway con JWT Authorizer
- [ ] PostgreSQL en RDS
- [ ] MongoDB Atlas
- [ ] ElastiCache para Redis
- [ ] CloudWatch para logs y métricas

## 📚 Documentación

- **[ENDPOINTS.md](./ENDPOINTS.md)** - Documentación completa de todos los endpoints
- **[backend/orders/README.md](./backend/orders/README.md)** - Microservicio Orders
- **[backend/bff/CIRCUIT_BREAKER.md](./backend/bff/CIRCUIT_BREAKER.md)** - Circuit Breaker

## 👨‍💻 Autor

**Bastián Martínez**  
Estudiante DuocUC  
Asignatura: DSY1107 - Desarrollo Cloud Native I  
Evaluación: EP1 (16% de la nota final)

## 📅 Última Actualización

**Fecha:** 2025-01-06  
**Versión:** 2.1.0  
**Estado:** ✅ 4 de 6 microservicios funcionales + Bases de datos + RabbitMQ desplegado

**Servicios Operacionales:**
- Orders, Catalog, Audit, Report (100% funcionales)
- PostgreSQL, MongoDB (100% funcionales)
- RabbitMQ desplegado (BFF/Notify con limitaciones de conexión)

---

## 🚀 Quick Start

```bash
# 1. Clonar repositorio
git clone <repo-url>
cd proyecto_cloudnative_ev1

# 2. Compilar backend
cd backend
mvn clean install

# 3. Levantar servicios
cd ../infra/docker
docker-compose up -d

# 4. Verificar health
curl http://localhost:8080/actuator/health

# 5. Crear primer pedido
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "12345",
    "customerName": "Juan Pérez",
    "customerEmail": "juan@example.com",
    "deliveryAddress": "Av. Providencia 123",
    "items": [{
      "productId": 1,
      "productName": "Café Latte",
      "quantity": 2,
      "price": 3500.0
    }]
  }'

# 6. Ver pedidos
curl http://localhost:8080/api/orders

# 7. Ver KPIs
curl http://localhost:8080/api/report/kpis?range=last24h
```

---

**¡Sistema listo para la siguiente evaluación!** 🎉
