# 📚 GUÍA DE ESTUDIO - PEDIDOS360
## Sistema Cloud-Native con Microservicios

**Autor:** Bastián Martínez  
**Fecha:** Septiembre 2026  
**Proyecto:** Evaluación Cloud Native - DuocUC

---

## 🎯 ¿QUÉ ES PEDIDOS360?

Un **sistema de gestión de pedidos** construido con arquitectura de microservicios y desplegado en la nube (AWS).

**Analogía Simple:**  
Es como un **restaurante moderno** donde cada departamento es independiente pero trabajan coordinados.

---

## 🏗️ ARQUITECTURA GENERAL (Vista de Pájaro)

```
┌─────────────────────────────────────────────┐
│  USUARIO usa navegador web                  │
│  (Chrome, Firefox, Edge)                    │
└──────────────┬──────────────────────────────┘
               │
               ↓
┌─────────────────────────────────────────────┐
│  MICROSOFT AZURE AD                         │
│  (Sistema de Login - OAuth2)                │
│  ✅ Valida usuario                          │
│  ✅ Genera TOKEN de seguridad               │
└──────────────┬──────────────────────────────┘
               │
               ↓
┌─────────────────────────────────────────────┐
│  FRONTEND - REACT                           │
│  (localhost:5173)                           │
│  - Pantallas que ve el usuario              │
│  - Login, Dashboard, Productos, etc.        │
└──────────────┬──────────────────────────────┘
               │
               ↓ HTTP con TOKEN
┌─────────────────────────────────────────────┐
│  BFF - Backend for Frontend                 │
│  (AWS EC2: 54.242.196.191:8081)            │
│  🛡️ Valida TOKEN                           │
│  🎭 Coordina microservicios                 │
│  🔌 Circuit Breaker                         │
└─────┬───────┬──────┬──────┬──────┬─────────┘
      │       │      │      │      │
      ↓       ↓      ↓      ↓      ↓
   ┌─────┐ ┌────┐ ┌────┐ ┌────┐ ┌─────┐
   │Order│ │Catlg│ │Audt│ │Rprt│ │Notfy│
   │:8082│ │:8084│ │:8083│ │:8085│ │:8086│
   └──┬──┘ └─┬──┘ └─┬──┘ └─┬──┘ └──┬──┘
      │      │      │      │       │
      ↓      ↓      ↓      ↓       ↓
   ┌──────────────────────────┐    │
   │  BASES DE DATOS          │    │
   │  • PostgreSQL x3         │    │
   │  • MongoDB x1            │    │
   └──────────────────────────┘    │
                                   ↓
                            ┌──────────┐
                            │ RabbitMQ │
                            │ (Mensajes)│
                            └──────────┘
```

---

## 🔐 PARTE 1: AUTENTICACIÓN (OAuth2 con Azure AD)

### ¿Qué es OAuth2?

Un **protocolo de seguridad** que permite login con servicios externos (como Microsoft, Google, Facebook).

### Flujo de Login:

```
1. Usuario → Click "INICIAR SESIÓN CON MICROSOFT"
   
2. Frontend → Redirige a Microsoft
   URL: login.microsoftonline.com
   
3. Usuario → Ingresa email y contraseña en Microsoft
   
4. Microsoft → Valida credenciales
   ✅ Correcto → Genera TOKEN JWT
   ❌ Incorrecto → Error
   
5. Microsoft → Redirige de vuelta al Frontend
   Con TOKEN en la URL
   
6. Frontend → Guarda TOKEN en memoria (MSAL library)
   
7. ✅ Usuario autenticado
```

### ¿Qué es un TOKEN JWT?

Un **documento digital cifrado** que contiene:
- Nombre del usuario
- Email
- Fecha de expiración
- Permisos

**Ejemplo simplificado:**
```json
{
  "nombre": "Bastián Martínez",
  "email": "bae.martinez@duocuc.cl",
  "expira": "2026-09-22 23:59:59"
}
```

### ¿Por qué es seguro?

- El TOKEN está **firmado** por Microsoft (no se puede falsificar)
- Tiene **fecha de expiración** (si lo roban, caduca pronto)
- Se envía **encriptado** (HTTPS)

---

## 📱 PARTE 2: FRONTEND (React)

### ¿Qué es?

La **interfaz visual** que ve el usuario. Las pantallas.

### Tecnologías:

- **React:** Framework JavaScript para interfaces
- **Vite:** Herramienta rápida para desarrollo
- **MSAL:** Library de Microsoft para OAuth2
- **Axios:** Para hacer peticiones HTTP al backend
- **Bootstrap:** Estilos CSS pre-hechos

### Páginas Principales:

1. **Login.jsx** → Pantalla de inicio de sesión
2. **Dashboard.jsx** → Panel principal con estadísticas
3. **Products.jsx** → CRUD de productos
4. **Audit.jsx** → Ver logs del sistema
5. **Notify.jsx** → Enviar notificaciones

### ¿Cómo hace peticiones?

```javascript
// Frontend (Products.jsx)
const productos = await apiService.getProducts();
```

**Internamente hace:**
```javascript
axios.get('http://54.242.196.191:8081/api/products', {
  headers: {
    'Authorization': 'Bearer TOKEN_AQUI'
  }
})
```

**Importante:** El frontend SIEMPRE incluye el TOKEN en cada petición.

---

## 🛡️ PARTE 3: BFF (Backend for Frontend)

### ¿Qué es?

El **coordinador principal**. Es la puerta de entrada al backend.

**Puerto:** 8081  
**Ubicación:** AWS EC2 (54.242.196.191)

### Responsabilidades:

1. **Validar TOKEN JWT**
   ```java
   // ¿El TOKEN es válido?
   // ¿Es de nuestro sistema?
   // ¿No está vencido?
   ```

2. **Orquestar Microservicios**
   ```
   Frontend pide productos
   → BFF llama a Catalog Service
   → BFF recibe respuesta
   → BFF la envía al Frontend
   ```

3. **Circuit Breaker** (Protección contra fallos)
   ```
   Si Catalog Service falla
   → BFF responde con lista vacía
   → Sistema sigue funcionando
   ```

4. **CORS** (Permitir llamadas desde localhost)
   ```java
   // Permite que Frontend local llame al backend en AWS
   allowedOrigins: "http://localhost:5173"
   ```

### Código Real (Simplificado):

```java
@RestController
public class ProductController {
    
    @GetMapping("/api/products")
    public List<Product> getProducts() {
        // 1. Spring valida TOKEN automáticamente
        // 2. Si es válido, ejecuta este método
        // 3. Si no, retorna 401 Unauthorized
        
        return catalogService.findAll();
    }
}
```

---

## 🏢 PARTE 4: MICROSERVICIOS (Los Trabajadores)

### Concepto Clave:

Cada microservicio hace **UNA SOLA COSA** y la hace bien.

### 1️⃣ Orders Service (Puerto 8082)

**Función:** Gestiona pedidos y clientes

**Base de Datos:** PostgreSQL (pedidos360_orders)

**Endpoints:**
- `GET /clientes` → Lista de clientes
- `POST /pedidos` → Crear pedido
- `GET /pedidos/{id}` → Ver pedido

**Ejemplo de Datos:**
```json
{
  "rut": "20456789-3",
  "nombre": "Bastián",
  "apellido": "Martínez",
  "direccion": "Av. Providencia 2594"
}
```

---

### 2️⃣ Catalog Service (Puerto 8084)

**Función:** Catálogo de productos

**Base de Datos:** MongoDB (catalog_db)

**¿Por qué MongoDB?**  
Porque los productos tienen **muchos atributos variables** (color, tamaño, especificaciones). MongoDB es flexible para esto.

**Endpoints:**
- `GET /products` → Lista productos
- `POST /products` → Crear producto
- `PUT /products/{id}` → Actualizar
- `DELETE /products/{id}` → Eliminar

**Ejemplo de Producto:**
```json
{
  "id": 1,
  "name": "Smartphone Samsung Galaxy S24",
  "price": 899000,
  "stock": 25,
  "category": "Tecnología"
}
```

---

### 3️⃣ Audit Service (Puerto 8083)

**Función:** Registra TODO lo que pasa en el sistema

**Base de Datos:** PostgreSQL (pedidos360_audit)

**¿Para qué sirve?**  
Para tener un **historial** de acciones. Si algo sale mal, puedes revisar qué pasó.

**Ejemplos de Eventos:**
```
- "Usuario X inició sesión"
- "Usuario Y creó producto iPhone 15"
- "Usuario Z eliminó pedido #123"
- "Sistema detectó fallo en Catalog Service"
```

**Endpoints:**
- `GET /audit` → Todos los eventos
- `GET /audit/user/{id}` → Eventos de un usuario
- `POST /audit` → Registrar evento

---

### 4️⃣ Report Service (Puerto 8085)

**Función:** Genera reportes y estadísticas

**Base de Datos:** PostgreSQL (pedidos360_reports)

**Ejemplos:**
- Total de ventas del mes
- Productos más vendidos
- Clientes más activos

**Endpoints:**
- `GET /reports/data` → Datos para reportes
- `GET /reports/sales` → Reporte de ventas

---

### 5️⃣ Notify Service (Puerto 8086)

**Función:** Envía notificaciones (email, SMS)

**Base de Datos:** ❌ NO TIENE (Stateless)

**¿Por qué no tiene BD?**  
Porque solo **procesa** y **envía**. No guarda nada.

**¿Cómo funciona?**  
Escucha mensajes de **RabbitMQ** y los procesa.

```java
@RabbitListener(queues = "email-queue")
public void handleEmail(EmailData data) {
    // Envía el email
    emailService.send(data);
}
```

---

## 🐰 PARTE 5: RABBITMQ (Mensajería Asíncrona)

### ¿Qué es?

Un **sistema de mensajes** entre servicios. Como WhatsApp pero para programas.

### ¿Para qué sirve?

Para que las cosas pasen **sin bloquear**.

### Ejemplo SIN RabbitMQ (Lento ❌):

```
Usuario → Crear pedido → Espera... (1 seg)
                       → Enviar email → Espera... (5 seg)
                                     → ¡Listo! (6 segundos total)
```

### Ejemplo CON RabbitMQ (Rápido ✅):

```
Usuario → Crear pedido → ¡Listo! (1 segundo)

(En paralelo, sin que el usuario espere...)
RabbitMQ → Notify Service → Envía email (5 seg en segundo plano)
```

### Conceptos:

- **Producer:** Quien envía mensajes (BFF)
- **Queue:** Buzón donde se guardan mensajes
- **Consumer:** Quien recibe y procesa (Notify)

### Código:

**Publicar mensaje (BFF):**
```java
rabbitTemplate.convertAndSend("email-queue", emailData);
```

**Consumir mensaje (Notify):**
```java
@RabbitListener(queues = "email-queue")
public void handleEmail(EmailData data) {
    sendEmail(data);
}
```

---

## 🔌 PARTE 6: CIRCUIT BREAKER (Resilience4j)

### ¿Qué es?

Un **interruptor automático** que protege el sistema cuando algo falla.

### Analogía:

Como el **interruptor de luz en tu casa**:
- Si hay un cortocircuito → El interruptor se abre (OPEN)
- Protege tu casa de quemarse
- Después de un rato, puedes intentar cerrarlo (HALF_OPEN)
- Si ya funciona, queda cerrado (CLOSED)

### Estados:

```
CLOSED (Cerrado) ✅
└─→ Todo funciona normal
    Si falla muchas veces
    ↓
OPEN (Abierto) ⚠️
└─→ Usa respuesta alternativa (fallback)
    Espera un tiempo (ej: 30 segundos)
    ↓
HALF_OPEN (Medio Abierto) 🔄
└─→ Hace pruebas para ver si ya funciona
    Si funciona → CLOSED
    Si falla → OPEN
```

### Código Real:

```java
@CircuitBreaker(name = "catalog", fallbackMethod = "fallbackProducts")
public List<Product> getProducts() {
    // Intenta llamar a Catalog Service
    return catalogService.findAll();
}

// Si falla, usa esto
public List<Product> fallbackProducts(Exception e) {
    // Respuesta alternativa: lista vacía
    return List.of();
}
```

### Configuración:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      catalog:
        sliding-window-size: 10        # Revisa últimas 10 llamadas
        failure-rate-threshold: 50     # Si 50% fallan → OPEN
        wait-duration-in-open-state: 30s  # Espera 30 seg antes de probar
```

### Ventajas:

✅ El sistema **NO se cae** aunque un servicio falle  
✅ Respuesta **inmediata** (no espera timeout)  
✅ Se **auto-recupera** cuando el servicio vuelve  

---

## 🗄️ PARTE 7: DATABASE PER SERVICE

### Concepto:

Cada microservicio tiene **su propia base de datos**.

```
Orders    → PostgreSQL (pedidos360_orders)
Audit     → PostgreSQL (pedidos360_audit)
Report    → PostgreSQL (pedidos360_reports)
Catalog   → MongoDB (catalog_db)
```

### ¿Por qué NO compartir una BD?

❌ **Compartida (Mala práctica):**
```
Orders ─┐
Catalog ├─→ PostgreSQL ÚNICA
Audit  ─┘
        ↓
Si falla PostgreSQL → TODO falla
```

✅ **Separadas (Buena práctica):**
```
Orders   → PostgreSQL 1
Catalog  → MongoDB
Audit    → PostgreSQL 2
         ↓
Si falla PostgreSQL 1 → Solo Orders falla
Los demás siguen funcionando ✅
```

### Ventajas:

1. **Independencia:** Un fallo no afecta a todos
2. **Escalabilidad:** Puedo hacer más grande solo la que necesita
3. **Tecnología adecuada:** SQL para unos, NoSQL para otros

---

## 🐳 PARTE 8: DOCKER Y CONTENEDORES

### ¿Qué es Docker?

Un sistema para crear **"cajas" (contenedores)** donde cada servicio vive aislado.

### Analogía:

Imagina que cada servicio es un **juego de PlayStation**:
- Sin Docker: Instalas el juego en tu PS5, si se daña la PS5, pierdes todo
- Con Docker: Cada juego en su propia "caja portable", si una se daña, las demás siguen

### ¿Qué es docker-compose?

Un **director de orquesta** que levanta TODOS los contenedores a la vez.

### Comando Mágico:

```bash
docker-compose -f docker-compose.full.yml up -d
```

**Esto levanta:**
- PostgreSQL (3 bases de datos)
- MongoDB
- RabbitMQ
- Orders Service
- Catalog Service
- Audit Service
- Report Service
- Notify Service
- BFF

**Todo con UN SOLO COMANDO** 🎯

### Archivo docker-compose.full.yml (Simplificado):

```yaml
services:
  postgres:
    image: postgres:15
    ports:
      - "5432:5432"
  
  mongodb:
    image: mongo:7
    ports:
      - "27017:27017"
  
  rabbitmq:
    image: rabbitmq:3.13-management
    ports:
      - "5672:5672"
      - "15672:15672"
  
  orders:
    build: ./backend/orders
    ports:
      - "8082:8081"
    depends_on:
      - postgres
  
  # ... más servicios
```

---

## ☁️ PARTE 9: DESPLIEGUE EN AWS EC2

### ¿Qué es AWS EC2?

Un **servidor virtual en la nube** de Amazon.

### Especificaciones:

- **Tipo:** t3.medium
- **CPU:** 2 vCPUs
- **RAM:** 4 GB
- **OS:** Ubuntu Server 24.04
- **IP Pública:** 54.242.196.191

### ¿Por qué en la nube?

✅ **Disponible 24/7** (siempre encendido)  
✅ **Accesible desde Internet**  
✅ **Escalable** (puedo hacerlo más grande si necesito)  
✅ **Profesional** (no es "localhost" de tu laptop)  

### Security Groups (Firewall):

```
Puerto 22   → SSH (para administrar)
Puerto 8081 → BFF
Puerto 8082 → Orders
Puerto 8083 → Audit
Puerto 8084 → Catalog
Puerto 8085 → Report
Puerto 8086 → Notify
Puerto 15672 → RabbitMQ Management
```

### Comandos Útiles:

**Conectarse:**
```bash
ssh -i clave.pem ubuntu@54.242.196.191
```

**Ver servicios:**
```bash
docker-compose ps
```

**Ver logs:**
```bash
docker-compose logs bff
```

---

## 🔄 PARTE 10: FLUJO COMPLETO (Ejemplo Real)

### Caso: Usuario crea un producto

```
┌─────────────────────────────────────────────────┐
│ PASO 1: Usuario en Frontend                    │
└─────────────────────────────────────────────────┘
Usuario llena formulario:
- Nombre: "iPhone 15 Pro"
- Precio: 1.200.000
- Stock: 10
- Categoría: "Tecnología"

Click en "CREAR PRODUCTO"

┌─────────────────────────────────────────────────┐
│ PASO 2: Frontend hace petición                 │
└─────────────────────────────────────────────────┘
POST http://54.242.196.191:8081/api/products
Headers:
  Authorization: Bearer eyJhbGc... (TOKEN)
Body:
  {
    "name": "iPhone 15 Pro",
    "price": 1200000,
    "stock": 10,
    "category": "Tecnología"
  }

┌─────────────────────────────────────────────────┐
│ PASO 3: BFF valida TOKEN                       │
└─────────────────────────────────────────────────┘
BFF pregunta a Azure AD:
  "¿Este TOKEN es válido?"
Azure AD responde:
  "✅ Sí, es del usuario Bastián Martínez"

┌─────────────────────────────────────────────────┐
│ PASO 4: BFF llama a Catalog Service            │
└─────────────────────────────────────────────────┘
BFF → POST http://catalog:8084/products
      (Llamada interna en Docker)

┌─────────────────────────────────────────────────┐
│ PASO 5: Catalog guarda en MongoDB              │
└─────────────────────────────────────────────────┘
Catalog Service:
  1. Recibe datos
  2. Valida (¿precio positivo? ¿stock válido?)
  3. Genera ID: 7
  4. Guarda en MongoDB
  5. Responde: {id: 7, name: "iPhone 15 Pro", ...}

┌─────────────────────────────────────────────────┐
│ PASO 6: BFF registra en Audit                  │
└─────────────────────────────────────────────────┘
BFF → POST http://audit:8083/audit
Body:
  {
    "user": "Bastián Martínez",
    "action": "CREATE_PRODUCT",
    "details": "Creó producto iPhone 15 Pro"
  }

Audit Service guarda en PostgreSQL

┌─────────────────────────────────────────────────┐
│ PASO 7: BFF publica en RabbitMQ                │
└─────────────────────────────────────────────────┘
BFF → RabbitMQ queue "notifications"
Message:
  {
    "type": "NEW_PRODUCT",
    "product": "iPhone 15 Pro",
    "user": "Bastián Martínez"
  }

┌─────────────────────────────────────────────────┐
│ PASO 8: Notify procesa mensaje                 │
└─────────────────────────────────────────────────┘
Notify Service (escuchando RabbitMQ):
  1. Recibe mensaje
  2. Prepara email
  3. Envía a: admin@pedidos360.cl
  4. Asunto: "Nuevo producto: iPhone 15 Pro"

(Esto pasa EN PARALELO, no bloquea al usuario)

┌─────────────────────────────────────────────────┐
│ PASO 9: BFF responde al Frontend               │
└─────────────────────────────────────────────────┘
BFF → Frontend
Response:
  {
    "success": true,
    "product": {
      "id": 7,
      "name": "iPhone 15 Pro",
      "price": 1200000
    }
  }

┌─────────────────────────────────────────────────┐
│ PASO 10: Frontend muestra mensaje              │
└─────────────────────────────────────────────────┘
Frontend muestra:
  "✅ Producto creado exitosamente"

Usuario ve el producto en la lista
```

**TIEMPO TOTAL: < 1 segundo** ⚡

---

## 📊 PATRONES CLOUD-NATIVE IMPLEMENTADOS

### 1. BFF (Backend for Frontend)

**Qué es:** Una capa intermedia entre frontend y microservicios

**Ventajas:**
- Frontend más simple
- Seguridad centralizada
- Orquestación de llamadas

---

### 2. Circuit Breaker

**Qué es:** Protección contra fallos en cascada

**Ventajas:**
- Sistema resiliente
- Respuestas rápidas aunque algo falle
- Auto-recuperación

---

### 3. Event-Driven (Basado en Eventos)

**Qué es:** Comunicación asíncrona con mensajes

**Ventajas:**
- Desacoplamiento
- Escalabilidad
- No bloquea al usuario

---

### 4. Database per Service

**Qué es:** Cada servicio su propia BD

**Ventajas:**
- Independencia
- Fallo aislado
- Tecnología adecuada para cada caso

---

### 5. Containerization (Docker)

**Qué es:** Cada servicio en su contenedor

**Ventajas:**
- Portabilidad
- Aislamiento
- Fácil despliegue

---

### 6. OAuth2/OIDC

**Qué es:** Autenticación externa delegada

**Ventajas:**
- No guardas contraseñas
- Login con Microsoft, Google, etc.
- Seguro y estándar

---

## 🎯 PUNTOS CLAVE PARA LA PRESENTACIÓN

### 1. Inicio: Contexto
"Pedidos360 es un sistema de gestión de pedidos con arquitectura cloud-native, desplegado en AWS."

### 2. Arquitectura General
"Usamos 6 microservicios independientes coordinados por un BFF, cada uno con su propia base de datos."

### 3. Seguridad
"Implementamos OAuth2 con Microsoft Azure AD. Los usuarios se autentican con su cuenta de Microsoft y reciben un TOKEN JWT que valida cada petición."

### 4. BFF
"El BFF es el coordinador principal. Valida tokens, orquesta servicios y tiene Circuit Breaker para proteger contra fallos."

### 5. Microservicios
"Cada microservicio hace una sola cosa:
- Orders: Gestiona pedidos y clientes
- Catalog: Catálogo de productos en MongoDB
- Audit: Registra todos los eventos
- Report: Genera reportes
- Notify: Envía notificaciones"

### 6. RabbitMQ
"Usamos mensajería asíncrona para las notificaciones. El usuario no espera a que se envíe el email."

### 7. Circuit Breaker
"Si un servicio falla, el Circuit Breaker protege al sistema respondiendo con datos alternativos en lugar de fallar completamente."

### 8. Bases de Datos
"Implementamos Database per Service: PostgreSQL para datos relacionales y MongoDB para el catálogo flexible de productos."

### 9. Docker
"Todo está contenedorizado. Con un solo comando levantamos los 9 servicios."

### 10. AWS
"El backend está desplegado en AWS EC2, accesible 24/7 desde Internet."

---

## 💡 TIPS PARA LA DEMO EN VIVO

### Preparación:

1. **Abre el frontend ANTES de la presentación**
2. **Haz login ANTES** (para que ya estés autenticado)
3. **Ten pestañas abiertas:**
   - Frontend (Dashboard)
   - RabbitMQ Management (http://54.242.196.191:15672)
   - Terminal SSH a AWS (por si acaso)

### Flujo de Demo:

**1. Mostrar Dashboard (30 seg)**
- "Aquí vemos las métricas del sistema"
- "6 productos, 4 eventos de auditoría"
- "Usuario autenticado: Bastián Martínez"

**2. Crear Producto (1 min)**
- Ir a "Productos"
- Crear un producto nuevo (ej: "Samsung Galaxy S24")
- Mostrar que aparece en la lista
- Explicar: "Esto se guardó en MongoDB a través del BFF"

**3. Ver Auditoría (30 seg)**
- Ir a "Auditoría"
- Mostrar el evento de creación del producto
- Explicar: "El Audit Service registró esta acción"

**4. Enviar Notificación (1 min)**
- Ir a "Notificaciones"
- Enviar un email de prueba
- Explicar: "Esto se procesó asíncronamente con RabbitMQ"

**5. Mostrar RabbitMQ (30 seg)**
- Abrir RabbitMQ Management UI
- Mostrar las colas
- Explicar: "Aquí se gestionan los mensajes entre servicios"

**6. Cerrar con Circuit Breaker (si preguntan)**
- "Si un servicio falla, el Circuit Breaker protege al sistema"
- Mostrar código si es necesario

---

## ❓ PREGUNTAS FRECUENTES (Posibles del Profe)

### "¿Por qué usaste OAuth2 en vez de un login propio?"

**Respuesta:**  
"Porque es más seguro. Microsoft maneja las contraseñas, no yo. Además, es el estándar en la industria. Servicios como Gmail, Facebook, Twitter usan OAuth2."

---

### "¿Qué pasa si Catalog Service se cae?"

**Respuesta:**  
"El Circuit Breaker detecta el fallo y responde con una lista vacía en lugar de esperar y fallar. El resto del sistema sigue funcionando. Después de 30 segundos, intenta reconectar automáticamente."

---

### "¿Por qué MongoDB para Catalog y PostgreSQL para Orders?"

**Respuesta:**  
"MongoDB es mejor para el catálogo porque los productos tienen atributos variables (un celular tiene especificaciones diferentes a un refrigerador). PostgreSQL es mejor para pedidos porque necesito relaciones entre tablas (pedidos, clientes, pagos)."

---

### "¿Cuál es la diferencia entre síncrono y asíncrono?"

**Respuesta:**  
"Síncrono es como una llamada telefónica: esperas a que la otra persona responda. Asíncrono es como WhatsApp: envías el mensaje y sigues con tu vida, cuando respondan lo ves. Con RabbitMQ hacemos asíncrono para que el usuario no espere."

---

### "¿Cómo funciona el BFF?"

**Respuesta:**  
"El BFF es como un coordinador. El frontend solo habla con él, y él se encarga de:
1. Validar que el usuario tenga permiso
2. Llamar a los microservicios necesarios
3. Juntar las respuestas
4. Proteger contra fallos con Circuit Breaker"

---

### "¿Por qué cada servicio tiene su propia base de datos?"

**Respuesta:**  
"Para independencia. Si la base de datos de Orders falla, Catalog sigue funcionando. Además, cada servicio puede usar la tecnología más adecuada para sus datos."

---

### "¿Qué es Docker y por qué lo usaste?"

**Respuesta:**  
"Docker crea 'cajas' aisladas para cada servicio. Ventajas:
- Funciona igual en mi laptop y en AWS
- Fácil de desplegar: un solo comando levanta todo
- Cada servicio está aislado, si uno falla no afecta a otros"

---

## 📋 CHECKLIST PRE-PRESENTACIÓN

### Día Antes (Martes Noche):

- [ ] Verificar que AWS EC2 esté corriendo
- [ ] Verificar que todos los servicios estén HEALTHY
- [ ] Hacer login en el frontend y probar todo
- [ ] Imprimir este documento en PDF
- [ ] Revisar preguntas frecuentes
- [ ] Practicar demo (cronometrar 5 minutos)

### Día de Presentación (Miércoles):

- [ ] Laptop cargada
- [ ] Internet funcionando
- [ ] Frontend corriendo (localhost:5173)
- [ ] Ya haber hecho login
- [ ] Documento PDF impreso
- [ ] RabbitMQ UI en una pestaña
- [ ] Mente descansada 😴

---

## 🚀 PUNTOS FUERTES DEL PROYECTO

### Cosas que Impresionan:

1. ✅ **OAuth2 REAL con Microsoft** (no es un login falso)
2. ✅ **Desplegado en AWS** (en la nube real, no localhost)
3. ✅ **6 microservicios funcionando** (complejo pero funcional)
4. ✅ **Circuit Breaker implementado** (resiliencia real)
5. ✅ **RabbitMQ con mensajería asíncrona** (patrón avanzado)
6. ✅ **2 tipos de bases de datos** (SQL y NoSQL)
7. ✅ **Todo dockerizado** (profesional)
8. ✅ **Frontend moderno** (React con tema visual atractivo)

---

## 📚 GLOSARIO DE TÉRMINOS

| Término | Significado Simple |
|---------|-------------------|
| **Microservicio** | Un programa pequeño que hace UNA cosa |
| **OAuth2** | Sistema de login con servicios externos |
| **TOKEN JWT** | Credencial digital con fecha de vencimiento |
| **BFF** | Coordinador entre frontend y microservicios |
| **Circuit Breaker** | Protección automática contra fallos |
| **RabbitMQ** | Sistema de mensajes entre programas |
| **Docker** | "Cajas" donde cada servicio vive aislado |
| **AWS EC2** | Servidor virtual en la nube de Amazon |
| **API** | Forma en que los programas se comunican |
| **Endpoint** | Una "función" accesible por HTTP |
| **CORS** | Permiso para que un sitio llame a otro |
| **Asíncrono** | Que no bloquea, pasa en segundo plano |
| **Síncrono** | Que espera respuesta antes de continuar |

---

## 🎓 RESUMEN FINAL (Elevator Pitch)

**Si tienes 30 segundos para explicar el proyecto:**

"Pedidos360 es un sistema de gestión de pedidos con arquitectura de microservicios desplegado en AWS. Usa OAuth2 con Microsoft para autenticación segura, tiene 6 microservicios independientes coordinados por un BFF, implementa Circuit Breaker para resiliencia, RabbitMQ para mensajería asíncrona, y combina PostgreSQL y MongoDB según las necesidades de cada servicio. Todo está contenedorizado con Docker y accesible 24/7 desde la nube."

---

## ✅ FIN DE LA GUÍA

**¡Éxito en tu presentación!** 🎉

Recuerda:
- Habla con confianza
- Muestra el sistema funcionando
- Explica simple, no técnico en exceso
- Si no sabes algo, di "tendría que investigarlo más"

**Mucha suerte el miércoles!** 🍀

---

**Autor:** Bastián Martínez  
**Contacto:** bae.martinez@duocuc.cl  
**GitHub:** https://github.com/ZEETAALOL/cloud-native  
**Fecha:** Septiembre 2026
