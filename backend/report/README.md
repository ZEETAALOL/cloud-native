# ms-pedidos360-report

Microservicio de reportería y KPIs en tiempo real.

## Responsabilidad
- Consumir eventos de Kafka
- Generar agregaciones y estadísticas
- Exponer endpoints de lectura para dashboards
- Analítica en tiempo real

## Stack Tecnológico
- Java 17
- Spring Boot 3.x
- Spring Kafka
- Oracle Database (para almacenar métricas procesadas)

## Tópicos Kafka que consume
- `orders.events` - Eventos de pedidos (OrderCreated, OrderAccepted, etc.)
- `audit.timeline` - Timeline de eventos de negocio

## Endpoints (read-only)
- `GET /api/report/kpis?range=last24h` - KPIs últimas 24 horas
- `GET /api/report/top-products?range=last7d` - Top productos última semana
- `GET /api/report/lead-time` - Tiempo promedio de entrega

## Métricas Calculadas
- Ventas por hora
- Lead time (tiempo creación → entrega)
- Estados activos de pedidos
- Top productos

## Implementación Fase 3

**Versión simplificada** (sin Kafka ni BD, solo REST API con datos simulados)

### Funcionalidades implementadas:
- ✅ Reporte de Ventas con métricas agregadas
- ✅ Reporte de Inventario con stock por categoría
- ✅ Reporte de Clientes con distribución regional
- ✅ Reporte de Rendimiento del sistema
- ✅ Datos generados al momento (simulación)

### Endpoints REST:
- `GET /api/reports/sales` - Reporte de ventas
- `GET /api/reports/inventory` - Reporte de inventario
- `GET /api/reports/customers` - Reporte de clientes
- `GET /api/reports/performance` - Reporte de rendimiento
- `GET /api/reports/types` - Tipos de reportes disponibles

### Ejecutar localmente:
```bash
cd backend/report
mvn spring-boot:run
```

Puerto: **8085**

## Estado
🟢 **Implementado (versión básica para demo)**
