#!/bin/bash

# Script de deployment automatizado para EC2-1 (ec2-apps)
# Pedidos360 - Cloud Native

set -e  # Salir si hay error

echo "🚀 Iniciando deployment de EC2-1 (ec2-apps)..."
echo "================================================"

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Función para imprimir con color
print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Verificar que estamos en el directorio correcto
if [ ! -f "docker-compose.yml" ]; then
    print_error "docker-compose.yml no encontrado. Ejecuta este script desde infra/ec2-apps/"
    exit 1
fi

print_success "Directorio correcto confirmado"

# Verificar Docker
if ! command -v docker &> /dev/null; then
    print_error "Docker no está instalado. Instálalo primero."
    exit 1
fi

print_success "Docker encontrado: $(docker --version)"

# Verificar Docker Compose
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose no está instalado. Instálalo primero."
    exit 1
fi

print_success "Docker Compose encontrado: $(docker-compose --version)"

# Preguntar si hacer pull del código
echo ""
read -p "¿Hacer git pull antes de desplegar? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_warning "Haciendo git pull..."
    cd ../..
    git pull origin main
    cd infra/ec2-apps
    print_success "Código actualizado"
fi

# Preguntar si reconstruir imágenes
echo ""
read -p "¿Reconstruir imágenes Docker? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_warning "Construyendo imágenes Docker..."
    docker-compose build --no-cache
    print_success "Imágenes construidas"
else
    print_warning "Usando imágenes existentes"
fi

# Detener contenedores existentes
echo ""
print_warning "Deteniendo contenedores existentes..."
docker-compose down

# Limpiar volúmenes si se solicita
echo ""
read -p "¿Eliminar volúmenes de bases de datos? (BORRA TODOS LOS DATOS) (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_warning "Eliminando volúmenes..."
    docker-compose down -v
    print_warning "Volúmenes eliminados - Se crearán bases de datos limpias"
else
    print_success "Volúmenes preservados"
fi

# Levantar bases de datos primero
echo ""
print_warning "Levantando bases de datos..."
docker-compose up -d postgres mongodb

print_warning "Esperando que las bases de datos arranquen (20 segundos)..."
sleep 20

# Verificar que las DBs están saludables
echo ""
print_warning "Verificando salud de bases de datos..."

if docker exec pedidos360-postgres pg_isready -U admin -d pedidos360 > /dev/null 2>&1; then
    print_success "PostgreSQL está listo"
else
    print_error "PostgreSQL no está respondiendo"
    print_warning "Logs de PostgreSQL:"
    docker-compose logs postgres | tail -20
    exit 1
fi

if docker exec pedidos360-mongodb mongosh --eval "db.adminCommand('ping')" > /dev/null 2>&1; then
    print_success "MongoDB está listo"
else
    print_error "MongoDB no está respondiendo"
    print_warning "Logs de MongoDB:"
    docker-compose logs mongodb | tail -20
    exit 1
fi

# Levantar microservicios
echo ""
print_warning "Levantando microservicios..."
docker-compose up -d orders catalog audit report notify

print_warning "Esperando que los microservicios arranquen (30 segundos)..."
sleep 30

# Levantar BFF
echo ""
print_warning "Levantando BFF..."
docker-compose up -d bff

print_warning "Esperando que el BFF arranque (20 segundos)..."
sleep 20

# Verificar que todo está corriendo
echo ""
echo "================================================"
print_warning "Verificando estado de contenedores..."
echo "================================================"

docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# Health checks
echo ""
echo "================================================"
print_warning "Ejecutando health checks..."
echo "================================================"

services=("bff:8080" "orders:8081" "catalog:8084" "audit:8083" "report:8085" "notify:8086")

for service in "${services[@]}"; do
    IFS=':' read -r name port <<< "$service"
    echo -n "Verificando $name... "
    
    if curl -s http://localhost:$port/actuator/health > /dev/null 2>&1; then
        print_success "OK"
    else
        print_error "FAILED"
    fi
done

# Verificar datos en PostgreSQL
echo ""
print_warning "Verificando datos en PostgreSQL..."
product_count=$(docker exec pedidos360-postgres psql -U admin -d pedidos360 -t -c "SELECT COUNT(*) FROM products;" 2>/dev/null | tr -d ' ')

if [ -n "$product_count" ] && [ "$product_count" -gt 0 ]; then
    print_success "$product_count productos cargados en PostgreSQL"
else
    print_warning "No se pudieron cargar productos de ejemplo"
fi

# Verificar datos en MongoDB
echo ""
print_warning "Verificando datos en MongoDB..."
audit_count=$(docker exec pedidos360-mongodb mongosh -u admin -p admin123 --authenticationDatabase admin --quiet --eval "db.getSiblingDB('pedidos360').audit_events.countDocuments()" 2>/dev/null)

if [ -n "$audit_count" ] && [ "$audit_count" -gt 0 ]; then
    print_success "$audit_count eventos de auditoría en MongoDB"
else
    print_warning "No se pudieron cargar eventos de ejemplo"
fi

# Resumen final
echo ""
echo "================================================"
print_success "Deployment completado!"
echo "================================================"
echo ""
echo "📊 URLs de acceso:"
echo "   - BFF (API):        http://localhost:8080"
echo "   - Orders:           http://localhost:8081"
echo "   - Catalog:          http://localhost:8084"
echo "   - Audit:            http://localhost:8083"
echo "   - Report:           http://localhost:8085"
echo "   - Notify:           http://localhost:8086"
echo ""
echo "💾 Bases de datos:"
echo "   - PostgreSQL:       localhost:5432"
echo "   - MongoDB:          localhost:27017"
echo ""
echo "🔍 Comandos útiles:"
echo "   - Ver logs:         docker-compose logs -f"
echo "   - Ver logs BFF:     docker-compose logs -f bff"
echo "   - Ver estado:       docker ps"
echo "   - Detener:          docker-compose down"
echo ""
print_success "¡Todo listo! 🚀"
