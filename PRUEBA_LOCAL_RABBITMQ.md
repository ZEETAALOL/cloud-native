# 🧪 Prueba Local de RabbitMQ

## Objetivo
Probar la integración RabbitMQ localmente antes de desplegar a EC2, siguiendo la guía oficial del profesor.

---

## Paso 1: Verificar requisitos

```powershell
# En PowerShell
docker --version    # Docker version 27.x.x o superior
java -version       # openjdk version "17" o superior
mvn -version        # Apache Maven 3.8+
```

✅ **Checkpoint 1:** Los tres comandos responden sin error.

---

## Paso 2: Levantar RabbitMQ local

```powershell
# Desde la raíz del proyecto
cd infra/docker

# Levantar RabbitMQ
docker compose -f docker-compose-local.yml up -d

# Verificar estado (debe quedar "healthy" en ~30 segundos)
docker compose -f docker-compose-local.yml ps
```

### Verificar RabbitMQ está listo

**Método 1: Logs**
```powershell
docker logs rabbitmq | Select-String "ready to accept"
```

**Método 2: Dashboard**
Abre http://localhost:15672
- Usuario: `admin`
- Password: `admin123`

**Método 3: Diagnóstico interno**
```powershell
docker exec rabbitmq rabbitmq-diagnostics ping
# → debe responder: Ping succeeded
```

✅ **Checkpoint 2:** Ves el dashboard de RabbitMQ en el navegador.

📸 **Captura de pantalla:** Dashboard con login exitoso.

---

## Paso 3: Compilar microservicio Notify

```powershell
# Desde la raíz del proyecto
cd backend/notify

# Compilar
mvn clean package

# Verificar
dir target\*.jar
# Debe aparecer: ms-pedidos360-notify-1.0.0.jar
```

✅ **Checkpoint 3:** `mvn clean package` termina en **BUILD SUCCESS**.

---

## Paso 4: Ejecutar microservicio Notify

```powershell
# Asegúrate de estar en backend/notify
java -jar target/ms-pedidos360-notify-1.0.0.jar
```

### ¿Qué debe aparecer en los logs?

```
Started NotifyApplication in X.XXX seconds
Cola 'q.cmd.email' lista para recibir mensajes
RabbitMQ conectado en localhost:5672
```

✅ **Checkpoint 4:** El servicio arranca sin errores de conexión a RabbitMQ.

📸 **Captura de pantalla:** Logs mostrando conexión exitosa.

---

## Paso 5: Verificar consumidor en RabbitMQ

### En el Dashboard (http://localhost:15672):

1. Ve a **Queues and Streams**
2. Debe aparecer la cola `q.cmd.email`
3. Verifica que **Consumers = 1**

✅ **Checkpoint 5:** La cola `q.cmd.email` aparece con **Consumers = 1**.

📸 **Captura de pantalla:** Dashboard mostrando cola con 1 consumer.

---

## Paso 6: Enviar mensaje de prueba desde el Dashboard

### Método A: Desde la UI de RabbitMQ

1. En el Dashboard, ve a **Queues** → `q.cmd.email` → **Publish message**
2. En **Payload**, pega este JSON:

```json
{
  "type": "ORDER_CREATED",
  "recipient": "cliente@example.com",
  "subject": "Pedido Confirmado",
  "body": "Tu pedido #12345 ha sido confirmado",
  "orderId": "12345",
  "timestamp": "2025-01-06T23:45:00"
}
```

3. Click en **Publish message**

### ¿Qué debe pasar?

En la consola de Notify debes ver:

```
========================================
📧 MENSAJE RECIBIDO DE RABBITMQ
========================================
Destinatario: cliente@example.com
Asunto: Pedido Confirmado
Cuerpo: Tu pedido #12345 ha sido confirmado
Order ID: 12345
Timestamp: 2025-01-06T23:45:00
========================================
✅ Email enviado exitosamente a: cliente@example.com
```

✅ **Checkpoint 6 (EL MÁS IMPORTANTE):** Ves el mensaje recibido y procesado en los logs de Notify.

📸 **Captura de pantalla:** Logs mostrando mensaje recibido.

---

## Paso 7: Probar con BFF (Completo)

### 7.1 Compilar BFF

```powershell
cd backend/bff
mvn clean package
```

### 7.2 Ejecutar BFF

```powershell
# Configurar variables de entorno necesarias
$env:ENTRA_ISSUER_URI="https://login.microsoftonline.com/47c2bee0-5950-430f-9276-bfc083e3d1da/v2.0"
$env:ENTRA_API_CLIENT_ID="faba8741-ba0d-440c-b061-f1aa893eb957"

# Ejecutar
java -jar target/ms-pedidos360-bff-1.0.0.jar
```

### 7.3 Probar endpoint de mensajería

**En otra terminal PowerShell:**

```powershell
# Enviar mensaje de prueba
Invoke-RestMethod -Uri "http://localhost:8080/api/messaging/send-email" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{
    "recipient": "juan@example.com",
    "subject": "Pedido #67890 Creado",
    "body": "Tu pedido ha sido registrado exitosamente",
    "orderId": "67890"
  }'
```

### ¿Qué debe pasar?

1. BFF responde con confirmación
2. En logs de BFF:
   ```
   📧 Mensaje de email enviado a RabbitMQ para orderId: 67890
   ```

3. En logs de Notify:
   ```
   📧 MENSAJE RECIBIDO DE RABBITMQ
   Destinatario: juan@example.com
   ...
   ✅ Email enviado exitosamente a: juan@example.com
   ```

✅ **Checkpoint 7:** El mensaje viaja de BFF → RabbitMQ → Notify correctamente.

📸 **Captura de pantalla:** Logs de ambos servicios mostrando el flujo completo.

---

## Paso 8: Verificar en Dashboard

En http://localhost:15672 → **Queues** → `q.cmd.email`:

- **Messages ready:** debe bajar a 0 (todos consumidos)
- En **Message Rates**: debes ver el gráfico de mensajes publicados y consumidos

---

## 🔧 Solución de problemas

### Error: "Connection refused on localhost:5672"

```powershell
# Verificar que RabbitMQ esté corriendo
docker ps | Select-String rabbitmq

# Si no está, levantarlo
docker compose -f docker-compose-local.yml up -d

# Verificar logs
docker logs rabbitmq

# Esperar hasta que esté healthy
docker compose -f docker-compose-local.yml ps
```

### Error: "No se reciben mensajes"

1. Verificar que Notify tenga Consumers = 1 en el dashboard
2. Revisar que el JSON del mensaje sea válido
3. Verificar logs de Notify para ver errores
4. Recompilar con `mvn clean package`

### Error: "El puerto 8080 está ocupado"

Cambiar puerto en `application.yml`:
```yaml
server:
  port: 8081
```

---

## ✅ Checklist Final

- [ ] RabbitMQ healthy en Docker
- [ ] Dashboard accesible en localhost:15672
- [ ] Notify compila sin errores
- [ ] Notify arranca y conecta a RabbitMQ
- [ ] Cola `q.cmd.email` aparece con Consumers = 1
- [ ] Mensaje desde Dashboard llega a Notify
- [ ] BFF compila sin errores
- [ ] BFF arranca y conecta a RabbitMQ
- [ ] Exchange `cmd.direct` y binding creados
- [ ] Mensaje desde BFF llega a Notify
- [ ] **Capturas de pantalla tomadas**

---

## 📚 Diferencias con la guía del profesor

| Aspecto | Guía Profesor | Nuestro Proyecto |
|---------|---------------|------------------|
| Cola | `hello` | `q.cmd.email` |
| Exchange | Por defecto ("") | `cmd.direct` explícito |
| Routing Key | N/A (cola directa) | `notify.email` |
| Durable | false | false (dev) / true (prod) |
| DTO | String simple | `EmailMessage` complejo |
| Productor | `Sender` con menú | `MessagingService` + REST API |
| Consumidor | `Receiver` simple | `EmailListener` con simulación |

---

## 🎯 Siguientes pasos

Una vez que todo funcione localmente:

1. ✅ Commit y push de cambios
2. ✅ Compilar JARs finales
3. ✅ Transferir a EC2
4. ✅ Configurar variables de entorno para EC2-2 (IP privada RabbitMQ)
5. ✅ Reiniciar servicios en EC2-1
6. ✅ Verificar en producción

---

**Última actualización:** 2025-01-06  
**Basado en:** Guía 2.1.2 RabbitMQ HelloWorld (Profesor DSY1107)
