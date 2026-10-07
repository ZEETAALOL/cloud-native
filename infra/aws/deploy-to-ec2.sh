#!/bin/bash

##############################################
# Script de Despliegue Automático en EC2
# Proyecto: Pedidos360
# IP Pública: 35.171.18.87
##############################################

set -e  # Detener si hay errores

echo "============================================"
echo "🚀 Iniciando despliegue de Pedidos360"
echo "============================================"

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Variables
PUBLIC_IP="98.81.152.105"
KEYCLOAK_URL="http://${PUBLIC_IP}:8080"
PROJECT_DIR="/home/ubuntu/cloud-native"

echo -e "${BLUE}📍 IP Pública: ${PUBLIC_IP}${NC}"
echo ""

# Paso 1: Actualizar sistema
echo -e "${BLUE}[1/8] Actualizando sistema...${NC}"
sudo apt update && sudo apt upgrade -y

# Paso 2: Instalar Docker
echo -e "${BLUE}[2/8] Instalando Docker...${NC}"
if ! command -v docker &> /dev/null; then
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker ubuntu
    rm get-docker.sh
    echo -e "${GREEN}✓ Docker instalado${NC}"
else
    echo -e "${GREEN}✓ Docker ya está instalado${NC}"
fi

# Paso 3: Instalar Docker Compose
echo -e "${BLUE}[3/8] Instalando Docker Compose...${NC}"
if ! command -v docker-compose &> /dev/null; then
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    echo -e "${GREEN}✓ Docker Compose instalado${NC}"
else
    echo -e "${GREEN}✓ Docker Compose ya está instalado${NC}"
fi

# Paso 4: Instalar Git
echo -e "${BLUE}[4/8] Instalando Git...${NC}"
if ! command -v git &> /dev/null; then
    sudo apt install git -y
    echo -e "${GREEN}✓ Git instalado${NC}"
else
    echo -e "${GREEN}✓ Git ya está instalado${NC}"
fi

# Paso 5: Clonar proyecto
echo -e "${BLUE}[5/8] Clonando proyecto desde GitHub...${NC}"
if [ -d "$PROJECT_DIR" ]; then
    echo "⚠️  El directorio ya existe. Actualizando..."
    cd $PROJECT_DIR
    git pull
else
    cd /home/ubuntu
    git clone https://github.com/ZEETAALOL/cloud-native.git
    cd cloud-native
fi
echo -e "${GREEN}✓ Proyecto clonado${NC}"

# Paso 6: Configurar variables de entorno
echo -e "${BLUE}[6/8] Configurando variables de entorno...${NC}"
cd $PROJECT_DIR/infra/docker

# Crear archivo .env si no existe
if [ ! -f .env ]; then
    cp .env.example .env
fi

# Actualizar .env con la IP pública
cat > .env << EOF
# Variables de entorno para Docker Compose - AWS EC2
# IP Pública: 98.81.152.105

# Azure AD Configuration
ENTRA_ISSUER_URI=https://login.microsoftonline.com/47c2bee0-5950-430f-9276-bfc083e3d1da/v2.0
ENTRA_API_CLIENT_ID=faba8741-ba0d-440c-b061-f1aa893eb957

# Keycloak Configuration
KEYCLOAK_URL=http://98.81.152.105:8080
KEYCLOAK_REALM=pedidos360
KEYCLOAK_CLIENT_ID=pedidos360-client

# Spring Profiles
SPRING_PROFILES_ACTIVE=docker,prod

# Public IP for services
PUBLIC_IP=98.81.152.105
EOF

echo -e "${GREEN}✓ Variables de entorno configuradas${NC}"

# Paso 7: Construir e iniciar servicios
echo -e "${BLUE}[7/8] Construyendo e iniciando servicios...${NC}"
echo "⏳ Este proceso tomará varios minutos (5-10 min)"
echo ""

# Descargar imágenes base primero (más rápido)
docker-compose pull postgres mongodb rabbitmq traefik 2>/dev/null || true

# Construir e iniciar servicios
docker-compose up -d --build

echo -e "${GREEN}✓ Servicios iniciados${NC}"

# Paso 8: Verificar estado de servicios
echo -e "${BLUE}[8/8] Verificando estado de servicios...${NC}"
echo ""
sleep 10

echo "Esperando que los servicios estén listos (esto puede tomar 2-3 minutos)..."
echo ""

# Esperar 90 segundos para que todos los servicios inicien
for i in {1..9}; do
    echo -n "."
    sleep 10
done
echo ""

docker-compose ps

echo ""
echo "============================================"
echo -e "${GREEN}✅ DESPLIEGUE COMPLETADO${NC}"
echo "============================================"
echo ""
echo "🌐 URLs de Acceso:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  API Gateway (Traefik):    http://${PUBLIC_IP}"
echo "  Keycloak Admin:           http://${PUBLIC_IP}:8080"
echo "  RabbitMQ Management:      http://${PUBLIC_IP}:15672"
echo "  BFF API:                  http://${PUBLIC_IP}:8081"
echo "  Orders Service:           http://${PUBLIC_IP}:8082"
echo "  Audit Service:            http://${PUBLIC_IP}:8083"
echo "  Catalog Service:          http://${PUBLIC_IP}:8084"
echo "  Report Service:           http://${PUBLIC_IP}:8085"
echo "  Notify Service:           http://${PUBLIC_IP}:8086"
echo ""
echo "🔐 Credenciales:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Keycloak Admin:   admin / admin"
echo "  RabbitMQ:         admin / admin123"
echo ""
echo "📋 Comandos Útiles:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Ver logs:         cd $PROJECT_DIR/infra/docker && docker-compose logs -f"
echo "  Ver un servicio:  docker-compose logs -f [servicio]"
echo "  Reiniciar todo:   docker-compose restart"
echo "  Detener todo:     docker-compose down"
echo "  Estado:           docker-compose ps"
echo ""
echo "⚠️  IMPORTANTE: Configura Keycloak"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "1. Accede a: http://${PUBLIC_IP}:8080"
echo "2. Login: admin / admin"
echo "3. Crear realm 'pedidos360'"
echo "4. Crear client 'pedidos360-client'"
echo "5. Configurar Valid Redirect URIs: http://${PUBLIC_IP}/*"
echo ""
echo "============================================"
