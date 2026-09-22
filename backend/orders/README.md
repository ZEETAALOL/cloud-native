# Orders Service - Pedidos360

Microservicio de gestión de pedidos y clientes del sistema Pedidos360.

## 🚀 Inicio Rápido

```powershell
mvn spring-boot:run
```

Corre en **http://localhost:8081**

## 🛠️ Tecnologías

- Spring Boot 3.2.0
- Java 17
- Maven

## 📋 Prerequisitos

- Java 17 (JDK)
- Maven

## 📁 Estructura

```
src/main/
├── java/cl/duoc/orders/
│   ├── OrdersApplication.java         # Main class
│   ├── controller/
│   │   └── ClienteController.java     # GET /clientes
│   └── dto/
│       └── ClienteResponse.java       # Record
└── resources/
    └── application.yml
```

## 🔌 Endpoints

### GET /clientes
Retorna información del cliente registrado en el sistema.

**Sin autenticación requerida** (solo llamado desde BFF).

**Respuesta (200):**
```json
{
  "rut": "19876543-2",
  "nombre": "María",
  "apellido": "González",
  "direccion": "Av. Libertador Bernardo O'Higgins 1234",
  "comuna": "Santiago"
}
```

## 📦 Build y Ejecución

```powershell
# Compilar
mvn clean package

# Ejecutar con Maven
mvn spring-boot:run

# Ejecutar JAR
java -jar target/ms-pedidos360-orders-1.0.0.jar
```

## 🧪 Probar Manualmente

```powershell
# PowerShell
Invoke-WebRequest http://localhost:8081/clientes

# Con curl
curl http://localhost:8081/clientes
```

## 🔄 Integración

Este microservicio es llamado por:
- **BFF** en `http://localhost:8080` mediante `ClienteRepository.java`

## 📊 Logs

```powershell
# Ver logs en tiempo real
mvn spring-boot:run

# Deberías ver:
# INFO: Solicitud recibida en /clientes
# INFO: Retornando cliente: María González
```

## 🚀 Futuras Mejoras

- [ ] Conectar a base de datos real
- [ ] Implementar CRUD completo de clientes
- [ ] Agregar validaciones
- [ ] Agregar tests unitarios
- [ ] Agregar manejo de errores personalizado

---

Ver documentación completa en [`../../docs/INSTRUCCIONES_SETUP.md`](../../docs/INSTRUCCIONES_SETUP.md)
