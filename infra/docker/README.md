# Docker - Pedidos360

Configuración Docker Compose para ejecutar todos los microservicios.

## 📁 Archivos

- `docker-compose.yml` - Definición de servicios (BFF + Orders)
- `.env.example` - Template de variables de entorno
- `rabbitmq.yml` - RabbitMQ (próximamente)
- `kafka.yml` - Kafka + Zookeeper (próximamente)

## 🚀 Inicio Rápido

### 1. Configurar Variables de Entorno

```bash
# Copiar template
cp .env.example .env

# Editar .env con tus valores (opcional, ya tiene valores por defecto)
```

### 2. Construir y Levantar Servicios

```bash
# Construir imágenes y levantar servicios
docker-compose up -d --build

# Ver logs
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f bff
docker-compose logs -f orders
```

### 3. Verificar que Funcionan

```bash
# Orders (debe devolver datos de Wacoldo)
curl http://localhost:8081/clientes

# BFF Health
curl http://localhost:8080/actuator/health
```

### 4. Probar con Frontend

El frontend debe estar configurado para apuntar a:
```env
VITE_BFF_BASE_URL=http://localhost:8080
```

Luego:
1. `cd frontend && npm run dev`
2. Abrir http://localhost:5173
3. Login con Azure AD
4. Consultar datos

## 🛠️ Comandos Útiles

### Ver Servicios Corriendo
```bash
docker-compose ps
```

### Detener Servicios
```bash
docker-compose down
```

### Detener y Eliminar Volúmenes
```bash
docker-compose down -v
```

### Reconstruir una Imagen
```bash
# Reconstruir solo BFF
docker-compose build bff

# Reconstruir todo
docker-compose build
```

### Ver Logs en Tiempo Real
```bash
docker-compose logs -f
```

### Acceder a un Contenedor
```bash
# Entrar a BFF
docker-compose exec bff sh

# Entrar a Orders
docker-compose exec orders sh
```

## 🏗️ Arquitectura Docker

```
┌─────────────────────────────────────┐
│  Docker Network: pedidos360-network │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  pedidos360-orders           │  │
│  │  Puerto: 8081                │  │
│  │  Imagen: pedidos360/orders   │  │
│  └──────────────────────────────┘  │
│              ↑                      │
│              │                      │
│  ┌──────────────────────────────┐  │
│  │  pedidos360-bff              │  │
│  │  Puerto: 8080                │  │
│  │  Imagen: pedidos360/bff      │  │
│  └──────────────────────────────┘  │
│              ↑                      │
└──────────────┼──────────────────────┘
               │
        Frontend (local)
        Puerto: 5173
```

## 🔍 Health Checks

Ambos servicios tienen health checks configurados:

- **Orders:** `GET /clientes` cada 30s
- **BFF:** `GET /actuator/health` cada 30s

Docker reiniciará automáticamente los servicios si fallan los health checks.

## 🐛 Troubleshooting

### Error: "Cannot connect to the Docker daemon"
```bash
# Verificar que Docker Desktop está corriendo
docker ps
```

### Error: "port is already allocated"
```bash
# Ver qué está usando el puerto
netstat -ano | findstr :8080
netstat -ano | findstr :8081

# Detener procesos o cambiar puertos en docker-compose.yml
```

### Servicios no inician
```bash
# Ver logs detallados
docker-compose logs

# Ver estado de contenedores
docker-compose ps
```

### BFF no puede comunicarse con Orders
```bash
# Verificar que están en la misma red
docker network inspect pedidos360-network

# Verificar variables de entorno
docker-compose exec bff env | grep MICROSERVICES
```

### Reconstruir desde cero
```bash
# Detener todo
docker-compose down -v

# Eliminar imágenes
docker rmi pedidos360/bff pedidos360/orders

# Reconstruir
docker-compose up -d --build
```

## 📦 Imágenes Creadas

| Servicio | Imagen | Tamaño Aprox |
|----------|--------|-------------|
| BFF | `pedidos360/bff:latest` | ~250 MB |
| Orders | `pedidos360/orders:latest` | ~200 MB |

## 🔐 Seguridad

- ✅ Multi-stage builds (reduce tamaño)
- ✅ Usuarios no-root en contenedores
- ✅ Health checks configurados
- ✅ Restart policy: unless-stopped
- ✅ Variables de entorno para secretos

## 🚀 Deploy a AWS EC2

Para desplegar en AWS:

1. Copiar archivos a EC2:
```bash
scp -r infra/docker/* ubuntu@<EC2_IP>:~/pedidos360/
```

2. En EC2:
```bash
cd ~/pedidos360
docker-compose up -d --build
```

3. Configurar Security Group para exponer puertos 8080 y 8081

## 📋 Próximos Pasos

- [ ] Agregar RabbitMQ (rabbitmq.yml)
- [ ] Agregar Kafka + Zookeeper (kafka.yml)
- [ ] Agregar Oracle Database
- [ ] Agregar Catalog microservice
- [ ] Agregar Notify microservice
