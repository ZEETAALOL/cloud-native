# EC2-1: ec2-apps - Microservicios Backend + Bases de Datos

Esta es la **primera instancia EC2** que contiene todos los microservicios backend y las bases de datos.

## 📦 Componentes

### Bases de Datos
- **PostgreSQL** (Puerto 5432) - Orders, Catalog, Report
- **MongoDB** (Puerto 27017) - Audit, Notifications

### Microservicios
- **BFF** (Puerto 8080) - Backend For Frontend
- **Orders** (Puerto 8081) - Gestión de pedidos
- **Catalog** (Puerto 8084) - Catálogo de productos
- **Audit** (Puerto 8083) - Auditoría y timeline
- **Report** (Puerto 8085) - KPIs y reportes
- **Notify** (Puerto 8086) - Notificaciones

---

## 🚀 Crear Instancia EC2 en AWS

### Paso 1: Lanzar Instancia

```
1. Ir a AWS Console → EC2 → Launch Instance
2. Nombre: ec2-apps-pedidos360
3. AMI: Ubuntu Server 22.04 LTS (HVM), SSD Volume Type
4. Tipo de instancia: t3.medium (2 vCPU, 4 GB RAM)
5. Key pair: Crear nueva o usar existente
6. Network settings:
   - VPC: Default o tu VPC
   - Auto-assign public IP: Enable
   - Security Group: Crear nuevo "pedidos360-apps-sg"
```

### Paso 2: Configurar Security Group

**Inbound Rules:**
```
Type            Protocol    Port Range    Source          Description
SSH             TCP         22            Mi IP           SSH access
Custom TCP      TCP         8080          0.0.0.0/0       BFF (Frontend)
Custom TCP      TCP         8081          VPC only        Orders
Custom TCP      TCP         8083          VPC only        Audit
Custom TCP      TCP         8084          VPC only        Catalog
Custom TCP      TCP         8085          VPC only        Report
Custom TCP      TCP         8086          VPC only        Notify
PostgreSQL      TCP         5432          VPC only        PostgreSQL
MongoDB         TCP         27017         VPC only        MongoDB
```

### Paso 3: Storage

```
Root volume: 30 GB gp3
```

### Paso 4: Launch

```
Revisar y Launch
```

---

## 🔧 Configurar Instancia (Conectar via SSH)

### 1. Conectar

```bash
ssh -i "tu-clave.pem" ubuntu@<EC2-PUBLIC-IP>
```

### 2. Actualizar Sistema

```bash
sudo apt update && sudo apt upgrade -y
```

### 3. Instalar Docker

```bash
# Instalar Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Agregar usuario al grupo docker
sudo usermod -aG docker ubuntu

# Instalar Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verificar
docker --version
docker-compose --version

# IMPORTANTE: Logout y login nuevamente para aplicar permisos
exit
```

```bash
# Reconectar
ssh -i "tu-clave.pem" ubuntu@<EC2-PUBLIC-IP>
```

### 4. Instalar Git

```bash
sudo apt install git -y
```

### 5. Clonar Repositorio

```bash
git clone https://github.com/TU-USUARIO/proyecto_cloudnative_ev1.git
cd proyecto_cloudnative_ev1
```

---

## 📝 Configuración Antes de Desplegar

### 1. Actualizar IPs en docker-compose.yml

```bash
cd infra/ec2-apps
nano docker-compose.yml
```

**Reemplazar:**
- `ec2-kafka-ip` → IP privada de EC2-3 (ej: 10.0.1.50)
- `ec2-mq-ip` → IP privada de EC2-2 (ej: 10.0.1.40)

### 2. Variables de Entorno (opcional)

```bash
# Crear archivo .env si necesitas
cat > .env << EOF
POSTGRES_PASSWORD=admin123
MONGO_PASSWORD=admin123
AZURE_TENANT_ID=47c2bee0-5950-430f-9276-bfc083e3d1da
AZURE_CLIENT_ID=faba8741-ba0d-440c-b061-f1aa893eb957
EOF
```

---

## 🚀 Desplegar

### Opción 1: Build y Deploy en un solo comando

```bash
cd ~/proyecto_cloudnative_ev1/infra/ec2-apps

# Build de todas las imágenes
docker-compose build

# Levantar todo
docker-compose up -d

# Ver logs
docker-compose logs -f
```

### Opción 2: Build paso a paso (si falla algo)

```bash
# Build individual
docker-compose build postgres
docker-compose build mongodb
docker-compose build orders
docker-compose build catalog
docker-compose build audit
docker-compose build report
docker-compose build notify
docker-compose build bff

# Levantar por fases
docker-compose up -d postgres mongodb
sleep 20  # Esperar que arranquen las DBs

docker-compose up -d orders catalog audit report notify
sleep 30  # Esperar que arranquen los microservicios

docker-compose up -d bff
```

---

## ✅ Verificar Deployment

### 1. Ver Estado de Contenedores

```bash
docker ps
```

Deberías ver 8 contenedores corriendo:
- pedidos360-postgres
- pedidos360-mongodb
- pedidos360-orders
- pedidos360-catalog
- pedidos360-audit
- pedidos360-report
- pedidos360-notify
- pedidos360-bff

### 2. Health Checks

```bash
# BFF
curl http://localhost:8080/actuator/health

# Orders
curl http://localhost:8081/actuator/health

# Catalog
curl http://localhost:8084/actuator/health

# Audit
curl http://localhost:8083/actuator/health

# Report
curl http://localhost:8085/actuator/health

# Notify
curl http://localhost:8086/actuator/health
```

### 3. Verificar DBs

```bash
# PostgreSQL
docker exec -it pedidos360-postgres psql -U admin -d pedidos360 -c "SELECT COUNT(*) FROM products;"

# MongoDB
docker exec -it pedidos360-mongodb mongosh -u admin -p admin123 --authenticationDatabase admin --eval "db.getSiblingDB('pedidos360').audit_events.countDocuments()"
```

### 4. Ver Logs

```bash
# Todos
docker-compose logs -f

# Específico
docker-compose logs -f bff
docker-compose logs -f orders
```

---

## 🌐 Acceso Desde el Frontend

Desde tu máquina local (donde corre el frontend):

```bash
# Actualizar frontend/src/authConfig.js o similar
export const API_BASE_URL = "http://<EC2-PUBLIC-IP>:8080";
```

**Probar:**
```bash
curl http://<EC2-PUBLIC-IP>:8080/actuator/health
```

---

## 🔄 Actualizar Código

```bash
cd ~/proyecto_cloudnative_ev1

# Pull cambios
git pull origin main

# Rebuild servicios modificados
cd infra/ec2-apps
docker-compose build orders  # o el que cambiaste
docker-compose up -d orders

# Ver logs
docker-compose logs -f orders
```

---

## 🛑 Detener Servicios

```bash
# Detener todos
docker-compose down

# Detener y eliminar volúmenes (⚠️ BORRA DATOS)
docker-compose down -v
```

---

## 🐛 Troubleshooting

### Error: Puerto ya en uso

```bash
# Ver qué proceso usa el puerto
sudo lsof -i :8080

# Matar proceso
sudo kill -9 <PID>
```

### Error: Out of memory

```bash
# Ver uso de memoria
free -h

# Ver uso de Docker
docker stats

# Aumentar swap
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

### Error: Cannot connect to Docker daemon

```bash
sudo systemctl start docker
sudo systemctl enable docker
```

### Limpiar espacio en disco

```bash
# Limpiar imágenes no usadas
docker system prune -a

# Limpiar volúmenes no usados
docker volume prune
```

---

## 📊 Monitoreo

### Ver recursos

```bash
# CPU, RAM por contenedor
docker stats

# Logs en tiempo real
docker-compose logs -f --tail=100

# Espacio en disco
df -h
```

### PostgreSQL

```bash
# Conectar
docker exec -it pedidos360-postgres psql -U admin -d pedidos360

# Queries útiles
SELECT * FROM products LIMIT 10;
SELECT * FROM orders ORDER BY created_at DESC LIMIT 5;
```

### MongoDB

```bash
# Conectar
docker exec -it pedidos360-mongodb mongosh -u admin -p admin123 --authenticationDatabase admin

# Queries útiles
use pedidos360
db.audit_events.find().sort({timestamp:-1}).limit(10)
```

---

## 🎯 URLs Importantes

- **BFF (API):** http://<EC2-PUBLIC-IP>:8080
- **Swagger Orders:** http://<EC2-PUBLIC-IP>:8081/swagger-ui.html
- **Swagger Catalog:** http://<EC2-PUBLIC-IP>:8084/swagger-ui.html
- **Health BFF:** http://<EC2-PUBLIC-IP>:8080/actuator/health

---

## ✅ Checklist de Deployment

- [ ] Instancia EC2 creada (t3.medium, Ubuntu 22.04)
- [ ] Security Group configurado
- [ ] Docker y Docker Compose instalados
- [ ] Repositorio clonado
- [ ] IPs actualizadas en docker-compose.yml
- [ ] docker-compose build exitoso
- [ ] docker-compose up -d ejecutado
- [ ] 8 contenedores corriendo
- [ ] Health checks respondiendo OK
- [ ] PostgreSQL con datos de ejemplo
- [ ] MongoDB con eventos de ejemplo
- [ ] Frontend puede conectarse al BFF

---

**¡EC2-1 lista!** 🎉

Siguiente: **EC2-2 (ec2-mq - RabbitMQ Cluster)**
