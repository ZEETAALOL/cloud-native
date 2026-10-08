# Estado Actual del Proyecto Pedidos360

**Fecha:** 2025-01-06  
**Evaluación:** DSY1107 - Desarrollo Cloud Native I  
**Estudiante:** Bastián Martínez

---

## 📊 Resumen Ejecutivo

El proyecto Pedidos360 tiene desplegados **2 instancias EC2** en AWS con **6 microservicios + 2 bases de datos + RabbitMQ**. 

**Estado general:** 
- ✅ **66% operacional** (4 de 6 microservicios funcionales)
- ⚠️ **34% con limitaciones** (2 microservicios con problema de integración RabbitMQ)

---

## 🏗️ Arquitectura Desplegada

### EC2-1: Aplicaciones (ec2-apps)
- **Tipo:** t2.medium (2 vCPUs, 4 GB RAM)
- **IP Pública:** `54.221.95.141`
- **IP Privada:** `172.31.42.221`
- **Región:** us-east-1

#### Servicios Corriendo:
| Servicio | Puerto | Estado | Funcionalidad |
|----------|--------|--------|---------------|
| **Orders** | 8081 | ✅ HEALTHY | Gestión completa de pedidos |
| **Catalog** | 8084 | ✅ HEALTHY | Catálogo de productos + stock |
| **Audit** | 8083 | ✅ HEALTHY | Timeline de eventos |
| **Report** | 8085 | ✅ HEALTHY | KPIs y reportes en tiempo real |
| **PostgreSQL** | 5432 | ✅ HEALTHY | Base de datos relacional |
| **MongoDB** | 27017 | ✅ HEALTHY | Base de datos documental |
| **BFF** | 8080 | ⚠️ UNHEALTHY | Backend For Frontend (problema RabbitMQ) |
| **Notify** | 8086 | ⚠️ UNHEALTHY | Sistema de notificaciones (problema RabbitMQ) |

### EC2-2: Mensajería (ec2-mq)
- **Tipo:** t2.micro (1 vCPU, 1 GB RAM)
- **IP Pública:** `34.239.139.42`
- **IP Privada:** `172.31.45.236`
- **Región:** us-east-1

#### Servicios Corriendo:
| Servicio | Puertos | Estado | Funcionalidad |
|----------|---------|--------|---------------|
| **RabbitMQ** | 5672 (AMQP)<br>15672 (Management) | ✅ HEALTHY | Message broker |

**Credenciales RabbitMQ:**
- Usuario: `admin`
- Password: `admin123`
- Management UI: http://34.239.139.42:15672

---

## ✅ Lo que FUNCIONA

### 1. Microservicio Orders (Puerto 8081)
```bash
curl http://54.221.95.141:8081/actuator/health
# Response: {"status":"UP"}
```

**Endpoints disponibles:**
- `POST /api/orders` - Crear pedido
- `GET /api/orders/{id}` - Obtener pedido
- `PUT /api/orders/{id}/status` - Cambiar estado
- `GET /api/orders` - Listar con filtros
- `DELETE /api/orders/{id}` - Cancelar pedido
- `GET /api/orders/stats` - Estadísticas

**Estados soportados:**
`CREADO` → `ACEPTADO` → `EN_PREPARACION` → `DESPACHADO` → `ENTREGADO` / `CANCELADO`

### 2. Microservicio Catalog (Puerto 8084)
```bash
curl http://54.221.95.141:8084/actuator/health
# Response: {"status":"UP"}
```

**Endpoints disponibles:**
- `GET /api/products` - Listar productos
- `POST /api/products` - Crear producto
- `PUT /api/products/{id}` - Actualizar producto
- `PATCH /api/products/{id}/stock` - Gestionar stock
- `DELETE /api/products/{id}` - Eliminar producto

### 3. Microservicio Audit (Puerto 8083)
```bash
curl http://54.221.95.141:8083/actuator/health
# Response: {"status":"UP"}
```

**Endpoints disponibles:**
- `GET /api/audit` - Todos los eventos
- `GET /api/audit/order/{orderId}` - Timeline de pedido
- `GET /api/audit/user/{userId}` - Eventos por usuario
- `GET /api/audit/recent?limit=50` - Eventos recientes

### 4. Microservicio Report (Puerto 8085)
```bash
curl http://54.221.95.141:8085/actuator/health
# Response: {"status":"UP"}
```

**Endpoints disponibles:**
- `GET /api/report/kpis?range=last24h` - KPIs generales
- `GET /api/report/top-products?range=last7d` - Top productos
- `GET /api/report/lead-time` - Tiempo promedio de entrega
- `GET /api/report/active-orders` - Pedidos activos
- `GET /api/report/revenue` - Ingresos

### 5. Bases de Datos
- **PostgreSQL** (puerto 5432) - ✅ Operacional
- **MongoDB** (puerto 27017) - ✅ Operacional

### 6. RabbitMQ
- **AMQP** (puerto 5672) - ✅ Escuchando
- **Management UI** (puerto 15672) - ✅ Accesible
- Estado del servicio: ✅ HEALTHY

---

## ⚠️ Lo que NO FUNCIONA

### Problema: BFF y Notify no pueden conectar a RabbitMQ

**Microservicios afectados:**
- **BFF** (puerto 8080) - Estado: UNHEALTHY
- **Notify** (puerto 8086) - Estado: UNHEALTHY

**Síntoma:**
```
java.net.ConnectException: Connection refused
at com.rabbitmq.client.impl.SocketFrameHandlerFactory.create
```

**Causa identificada:**
- RabbitMQ está en EC2-2 (instancia separada)
- BFF y Notify en EC2-1 intentan conectarse a `172.31.45.236:5672`
- La conexión TCP funciona (verificado con telnet)
- El handshake AMQP no se completa a tiempo
- RabbitMQ cierra la conexión con: `{handshake_timeout,handshake}`

**Tiempo invertido en troubleshooting:** ~1.5 horas

**Configuraciones probadas:**
1. ✅ Aumentar timeouts de Spring (`CONNECTION_TIMEOUT=60000`)
2. ✅ Aumentar timeouts de RabbitMQ (`handshake_timeout = 60000`)
3. ✅ Upgrade de EC2-1 a t2.medium (4 GB RAM)
4. ✅ Verificar security groups y conectividad

**Resultado:** Sin éxito. El problema persiste.

**Documentación completa:** Ver [TROUBLESHOOTING.md](./TROUBLESHOOTING.md)

---

## 📋 Archivos de Configuración Importantes

### EC2-1 (ec2-apps)
- **Docker Compose:** `infra/ec2-apps/docker-compose.yml`
- **Variables de entorno RabbitMQ:**
  ```yaml
  SPRING_RABBITMQ_HOST: 172.31.45.236
  SPRING_RABBITMQ_PORT: 5672
  SPRING_RABBITMQ_USERNAME: admin
  SPRING_RABBITMQ_PASSWORD: admin123
  SPRING_RABBITMQ_CONNECTION_TIMEOUT: 60000
  SPRING_RABBITMQ_REQUESTED_HEARTBEAT: 60
  ```

### EC2-2 (ec2-mq)
- **Docker Compose:** `~/rabbitmq-cluster/docker-compose.yml`
- **Configuración:** `~/rabbitmq-cluster/rabbitmq.conf`
  ```conf
  handshake_timeout = 60000
  channel_max = 2047
  heartbeat = 60
  ```

---

## 🎯 Próximos Pasos

### Prioridad 1: Resolver integración RabbitMQ
Ver opciones en [TROUBLESHOOTING.md](./TROUBLESHOOTING.md):
- **Opción A:** Host networking en RabbitMQ
- **Opción B:** Mover RabbitMQ a EC2-1
- **Opción C:** Configuración avanzada Spring AMQP
- **Opción D:** Habilitar logs DEBUG

### Prioridad 2: Mejoras de código
Ver [INFORME_MEJORAS.md](./INFORME_MEJORAS.md):
1. Validaciones básicas
2. Manejo de errores unificado
3. Persistencia real en bases de datos
4. Pruebas unitarias
5. Documentación detallada de endpoints

### Prioridad 3: Integración Kafka (próxima evaluación)
- Desplegar Kafka + Zookeeper
- Publicar eventos de negocio
- Consumir eventos en Audit y Report

---

## 📊 Métricas del Proyecto

### Código
- **Microservicios:** 6
- **Controladores:** ~20
- **Endpoints REST:** ~40
- **Modelos de dominio:** ~15
- **DTOs:** ~15

### Infraestructura
- **Instancias EC2:** 2
- **Contenedores Docker:** 9
- **Bases de datos:** 2 (PostgreSQL + MongoDB)
- **Message brokers:** 1 (RabbitMQ)

### Estado Operacional
- **Servicios operacionales:** 6 de 8 (75%)
- **Microservicios funcionales:** 4 de 6 (66%)
- **Infraestructura desplegada:** 100%

---

## 🔗 Enlaces Útiles

### Servicios Activos
- Orders: http://54.221.95.141:8081/actuator/health
- Catalog: http://54.221.95.141:8084/actuator/health
- Audit: http://54.221.95.141:8083/actuator/health
- Report: http://54.221.95.141:8085/actuator/health

### RabbitMQ
- Management UI: http://34.239.139.42:15672
- AMQP: `34.239.139.42:5672`

### Repositorio
- GitHub: https://github.com/ZEETAALOL/cloud-native.git
- Rama: `main`
- Último commit: `c527e52`

---

## 📝 Notas Adicionales

### Decisiones Técnicas
1. **t2.medium elegido:** t2.micro (1 GB) era insuficiente para 8 contenedores Java
2. **Arquitectura multi-instancia:** Separar aplicaciones de mensajería para escalar independientemente
3. **Docker bridge networking:** Estándar de Docker, pero causó el problema actual con RabbitMQ

### Lecciones Aprendidas
1. Spring AMQP requiere handshake completo que puede fallar en redes Docker bridge
2. AWS t2.micro es insuficiente para múltiples microservicios Java
3. Separar RabbitMQ en instancia propia puede causar problemas de networking
4. Importante documentar troubleshooting para referencia futura

### Recursos Utilizados
- **AWS Academy:** Laboratorio Learner Lab
- **GitHub:** Control de versiones
- **Docker Hub:** Imágenes oficiales
- **Spring Boot 3.2.0:** Framework principal
- **Java 17:** Runtime

---

**Última actualización:** 2025-01-06 23:30  
**Preparado por:** Kiro AI + Bastián Martínez  
**Para:** Evaluación DSY1107 - DuocUC
