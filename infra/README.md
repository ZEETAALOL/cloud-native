# Infraestructura - Pedidos360

Configuración de infraestructura para despliegue local y en la nube.

## 📁 Estructura

```
infra/
├── docker/              # Docker Compose files
│   ├── apps.yml        # Microservicios backend
│   ├── rabbitmq.yml    # RabbitMQ + Management
│   └── kafka.yml       # Kafka + Zookeeper
│
└── aws/                 # Configuración AWS
    ├── api-gateway/    # AWS API Gateway configs
    ├── ec2/            # Scripts de deploy EC2
    └── rds/            # Oracle RDS configs
```

## 🐳 Docker Compose

### Levantar Todo el Sistema

```bash
# 1. Infraestructura de mensajería
cd infra/docker
docker-compose -f rabbitmq.yml up -d
docker-compose -f kafka.yml up -d

# 2. Microservicios
docker-compose -f apps.yml up -d
```

### Detener Todo
```bash
docker-compose -f apps.yml down
docker-compose -f rabbitmq.yml down
docker-compose -f kafka.yml down
```

## 🌩️ AWS Deployment

### Prerequisitos
- AWS Academy Lab iniciado
- AWS CLI configurado
- Docker instalado en EC2

### Instancias Requeridas

| Instancia | Propósito | Servicios |
|-----------|-----------|-----------|
| **ec2-apps** | Microservicios | BFF, Orders, Catalog, Notify |
| **ec2-mq** | Mensajería | RabbitMQ (2 nodos clúster) |
| **ec2-kafka** | Streaming | Zookeeper + Kafka (3 brokers) |
| **RDS Oracle** | Base de Datos | Todas las tablas |

### API Gateway

**Tipo:** HTTP API  
**Authorizer:** JWT (Azure AD)

**Configuración:**
- Issuer: `https://login.microsoftonline.com/<TENANT_ID>/v2.0`
- Audience: `api://<API_CLIENT_ID>`

**Rutas:**
- `GET /api/data` → BFF:8080
- `GET /api/orders/*` → BFF:8080
- `GET /api/catalog/*` → BFF:8080

## 🔐 Security Groups

### EC2 Apps
- Puerto 8080 (BFF)
- Puerto 8081 (Orders)
- Puerto 8082 (Catalog)
- Puerto 22 (SSH)

### EC2 RabbitMQ
- Puerto 5672 (AMQP)
- Puerto 15672 (Management UI)
- Puerto 22 (SSH)

### EC2 Kafka
- Puerto 9092 (Kafka)
- Puerto 2181 (Zookeeper)
- Puerto 8080 (Kafka UI)
- Puerto 22 (SSH)

## 📋 Checklist de Despliegue

### Local (Docker)
- [ ] Construir imágenes Docker
- [ ] Levantar RabbitMQ
- [ ] Levantar Kafka
- [ ] Levantar microservicios
- [ ] Probar endpoints

### AWS
- [ ] Crear instancias EC2
- [ ] Configurar security groups
- [ ] Instalar Docker en EC2
- [ ] Copiar docker-compose a EC2
- [ ] Configurar API Gateway
- [ ] Configurar RDS Oracle
- [ ] Actualizar variables de entorno
- [ ] Deploy y pruebas

## 🔄 CI/CD (Futuro)

Considerar GitHub Actions para:
- Build automático de imágenes Docker
- Push a Docker Hub / ECR
- Deploy automático a EC2
