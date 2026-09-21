#!/bin/bash
###############################################################################
# Script de Setup Automático para EC2 - Pedidos360
# Ejecutar DESPUÉS de conectar por SSH a la instancia EC2
###############################################################################

set -e  # Exit on error

echo "🚀 Iniciando setup de EC2 para Pedidos360..."
echo ""

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. Update system
echo -e "${BLUE}📦 Actualizando sistema...${NC}"
sudo apt-get update -y
sudo apt-get upgrade -y

# 2. Install Docker
echo -e "${BLUE}🐋 Instalando Docker...${NC}"
sudo apt-get install -y docker.io docker-compose-v2 git curl

# 3. Configure Docker
echo -e "${BLUE}⚙️  Configurando Docker...${NC}"
sudo usermod -aG docker ubuntu
sudo systemctl enable docker
sudo systemctl start docker

# 4. Get public IP
echo -e "${BLUE}🌐 Obteniendo IP pública...${NC}"
export PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)
echo -e "${GREEN}✅ IP Pública: $PUBLIC_IP${NC}"

# 5. Clone repository
echo -e "${BLUE}📥 Clonando repositorio...${NC}"
if [ ! -d "/home/ubuntu/proyecto_cloudnative_ev1" ]; then
    echo -e "${YELLOW}Por favor ingresa la URL de tu repositorio GitHub:${NC}"
    read REPO_URL
    git clone $REPO_URL /home/ubuntu/proyecto_cloudnative_ev1
else
    echo -e "${YELLOW}⚠️  Repositorio ya existe, usando existente${NC}"
fi

cd /home/ubuntu/proyecto_cloudnative_ev1/infra/docker

# 6. Create .env file for AWS
echo -e "${BLUE}📝 Creando archivo de variables de entorno...${NC}"
cat > .env.aws << EOF
# AWS Environment Variables
PUBLIC_IP=$PUBLIC_IP
EXTERNAL_URL=http://$PUBLIC_IP

# Keycloak
KC_HOSTNAME=$PUBLIC_IP
KC_HTTP_PORT=8080

# Traefik
TRAEFIK_ENTRYPOINT_HTTP_PORT=80

# Database passwords (CAMBIAR EN PRODUCCIÓN)
POSTGRES_PASSWORD=postgres123
MONGO_ROOT_PASSWORD=mongo123

# RabbitMQ
RABBITMQ_DEFAULT_USER=admin
RABBITMQ_DEFAULT_PASS=admin123
EOF

echo -e "${GREEN}✅ Variables de entorno creadas${NC}"

# 7. Show next steps
echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}✅ Setup completado exitosamente!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${YELLOW}📋 Próximos pasos:${NC}"
echo ""
echo "1. Logout y login de nuevo para aplicar permisos Docker:"
echo -e "   ${BLUE}exit${NC}"
echo -e "   ${BLUE}ssh -i tu-key.pem ubuntu@$PUBLIC_IP${NC}"
echo ""
echo "2. Navegar al directorio del proyecto:"
echo -e "   ${BLUE}cd /home/ubuntu/proyecto_cloudnative_ev1/infra/docker${NC}"
echo ""
echo "3. Levantar servicios:"
echo -e "   ${BLUE}docker compose up -d${NC}"
echo ""
echo "4. Ver logs:"
echo -e "   ${BLUE}docker compose logs -f${NC}"
echo ""
echo "5. Acceder a los servicios:"
echo -e "   🌐 Traefik:    http://$PUBLIC_IP"
echo -e "   🔐 Keycloak:   http://$PUBLIC_IP:8080"
echo -e "   📊 BFF:        http://$PUBLIC_IP/api"
echo ""
echo -e "${GREEN}========================================${NC}"
