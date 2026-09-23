# Pedidos360 - Sistema Cloud-Native de Gestión de Pedidos

Sistema de microservicios para gestión de pedidos desarrollado con arquitectura cloud-native, desplegado en AWS EC2.

## Tecnologías

- **Backend:** Spring Boot (Java 17)
- **Frontend:** React + Vite
- **Autenticación:** OAuth2 con Azure AD (Microsoft Entra ID)
- **Mensajería:** RabbitMQ
- **Bases de datos:** PostgreSQL, MongoDB
- **Contenedores:** Docker
- **Cloud:** AWS EC2
- **Resiliencia:** Resilience4j (Circuit Breaker, Retry)

## Arquitectura

El sistema está compuesto por 6 microservicios independientes:

1. **BFF (Backend For Frontend)** - Puerto 8080
   - Punto de entrada único para el frontend
   - Valida JWT de Azure AD
   - Circuit Breaker para llamadas a microservicios

2. **Orders** - Puerto 8081
   - Gestión de pedidos
   - Base de datos: PostgreSQL

3. **Catalog** - Puerto 8084
   - Catálogo de productos (CRUD)
   - Base de datos: PostgreSQL

4. **Audit** - Puerto 8083
   - Registro de eventos del sistema
   - Base de datos: MongoDB

5. **Report** - Puerto 8085
   - Generación de reportes
   - Base de datos: PostgreSQL

6. **Notify** - Puerto 8086
   - Envío de notificaciones por email
   - Consumidor de RabbitMQ

## Despliegue

### Backend (AWS EC2)

Los microservicios están desplegados en una instancia EC2 t3.medium usando Docker Compose.

**IP actual:** 54.242.98.221

**Containers corriendo:**
- postgres (Puerto 5432)
- mongodb (Puerto 27017)
- rabbitmq (Puerto 5672, Management 15672)
- bff (Puerto 8080)
- orders (Puerto 8081)
- catalog (Puerto 8084)
- audit (Puerto 8083)
- report (Puerto 8085)
- notify (Puerto 8086)

### Frontend

El frontend React está disponible en:
- **Local con autenticación:** http://localhost:5173 (OAuth2 Azure AD)
- **AWS sin autenticación:** http://54.242.98.221:3000 (demo mode)

## Configuración de Autenticación

El sistema usa OAuth2 con Azure AD (Microsoft Entra ID).

**Tenant ID:** 47c2bee0-5950-430f-9276-bfc083e3d1da  
**Client ID:** faba8741-ba0d-440c-b061-f1aa893eb957

**Redirect URIs configurados:**
- http://localhost:5173
- http://localhost:3000

## Desarrollo Local

### Requisitos
- Node.js 20+
- Maven 3.8+
- Java 17
- Docker & Docker Compose

### Backend

```bash
cd backend/[microservicio]
mvn clean package
java -jar target/*.jar
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Comunicación entre Servicios

### Síncrona (RestClient)
- BFF → Microservicios (HTTP/REST)
- Circuit Breaker para tolerancia a fallos

### Asíncrona (RabbitMQ)
- Eventos de negocio entre microservicios
- Notificaciones por email

## CORS

El BFF está configurado para aceptar peticiones desde:
- http://localhost:*
- http://127.0.0.1:*
- http://54.242.98.221:*

## Autor

Bastián Martínez - DuocUC

## Licencia

Proyecto académico - Evaluación Cloud Native
