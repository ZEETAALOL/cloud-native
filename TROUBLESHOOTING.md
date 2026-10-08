# Troubleshooting - Pedidos360

## ⚠️ Problema: BFF y Notify no pueden conectar con RabbitMQ

**Fecha:** 2025-01-06  
**Estado:** BLOQUEADO  
**Prioridad:** ALTA  
**Tiempo invertido:** ~1.5 horas

---

## 📋 Resumen

Los microservicios BFF (puerto 8080) y Notify (puerto 8086) quedan en estado `unhealthy` al intentar conectarse a RabbitMQ desplegado en una instancia EC2 separada. El resto de microservicios (Orders, Catalog, Audit, Report) funcionan correctamente.

## 🏗️ Arquitectura del Problema

```
EC2-1 (ec2-apps) - 54.221.95.141        EC2-2 (ec2-mq) - 34.239.139.42
┌─────────────────────────────────┐    ┌────────────────────────────┐
│ t2.medium (4 GB RAM)            │    │ t2.micro (1 GB RAM)        │
│                                 │    │                            │
│ ✅ Orders (8081)                │    │ ✅ RabbitMQ                │
│ ✅ Catalog (8084)               │    │    - AMQP: 5672           │
│ ✅ Audit (8083)                 │    │    - Management: 15672     │
│ ✅ Report (8085)                │    │    - Status: HEALTHY       │
│ ✅ PostgreSQL (5432)            │    └────────────────────────────┘
│ ✅ MongoDB (27017)              │              ↑
│                                 │              │
│ ⚠️ BFF (8080) ─────────────────┼──────────────┘ Connection Refused
│ ⚠️ Notify (8086) ──────────────┼──────────────┘ Handshake Timeout
│                                 │
└─────────────────────────────────┘
```

## 🔴 Síntomas

### 1. Estado de contenedores en EC2-1

```bash
$ docker ps
CONTAINER ID   STATUS
bff            (unhealthy)
notify         (unhealthy)
orders         (healthy)
catalog        (healthy)
audit          (healthy)
report         (healthy)
postgres       (healthy)
mongodb        (healthy)
```

### 2. Logs de BFF

```
org.springframework.amqp.AmqpConnectException: java.net.ConnectException: Connection refused
    at org.springframework.amqp.rabbit.support.RabbitExceptionTranslator.convertRabbitAccessException
    at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection
    at org.springframework.amqp.rabbit.connection.CachingConnectionFactory.createConnection
...
Caused by: java.net.ConnectException: Connection refused
    at java.base/sun.nio.ch.Net.pollConnect(Native Method)
    at java.base/sun.nio.ch.Net.pollConnectNow(Net.java:672)
    at java.base/sun.nio.ch.NioSocketImpl.timedFinishConnect(NioSocketImpl.java:542)
    at java.base/sun.nio.ch.NioSocketImpl.connect(NioSocketImpl.java:597)
    at java.base/java.net.SocksSocketImpl.connect(SocksSocketImpl.java:327)
    at java.base/java.net.Socket.connect(Socket.java:633)
    at com.rabbitmq.client.impl.SocketFrameHandlerFactory.create(SocketFrameHandlerFactory.java:61)
    at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1355)
```

### 3. Logs de RabbitMQ (EC2-2)

```
2025-01-06 XX:XX:XX [info] <0.1260.0> accepting AMQP connection <0.1260.0> (172.31.42.221:35506 -> 172.18.0.2:5672)
2025-01-06 XX:XX:XX [warning] <0.1260.0> closing AMQP connection <0.1260.0> (172.31.42.221:35506 -> 172.18.0.2:5672):
{handshake_timeout,handshake}
```

**Análisis:** RabbitMQ SÍ recibe la conexión desde EC2-1 (IP correcta: 172.31.42.221), pero el handshake no se completa a tiempo.

## ✅ Verificaciones Realizadas

### Conectividad de Red

```bash
# Desde EC2-1 hacia EC2-2
$ telnet 172.31.45.236 5672
Trying 172.31.45.236...
Connected to 172.31.45.236.
Escape character is '^]'.
```

✅ **Resultado:** Conexión TCP exitosa. No es un problema de firewall ni security groups.

### Security Groups

**EC2-1 (ec2-apps):**
- Inbound: 0.0.0.0/0 en puertos 8080-8090, 5432, 27017, 22
- Outbound: All traffic

**EC2-2 (ec2-mq):**
- Inbound: 0.0.0.0/0 en puerto 5672 (AMQP), 15672 (Management), 22
- Outbound: All traffic

✅ **Resultado:** Puertos correctamente abiertos.

### Docker Networking

```bash
# En EC2-2
$ docker network ls
NETWORK ID     NAME                DRIVER    SCOPE
a1b2c3d4e5f6   rabbitmq_default    bridge    local

$ docker inspect rabbitmq
"Networks": {
    "rabbitmq_default": {
        "IPAddress": "172.18.0.2",
        ...
    }
}
```

**Problema identificado:** RabbitMQ está en una red Docker bridge interna con IP `172.18.0.2`. Los logs muestran que las conexiones llegan a esta IP interna, no a la IP del host.

### Versiones

- **Spring Boot:** 3.2.0
- **Java:** 17
- **RabbitMQ:** 3.13-management
- **Docker:** 27.4.1
- **Docker Compose:** 2.32.1

## 🔧 Intentos de Solución

### Intento 1: Aumentar timeouts en Spring

**Archivo:** `infra/ec2-apps/docker-compose.yml`

```yaml
environment:
  SPRING_RABBITMQ_HOST: 172.31.45.236
  SPRING_RABBITMQ_PORT: 5672
  SPRING_RABBITMQ_USERNAME: admin
  SPRING_RABBITMQ_PASSWORD: admin123
  SPRING_RABBITMQ_CONNECTION_TIMEOUT: 60000        # ← Agregado
  SPRING_RABBITMQ_REQUESTED_HEARTBEAT: 60          # ← Agregado
```

❌ **Resultado:** Sin cambios. Sigue el mismo error.

### Intento 2: Configurar timeouts en RabbitMQ

**Archivo creado:** `/home/ubuntu/rabbitmq-cluster/rabbitmq.conf` en EC2-2

```conf
handshake_timeout = 60000
channel_max = 2047
heartbeat = 60
```

**Comando ejecutado:**
```bash
docker restart rabbitmq
```

❌ **Resultado:** Sin cambios. RabbitMQ sigue cerrando por `{handshake_timeout,handshake}`.

### Intento 3: Upgrade de instancia EC2-1

**Cambio:** t2.micro (1 GB RAM) → t2.medium (4 GB RAM)

**Razón:** Insuficiente memoria para 8 contenedores Java.

✅ **Resultado parcial:** Orders, Catalog, Audit, Report se recuperaron.  
❌ **Resultado final:** BFF y Notify siguen sin conectar a RabbitMQ.

### Intento 4: Verificar Docker bridge

**Comando en EC2-2:**
```bash
docker inspect rabbitmq | grep IPAddress
"IPAddress": "172.18.0.2"
```

**Diagnóstico:** El problema está en el networking de Docker. RabbitMQ escucha en `172.18.0.2` (IP del contenedor en red bridge), pero el puerto 5672 está mapeado al host.

**Teoría:** El cliente Java de Spring AMQP intenta conectarse al host `172.31.45.236:5672`, Docker mapea el puerto, pero algo en el handshake SSL/TLS o en la capa de aplicación falla.

## 🤔 Hipótesis

### Hipótesis 1: Docker Bridge Networking
El mapeo de puertos Docker bridge (`172.31.45.236:5672` → `172.18.0.2:5672`) funciona para TCP, pero el protocolo AMQP tiene un handshake complejo que puede fallar debido a latencia o configuración de red Docker.

### Hipótesis 2: Timeout demasiado estricto
A pesar de haber aumentado los timeouts a 60 segundos, el handshake AMQP requiere múltiples round-trips y puede estar excediendo el límite en redes Docker bridge.

### Hipótesis 3: Versión incompatible de cliente
Spring Boot 3.2.0 usa una versión del cliente RabbitMQ que puede tener problemas conocidos con timeouts o networking.

### Hipótesis 4: Falta configuración Spring AMQP
Puede que Spring AMQP requiera configuración adicional para trabajar con RabbitMQ en instancias remotas (ej: `spring.rabbitmq.template.retry`, `spring.rabbitmq.listener.simple.retry`).

## 🎯 Próximos Pasos Recomendados

### Opción A: Host networking (rápido)
Cambiar RabbitMQ a modo host networking en lugar de bridge:

```yaml
# docker-compose.yml en EC2-2
services:
  rabbitmq:
    network_mode: "host"
    # Quitar ports: ya que host mode expone directamente
```

**Ventajas:** Elimina la capa de NAT de Docker  
**Desventajas:** Menos aislamiento

### Opción B: RabbitMQ en la misma instancia (más rápido)
Mover RabbitMQ a EC2-1 para eliminar networking entre instancias:

```yaml
# docker-compose.yml en EC2-1
services:
  rabbitmq:
    image: rabbitmq:3.13-management
    ports:
      - "5672:5672"
      - "15672:15672"
```

**Ventajas:** Elimina problemas de networking inter-instancia  
**Desventajas:** Mayor consumo de RAM en EC2-1

### Opción C: Configuración Spring AMQP avanzada
Agregar más configuración a `application.yml` de BFF y Notify:

```yaml
spring:
  rabbitmq:
    host: 172.31.45.236
    port: 5672
    username: admin
    password: admin123
    connection-timeout: 60000
    requested-heartbeat: 60
    template:
      retry:
        enabled: true
        initial-interval: 2s
        max-attempts: 3
        max-interval: 10s
    listener:
      simple:
        retry:
          enabled: true
          initial-interval: 2s
          max-attempts: 3
```

### Opción D: Downgrade Spring Boot
Probar con Spring Boot 2.7.x que tiene versión más antigua (y posiblemente más estable) del cliente RabbitMQ.

### Opción E: Investigar logs detallados
Habilitar DEBUG logging en Spring AMQP:

```yaml
logging:
  level:
    org.springframework.amqp: DEBUG
    com.rabbitmq: DEBUG
```

## 📚 Referencias

- [Spring AMQP Connection Configuration](https://docs.spring.io/spring-amqp/reference/amqp/connections.html)
- [RabbitMQ Timeouts](https://www.rabbitmq.com/docs/networking#timeouts)
- [Docker Bridge Networking](https://docs.docker.com/engine/network/drivers/bridge/)

## 🏷️ Tags

`#rabbitmq` `#spring-boot` `#docker-networking` `#aws-ec2` `#connection-refused` `#handshake-timeout`

---

**Última actualización:** 2025-01-06  
**Responsable:** Bastián Martínez
