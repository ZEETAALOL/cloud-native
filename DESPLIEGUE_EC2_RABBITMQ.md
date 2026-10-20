# 🚀 Despliegue RabbitMQ en EC2

## Prerequisitos

✅ Has probado localmente y funciona (ver [PRUEBA_LOCAL_RABBITMQ.md](./PRUEBA_LOCAL_RABBITMQ.md))  
✅ EC2-1 (ec2-apps) está corriendo: IP Pública `54.221.95.141`  
✅ EC2-2 (ec2-mq) está corriendo: IP Privada `172.31.45.236`  
✅ RabbitMQ está healthy en EC2-2

---

## Paso 1: Compilar JARs actualizados localmente

```powershell
# Desde la raíz del proyecto

# Compilar BFF
cd backend/bff
mvn clean package -DskipTests
cd ../..

# Compilar Notify
cd backend/notify
mvn clean package -DskipTests
cd ../..
```

Verifica que se generaron:
- `backend/bff/target/ms-pedidos360-bff-1.0.0.jar`
- `backend/notify/target/ms-pedidos360-notify-1.0.0.jar`

---

## Paso 2: Subir código actualizado a EC2-1

### Opción A: Git pull (más fácil)

```bash
# Conectarse a EC2-1
ssh -i tu-clave.pem ubuntu@54.221.95.141

# Navegar al proyecto
cd ~/proyecto_cloudnative_ev1

# Pull de cambios
git pull origin main

# Compilar en EC2
cd backend/bff && mvn clean package -DskipTests && cd ../..
cd backend/notify && mvn clean package -DskipTests && cd ../..
```

### Opción B: SCP (si no quieres compilar en EC2)

```powershell
# Desde tu máquina local
scp -i tu-clave.pem backend/bff/target/ms-pedidos360-bff-1.0.0.jar ubuntu@54.221.95.141:~/proyecto_cloudnative_ev1/backend/bff/target/

scp -i tu-clave.pem backend/notify/target/ms-pedidos360-notify-1.0.0.jar ubuntu@54.221.95.141:~/proyecto_cloudnative_ev1/backend/notify/target/
```

---

## Paso 3: Actualizar docker-compose.yml en EC2-1

Conéctate a EC2-1:

```bash
ssh -i tu-clave.pem ubuntu@54.221.95.141
```

Edita el archivo:

```bash
nano ~/proyecto_cloudnative_ev1/infra/ec2-apps/docker-compose.yml
```

**Cambios importantes en la sección de `bff`:**

```yaml
bff:
  build:
    context: ../../backend/bff
    dockerfile: Dockerfile
  container_name: bff
  ports:
    - "8080:8080"
  environment:
    # OAuth2
    ENTRA_ISSUER_URI: https://login.microsoftonline.com/47c2bee0-5950-430f-9276-bfc083e3d1da/v2.0
    ENTRA_API_CLIENT_ID: faba8741-ba0d-440c-b061-f1aa893eb957
    
    # Microservices
    MICROSERVICES_CLIENTES_BASE_URL: http://orders:8081
    MICROSERVICES_AUDIT_BASE_URL: http://audit:8083
    MICROSERVICES_CATALOG_BASE_URL: http://catalog:8084
    MICROSERVICES_REPORT_BASE_URL: http://report:8085
    MICROSERVICES_NOTIFY_BASE_URL: http://notify:8086
    
    # RabbitMQ - IMPORTANTE: Usar IP PRIVADA de EC2-2
    SPRING_RABBITMQ_HOST: 172.31.45.236
    SPRING_RABBITMQ_PORT: 5672
    SPRING_RABBITMQ_USERNAME: admin
    SPRING_RABBITMQ_PASSWORD: admin123
  depends_on:
    postgres:
      condition: service_healthy
    mongodb:
      condition: service_healthy
  networks:
    - app-network
  healthcheck:
    test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
    interval: 30s
    timeout: 10s
    retries: 5
```

**Cambios importantes en la sección de `notify`:**

```yaml
notify:
  build:
    context: ../../backend/notify
    dockerfile: Dockerfile
  container_name: notify
  ports:
    - "8086:8086"
  environment:
    # RabbitMQ - IMPORTANTE: Usar IP PRIVADA de EC2-2
    SPRING_RABBITMQ_HOST: 172.31.45.236
    SPRING_RABBITMQ_PORT: 5672
    SPRING_RABBITMQ_USERNAME: admin
    SPRING_RABBITMQ_PASSWORD: admin123
  networks:
    - app-network
  healthcheck:
    test: ["CMD", "curl", "-f", "http://localhost:8086/actuator/health"]
    interval: 30s
    timeout: 10s
    retries: 5
```

Guardar: `Ctrl+O` → Enter → `Ctrl+X`

---

## Paso 4: Reconstruir y reiniciar servicios

```bash
cd ~/proyecto_cloudnative_ev1/infra/ec2-apps

# Detener todo
docker-compose down

# Limpiar imágenes viejas (opcional)
docker image prune -f

# Reconstruir y levantar
docker-compose up -d --build

# Ver logs en tiempo real
docker-compose logs -f bff notify
```

---

## Paso 5: Verificar conectividad

### 5.1 Verificar que los servicios arranquen

```bash
# Ver estado
docker-compose ps

# BFF y Notify deben estar "healthy" después de ~1 minuto
```

### 5.2 Verificar logs de conexión a RabbitMQ

```bash
# Logs de BFF
docker logs bff | grep -i rabbitmq

# Logs de Notify
docker logs notify | grep -i rabbitmq
```

**Debes ver:**
```
RabbitMQ conectado en 172.31.45.236:5672
Cola 'q.cmd.email' lista para recibir mensajes
```

**NO debes ver:**
```
Connection refused
handshake_timeout
```

---

## Paso 6: Verificar en RabbitMQ Management

Desde tu navegador local:

```
http://34.239.139.42:15672
Usuario: admin
Password: admin123
```

1. Ve a **Queues and Streams**
2. Debe aparecer:
   - `q.cmd.email` con **Consumers = 1**

3. Ve a **Exchanges**
4. Debe aparecer:
   - `cmd.direct` de tipo `direct`

5. Ve a **Connections**
6. Deben aparecer 2 conexiones:
   - Una desde BFF (172.31.42.221)
   - Una desde Notify (172.31.42.221)

📸 **Captura:** Dashboard mostrando cola y consumers.

---

## Paso 7: Probar end-to-end en EC2

### 7.1 Enviar mensaje desde tu máquina local

```powershell
# Desde PowerShell en tu máquina
Invoke-RestMethod -Uri "http://54.221.95.141:8080/api/messaging/send-email" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{
    "recipient": "cliente@example.com",
    "subject": "Pedido #99999 Creado",
    "body": "Tu pedido ha sido confirmado en producción",
    "orderId": "99999"
  }'
```

### 7.2 Ver logs en EC2-1

```bash
# Logs de BFF (debe mostrar mensaje enviado)
docker logs bff --tail 20

# Logs de Notify (debe mostrar mensaje recibido)
docker logs notify --tail 20
```

**Salida esperada en logs de Notify:**

```
========================================
📧 MENSAJE RECIBIDO DE RABBITMQ
========================================
Destinatario: cliente@example.com
Asunto: Pedido #99999 Creado
Cuerpo: Tu pedido ha sido confirmado en producción
Order ID: 99999
========================================
✅ Email enviado exitosamente a: cliente@example.com
```

✅ **CHECKPOINT FINAL:** El mensaje viaja de tu máquina → BFF (EC2-1) → RabbitMQ (EC2-2) → Notify (EC2-1).

📸 **Captura:** Logs de ambos servicios mostrando el flujo completo.

---

## Paso 8: Integrar con Orders

Ahora que RabbitMQ funciona, podemos hacer que Orders envíe notificaciones automáticamente.

### 8.1 Actualizar Orders para enviar mensajes

Edita `backend/orders/src/main/java/cl/duoc/orders/service/OrderService.java`

Agrega después de crear un pedido:

```java
// Enviar notificación por RabbitMQ
messagingService.sendEmailNotification(
    order.getCustomerEmail(),
    "Pedido Confirmado - #" + order.getId(),
    "Tu pedido ha sido registrado exitosamente",
    order.getId().toString()
);
```

---

## 🔧 Solución de problemas en EC2

### Error: "Connection refused" en EC2

```bash
# Verificar que RabbitMQ en EC2-2 esté escuchando
ssh ubuntu@34.239.139.42
docker ps | grep rabbitmq
docker logs rabbitmq | tail -20

# Probar conectividad desde EC2-1
ssh ubuntu@54.221.95.141
telnet 172.31.45.236 5672
# Debe conectar
```

### Error: "handshake_timeout" persiste

Si después de estos cambios aún ves el error, el problema puede ser:

1. **Versión incorrecta de cliente RabbitMQ**
   
   Verifica en `pom.xml` que tengas:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-amqp</artifactId>
   </dependency>
   ```

2. **Virtual host incorrecto**
   
   Confirma en `application.yml`:
   ```yaml
   spring:
     rabbitmq:
       virtual-host: /
   ```

3. **Credenciales incorrectas**
   
   En RabbitMQ Management, ve a **Admin** → **Users** y confirma que `admin/admin123` existe.

### BFF o Notify quedan unhealthy

```bash
# Ver logs detallados
docker logs bff --tail 100
docker logs notify --tail 100

# Ver health check
curl http://localhost:8080/actuator/health
curl http://localhost:8086/actuator/health
```

---

## ✅ Checklist de despliegue

- [ ] JARs compilados localmente con cambios
- [ ] Código subido a EC2-1 (git pull o scp)
- [ ] docker-compose.yml actualizado con IP privada de RabbitMQ
- [ ] Servicios reconstruidos con `docker-compose up -d --build`
- [ ] BFF y Notify en estado `healthy`
- [ ] Cola `q.cmd.email` visible en RabbitMQ con Consumers = 1
- [ ] Exchange `cmd.direct` creado
- [ ] 2 conexiones activas desde EC2-1 a EC2-2
- [ ] Mensaje de prueba enviado y recibido correctamente
- [ ] Logs muestran flujo completo sin errores
- [ ] Capturas de pantalla tomadas

---

## 📊 Arquitectura final

```
┌─────────────────────────────────────────────────────────────────┐
│ TU MÁQUINA LOCAL (54.xxx.xxx.xxx desde AWS)                   │
│                                                                 │
│  [POST /api/messaging/send-email]                              │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ↓ HTTP
┌─────────────────────────────────────────────────────────────────┐
│ EC2-1 (ec2-apps) - 54.221.95.141 / 172.31.42.221              │
│                                                                 │
│  ┌─────────────┐         ┌──────────────────────────┐         │
│  │ BFF (8080)  │────────>│ MessagingService         │         │
│  │             │         │ RabbitTemplate.send()    │         │
│  └─────────────┘         └────────┬─────────────────┘         │
│                                   │                             │
│  ┌─────────────┐                  │                            │
│  │ Notify      │<─────────────────┘                            │
│  │ (8086)      │  AMQP (via EC2-2)                             │
│  │ @RabbitListener                                             │
│  └─────────────┘                                               │
└───────────────────────────┬─────────────────────────────────────┘
                            │ AMQP over TCP
                            │ 172.31.42.221 → 172.31.45.236:5672
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ EC2-2 (ec2-mq) - 34.239.139.42 / 172.31.45.236                │
│                                                                 │
│  ┌──────────────────────────────────────────┐                 │
│  │ RabbitMQ 3.13-management                 │                 │
│  │                                           │                 │
│  │  Exchange: cmd.direct                    │                 │
│  │      ↓ routing key: notify.email         │                 │
│  │  Queue: q.cmd.email                      │                 │
│  │      → Consumers: 1 (Notify)             │                 │
│  │                                           │                 │
│  │  Management UI: :15672 (admin/admin123)  │                 │
│  └──────────────────────────────────────────┘                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🎯 Próximos pasos

Una vez funcionando:

1. ✅ Cambiar `durable=false` a `durable=true` en RabbitMQConfig (producción)
2. ✅ Agregar DLQ (Dead Letter Queue) para mensajes fallidos
3. ✅ Implementar retry con backoff exponencial
4. ✅ Integrar Orders para que envíe notificaciones automáticas
5. ✅ Preparar integración Kafka para eventos de auditoría

---

**Última actualización:** 2025-01-06  
**Basado en:** Guía oficial del profesor + arquitectura EC2 multi-instancia
