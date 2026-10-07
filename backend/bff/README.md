# BFF (Backend for Frontend) - Pedidos360

Backend for Frontend que valida JWT de Azure AD y orquesta llamadas a microservicios.

## 🚀 Inicio Rápido

```powershell
# Configurar variables de entorno
$env:ENTRA_ISSUER_URI="https://login.microsoftonline.com/<TENANT_ID>/v2.0"
$env:ENTRA_API_CLIENT_ID="<API_CLIENT_ID>"

# Ejecutar
mvn spring-boot:run
```

Corre en **http://localhost:8080**

## 🛠️ Tecnologías

- Spring Boot 4.1.1
- Java 25
- Maven
- OAuth2 Resource Server
- RestClient

## 📋 Prerequisitos

- Java 25 (JDK)
- Maven
- Variables de entorno configuradas

## ⚙️ Configuración

### Variables de Entorno Requeridas

```powershell
$env:ENTRA_ISSUER_URI="https://login.microsoftonline.com/<TENANT_ID>/v2.0"
$env:ENTRA_API_CLIENT_ID="<API_CLIENT_ID>"
```

### application.yml

```yaml
server:
  port: 8080

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${ENTRA_ISSUER_URI}
          audiences: api://${ENTRA_API_CLIENT_ID}

microservices:
  order-service:
    url: http://localhost:8081
```

## 📁 Estructura

```
src/main/
├── java/cl/duoc/api/
│   ├── ApiApplication.java           # Main class
│   ├── SecurityConfig.java           # OAuth2 + CORS
│   ├── controller/
│   │   └── DataController.java       # GET /api/data
│   ├── service/
│   │   └── DataService.java          # Lógica de negocio
│   ├── repository/
│   │   └── ClienteRepository.java    # RestClient a Orders
│   └── dto/
│       └── ClienteResponse.java      # Record
└── resources/
    └── application.yml
```

## 🔌 Endpoints

### GET /api/data
Obtiene datos del cliente desde el microservicio Orders.

**Headers requeridos:**
```
Authorization: Bearer <JWT-token>
```

**Respuesta exitosa (200):**
```json
{
  "rut": "20456789-3",
  "nombre": "Bastián",
  "apellido": "Martínez",
  "direccion": "Av. Providencia 2594, Providencia",
  "comuna": "Santiago"
}
```

**Errores:**
- `401 Unauthorized` - Token inválido o expirado
- `500 Internal Server Error` - Microservicio Orders no disponible

## 🔐 Seguridad

### Validación JWT
- Valida firma del token con Azure AD
- Verifica issuer: `https://login.microsoftonline.com/<TENANT_ID>/v2.0`
- Verifica audience: `api://<API_CLIENT_ID>`

### CORS
Permite requests desde:
- `http://localhost:5173` (Frontend)

## 🔄 Comunicación con Microservicios

### Orders Service
- URL: `http://localhost:8081`
- Endpoint: `GET /clientes`
- Sin autenticación (confianza interna)

## 📦 Build y Ejecución

```powershell
# Compilar
mvn clean package

# Ejecutar con Maven
mvn spring-boot:run

# Ejecutar JAR
java -jar target/ms-pedidos360-bff-1.0.0.jar
```

## 🐛 Troubleshooting

### Error: "Invalid token"
- Verifica que `$env:ENTRA_ISSUER_URI` esté configurado correctamente
- Verifica que `$env:ENTRA_API_CLIENT_ID` coincida con el registrado en Azure AD
- Revisa que el token tenga el scope `access_as_user`

### Error: "Connection refused" al llamar Orders
- Verifica que Orders esté corriendo en puerto 8081
- Prueba manualmente: `curl http://localhost:8081/clientes`
- Revisa `application.yml` tenga la URL correcta

### Error: "CORS policy"
- Verifica que `SecurityConfig.java` tenga `http://localhost:5173` en `allowedOrigins`
- Reinicia el BFF después de cambiar la configuración

## 📊 Logs

```powershell
# Ver logs en tiempo real
mvn spring-boot:run

# Nivel de log DEBUG
# Edita application.yml:
logging:
  level:
    cl.duoc: DEBUG
```

---

Ver documentación completa en [`../../docs/INSTRUCCIONES_SETUP.md`](../../docs/INSTRUCCIONES_SETUP.md)
