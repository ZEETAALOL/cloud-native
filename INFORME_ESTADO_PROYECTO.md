# INFORME COMPLETO DEL PROYECTO PEDIDOS360
## Cloud Native Microservices - Estado Actual

**Fecha:** 21 de Septiembre 2026
**Entrega:** Martes 22 de Septiembre 2026 (23:00)
**Presentación:** Miércoles 23 de Septiembre 2026
**Estudiante:** Benjamín Martínez (bae.martinez@duocuc.cl)
**Presupuesto AWS:** $50 USD (AWS Academy Learner Lab)

---

## RESUMEN EJECUTIVO

Proyecto de arquitectura cloud-native con 6 microservicios Java Spring Boot desplegados en AWS EC2. Implementa patrones modernos como BFF, Circuit Breaker, Event-Driven Architecture y OAuth2. El backend está 95% completo y funcional localmente. Actualmente en proceso de despliegue en AWS con instancia t3.medium.

---

## ARQUITECTURA DEL SISTEMA

### Microservicios Implementados (6 total):

1. **BFF (Backend for Frontend)** - Puerto 8081
   - API Gateway principal
   - Circuit Breaker con Resilience4j
   - Autenticación OAuth2 con Azure AD
   - Integración con RabbitMQ
   - Conecta con todos los microservicios

2. **Orders** - Puerto 8082 (interno 8081)
   - Gestión de pedidos y clientes
   - Base de datos: PostgreSQL
   - CRUD completo

3. **Audit** - Puerto 8083
   - Registro de eventos del sistema
   - Base de datos: PostgreSQL
   - Auditoría centralizada

4. **Catalog** - Puerto 8084
   - Catálogo de productos
   - Base de datos: MongoDB
   - CRUD de productos

5. **Report** - Puerto 8085
   - Generación de reportes
   - Base de datos: PostgreSQL
   - Consultas analíticas

6. **Notify** - Puerto 8086
   - Servicio de notificaciones
   - Consumer de RabbitMQ
   - Envío de correos electrónicos

### Infraestructura:

- **PostgreSQL 15**: Base de datos para Orders, Audit, Report
- **MongoDB 7**: Base de datos para Catalog
- **RabbitMQ 3.13**: Message broker para comunicación asíncrona
- **Traefik** (planeado): API Gateway reverso
- **Keycloak** (planeado): Servidor OAuth2/OIDC propio

### Patrones Implementados:

1. **BFF (Backend for Frontend)**: Capa de agregación para frontend
2. **Circuit Breaker**: Resilience4j en BFF con fallbacks
3. **Event-Driven**: RabbitMQ entre BFF → Notify
4. **Database per Service**: Cada microservicio tiene su propia BD
5. **API Gateway**: Traefik como punto de entrada único
6. **OAuth2/OIDC**: Autenticación con Azure AD (EntraID)

---

## ESTADO ACTUAL DEL PROYECTO

### ✅ COMPLETADO (Backend Local):

1. **6 microservicios desarrollados** con Spring Boot 3.2.0
2. **Dockerfiles** creados para cada servicio
3. **docker-compose.yml** funcional localmente
4. **Circuit Breaker** implementado y probado en BFF
5. **RabbitMQ** configurado y funcionando (BFF → Notify)
6. **OAuth2** integrado con Azure AD EntraID
7. **Bases de datos** PostgreSQL y MongoDB funcionando
8. **Healthchecks** en todos los servicios
9. **Proyecto en GitHub**: https://github.com/ZEETAALOL/cloud-native.git
10. **Documentación técnica** de Circuit Breaker

### ⏳ EN PROCESO (Despliegue AWS):

1. **Instancia EC2** t3.medium (2 vCPU, 4GB RAM) creada
   - IP Pública: 3.85.37.168
   - Ubuntu Server 24.04 LTS
   - Security Group configurado (puertos 22, 80, 443, 8080, 15672)
   
2. **Docker y Docker Compose** instalados en EC2

3. **Servicios desplegándose por fases**:
   - ✅ PostgreSQL: Healthy
   - ✅ MongoDB: Healthy
   - ✅ RabbitMQ: Healthy
   - ✅ Audit: Healthy
   - ✅ Catalog: Healthy
   - ✅ Report: Healthy
   - ⚠️ Orders: Problema de healthcheck (puerto 8081 vs 8082) - FIX DISPONIBLE
   - ⏳ Notify: Pendiente de Orders
   - ⏳ BFF: Pendiente de todos los anteriores

4. **Fix aplicado**: Corrección de puerto de Orders (8081 interno, 8082 externo)

### ❌ PENDIENTE (Para Martes):

**Backend:**
1. Terminar despliegue de Orders, Notify y BFF en EC2
2. Configurar Keycloak en EC2 (o mantener Azure AD)
3. Probar todos los endpoints en AWS
4. Verificar Circuit Breaker en producción
5. Probar flujo completo RabbitMQ

**Frontend (3-4 horas - Martes 14:00-19:00):**
1. Crear aplicación React con Vite
2. Integración con Keycloak/Azure AD
3. Pantallas básicas:
   - Login
   - Dashboard
   - Lista de productos (GET /api/products)
   - Crear producto (POST /api/products)
   - Ver auditoría (GET /api/audit)
   - Enviar notificación (POST /api/notify)
4. Diseño responsive (Bootstrap/Tailwind)
5. Manejo de errores y loading states

**Documentación (1-2 horas - Martes 20:00-23:00):**
1. README.md profesional con:
   - Descripción del proyecto
   - Arquitectura (diagrama)
   - Tecnologías utilizadas
   - Instrucciones de despliegue
   - URLs de acceso
   - Credenciales de prueba
   
2. Documentación técnica:
   - Patrones implementados
   - Decisiones de diseño
   - Configuración de servicios
   - Diagramas de secuencia
   
3. Screenshots/Video:
   - Sistema funcionando
   - Circuit Breaker en acción
   - RabbitMQ procesando mensajes
   - Frontend interactuando con backend

---

## CONFIGURACIÓN TÉCNICA

### URLs de Acceso (cuando esté completo):

```
API Gateway (Traefik):     http://3.85.37.168
Keycloak Admin:            http://3.85.37.168:8080
RabbitMQ Management:       http://3.85.37.168:15672
BFF API:                   http://3.85.37.168:8081
Orders Service:            http://3.85.37.168:8082
Audit Service:             http://3.85.37.168:8083
Catalog Service:           http://3.85.37.168:8084
Report Service:            http://3.85.37.168:8085
Notify Service:            http://3.85.37.168:8086
```

### Credenciales:

```
RabbitMQ:
- Usuario: admin
- Password: admin123
- Management UI: http://3.85.37.168:15672

PostgreSQL:
- Usuario: pedidos360
- Password: pedidos360pass
- Base de datos: pedidos360
- Puerto: 5432

MongoDB:
- Usuario: pedidos360
- Password: pedidos360pass
- Base de datos: catalog
- Puerto: 27017

Keycloak (pendiente configurar):
- Usuario: admin
- Password: admin
- Realm: pedidos360
- Client: pedidos360-client

Azure AD (actual):
- Issuer URI: https://login.microsoftonline.com/47c2bee0-5950-430f-9276-bfc083e3d1da/v2.0
- Client ID: faba8741-ba0d-440c-b061-f1aa893eb957
```

### Variables de Entorno (.env):

```
ENTRA_ISSUER_URI=https://login.microsoftonline.com/47c2bee0-5950-430f-9276-bfc083e3d1da/v2.0
ENTRA_API_CLIENT_ID=faba8741-ba0d-440c-b061-f1aa893eb957
KEYCLOAK_URL=http://3.85.37.168:8080
KEYCLOAK_REALM=pedidos360
KEYCLOAK_CLIENT_ID=pedidos360-client
SPRING_PROFILES_ACTIVE=docker,prod
PUBLIC_IP=3.85.37.168
```

---

## PROBLEMAS ENCONTRADOS Y SOLUCIONES

### Problema 1: AWS Academy Learner Lab - Permisos Restringidos
**Síntoma:** Error "not authorized to perform: ec2:RunInstances" al crear VPC personalizada
**Solución:** Usar VPC por defecto y configuración simplificada de AWS Academy

### Problema 2: Instancia t2.micro Insuficiente
**Síntoma:** Build de Docker se congelaba, conexiones SSH caídas
**Causa:** 1GB RAM insuficiente para compilar 6 microservicios Java
**Solución:** Migrar a t3.medium (4GB RAM) - Costo ~$2/día

### Problema 3: Docker Compose Incompleto
**Síntoma:** Servicios fallaban porque no había PostgreSQL, MongoDB, RabbitMQ
**Causa:** docker-compose.yml original solo tenía microservicios
**Solución:** Crear docker-compose.full.yml con toda la infraestructura

### Problema 4: Orders Service "Unhealthy"
**Síntoma:** Orders siempre fallaba el healthcheck
**Causa:** Puerto interno 8081 pero healthcheck apuntaba a 8082
**Solución:** Corregir mapeo de puertos (8082:8081) y healthcheck a puerto 8081
**Estado:** Fix disponible en GitHub, pendiente de aplicar

### Problema 5: Despliegue Gradual
**Síntoma:** Iniciar todo a la vez causaba fallos en cadena
**Solución:** Estrategia de despliegue por fases:
   1. Infraestructura (PostgreSQL, MongoDB, RabbitMQ)
   2. Microservicios base (Audit, Catalog, Report)
   3. Microservicios dependientes (Orders, Notify)
   4. BFF (depende de todos)

---

## COMANDOS IMPORTANTES

### Conectarse a EC2:
```bash
ssh -i ~/Downloads/TU-CLAVE.pem ubuntu@3.85.37.168
```

### Desplegar el fix de Orders:
```bash
cd ~/cloud-native/infra/docker
wget -O docker-compose.full.yml https://raw.githubusercontent.com/ZEETAALOL/cloud-native/main/infra/docker/docker-compose.full.yml
sudo docker-compose -f docker-compose.full.yml up -d --force-recreate orders
sleep 60
sudo docker-compose -f docker-compose.full.yml ps
```

### Desplegar BFF (después de Orders):
```bash
sudo docker-compose -f docker-compose.full.yml up -d bff
sleep 60
sudo docker-compose -f docker-compose.full.yml ps
```

### Ver logs de un servicio:
```bash
sudo docker-compose -f docker-compose.full.yml logs --tail=100 [servicio]
```

### Ver estado de todos los servicios:
```bash
sudo docker-compose -f docker-compose.full.yml ps
```

### Reiniciar un servicio:
```bash
sudo docker-compose -f docker-compose.full.yml restart [servicio]
```

### Detener todo:
```bash
sudo docker-compose -f docker-compose.full.yml down
```

---

## PLAN DE TRABAJO MARTES 22/09/2026

### Mañana (antes de las 14:00):
- [ ] Aplicar fix de Orders en EC2
- [ ] Desplegar BFF
- [ ] Verificar que todos los servicios están Healthy
- [ ] Probar endpoints básicos

### Trabajo (14:00-19:00):
- [ ] Crear proyecto React con Vite
- [ ] Configurar autenticación OAuth2
- [ ] Implementar pantalla de login
- [ ] Crear dashboard principal
- [ ] Implementar CRUD de productos
- [ ] Integrar con API del BFF

### Casa (20:00-23:00):
- [ ] Terminar frontend
- [ ] Documentación completa
- [ ] README.md profesional
- [ ] Screenshots del sistema funcionando
- [ ] Video demo (opcional)
- [ ] Revisar que todo funcione
- [ ] Preparar presentación para el miércoles

---

## TECNOLOGÍAS UTILIZADAS

### Backend:
- Java 17
- Spring Boot 3.2.0
- Spring Cloud (Circuit Breaker)
- Resilience4j
- Spring Security OAuth2
- Spring AMQP (RabbitMQ)
- Spring Data JPA
- Spring Data MongoDB
- PostgreSQL 15
- MongoDB 7
- RabbitMQ 3.13
- Docker & Docker Compose

### Frontend (pendiente):
- React 18
- Vite
- React Router
- Axios
- OAuth2 Client Library
- Bootstrap/Tailwind CSS

### Infraestructura:
- AWS EC2 (t3.medium)
- Ubuntu Server 24.04 LTS
- Docker Engine
- Docker Compose
- Traefik (planeado)
- Keycloak (planeado)

### DevOps:
- Git & GitHub
- Docker multi-stage builds
- Docker Compose orchestration
- AWS Security Groups
- SSH

---

## ESTIMACIÓN DE COSTOS AWS

**Instancia t3.medium:**
- Costo por hora: $0.042
- Costo por día: ~$1.00
- Uso estimado: 2-3 días
- Costo total: $2-3 USD
- Presupuesto restante: $47-48 de $50

**Recomendación:** Mantener t3.medium hasta después de la presentación del miércoles. Si necesitas mantener el proyecto más tiempo, cambiar a t2.micro (gratis).

---

## ESTRUCTURA DEL REPOSITORIO

```
proyecto_cloudnative_ev1/
├── backend/
│   ├── audit/          (Microservicio de auditoría)
│   ├── bff/            (Backend for Frontend)
│   ├── catalog/        (Catálogo de productos)
│   ├── notify/         (Notificaciones)
│   ├── orders/         (Pedidos y clientes)
│   └── report/         (Reportes)
├── frontend/           (React - pendiente)
├── infra/
│   ├── aws/
│   │   ├── deploy-to-ec2.sh
│   │   └── INSTRUCCIONES_AWS.md
│   ├── docker/
│   │   ├── docker-compose.yml (original - solo microservicios)
│   │   ├── docker-compose.full.yml (completo con BD)
│   │   └── init-db.sql
│   └── mq/
│       └── docker-compose.yml (RabbitMQ standalone)
├── .gitignore
├── README.md
└── INFORME_ESTADO_PROYECTO.md (este archivo)
```

---

## CONTACTOS Y RECURSOS

**GitHub:** https://github.com/ZEETAALOL/cloud-native.git
**AWS EC2 IP:** 3.85.37.168
**Email:** bae.martinez@duocuc.cl

**Documentación de referencia:**
- Spring Boot: https://spring.io/projects/spring-boot
- Resilience4j: https://resilience4j.readme.io/
- Docker Compose: https://docs.docker.com/compose/
- AWS EC2: https://docs.aws.amazon.com/ec2/

---

## NOTAS FINALES

1. **Prioridad 1:** Terminar despliegue de backend en AWS (Orders + BFF)
2. **Prioridad 2:** Frontend básico funcional
3. **Prioridad 3:** Documentación completa
4. **Opcional:** Keycloak propio (si alcanza el tiempo)

**Estado actual:** Backend 95% completo, falta solo resolver Orders y levantar BFF. Frontend 0%. Documentación 40%.

**Tiempo restante:** ~26 horas hasta entrega final (Martes 23:00)

**Riesgo principal:** Tiempo ajustado para frontend y documentación. Se recomienda frontend minimalista pero funcional sobre uno complejo incompleto.

---

FIN DEL INFORME
