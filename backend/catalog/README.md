# ms-pedidos360-catalog

Microservicio de gestión de catálogo de productos.

## Responsabilidad
- CRUD de productos
- Gestión de stock
- Control de precios

## Stack Tecnológico
- Java 17
- Spring Boot 3.x
- Oracle Database (cloud)
- Spring Data JPA

## Endpoints
- `GET /api/catalog/products` - Listar productos
- `POST /api/catalog/products` - Crear producto
- `PUT /api/catalog/products/{id}` - Actualizar producto (precio/stock)
- `DELETE /api/catalog/products/{id}` - Eliminar producto

## Base de Datos
- Tabla: `productos`
- Campos: id, nombre, descripcion, precio, stock, activo

## Implementación Fase 3

**Versión simplificada** (sin BD, solo REST API con datos en memoria)

### Funcionalidades implementadas:
- ✅ CRUD completo de productos
- ✅ Gestión de stock en memoria
- ✅ Control de precios
- ✅ Filtrado por categoría
- ✅ Filtrado por estado activo

### Endpoints REST:
- `GET /api/products` - Listar todos los productos
- `GET /api/products/{id}` - Obtener producto por ID
- `GET /api/products/category/{category}` - Productos por categoría
- `GET /api/products/active` - Productos activos
- `POST /api/products` - Crear producto
- `PUT /api/products/{id}` - Actualizar producto
- `DELETE /api/products/{id}` - Eliminar producto

### Ejecutar localmente:
```bash
cd backend/catalog
mvn spring-boot:run
```

Puerto: **8084**

## Estado
🟢 **Implementado (versión básica para demo)**
