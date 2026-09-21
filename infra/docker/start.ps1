# Script para iniciar todos los servicios con Docker Compose
# Pedidos360 - Microservicios

Write-Host "🐳 Pedidos360 - Docker Startup" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que Docker está corriendo
Write-Host "Verificando Docker..." -ForegroundColor Yellow
try {
    docker ps | Out-Null
    Write-Host "✓ Docker está corriendo" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker no está corriendo o no está instalado" -ForegroundColor Red
    Write-Host "Por favor, inicia Docker Desktop" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Verificar archivo .env
if (-not (Test-Path ".env")) {
    Write-Host "⚠ Archivo .env no encontrado" -ForegroundColor Yellow
    Write-Host "Creando .env desde .env.example..." -ForegroundColor Yellow
    Copy-Item ".env.example" ".env"
    Write-Host "✓ Archivo .env creado" -ForegroundColor Green
    Write-Host "  Puedes editarlo si necesitas cambiar valores" -ForegroundColor Gray
}

Write-Host ""

# Preguntar qué hacer
Write-Host "¿Qué deseas hacer?" -ForegroundColor Cyan
Write-Host "1) Construir y levantar servicios (--build)" -ForegroundColor White
Write-Host "2) Levantar servicios (sin rebuild)" -ForegroundColor White
Write-Host "3) Detener servicios" -ForegroundColor White
Write-Host "4) Ver logs" -ForegroundColor White
Write-Host "5) Ver estado de servicios" -ForegroundColor White
Write-Host ""

$opcion = Read-Host "Selecciona opción (1-5)"

switch ($opcion) {
    "1" {
        Write-Host ""
        Write-Host "🔨 Construyendo y levantando servicios..." -ForegroundColor Cyan
        docker-compose up -d --build
        Write-Host ""
        Write-Host "✓ Servicios iniciados" -ForegroundColor Green
        Write-Host ""
        Write-Host "Servicios disponibles:" -ForegroundColor Cyan
        Write-Host "  - Orders: http://localhost:8081/clientes" -ForegroundColor White
        Write-Host "  - BFF: http://localhost:8080/actuator/health" -ForegroundColor White
        Write-Host ""
        Write-Host "Ver logs: docker-compose logs -f" -ForegroundColor Gray
    }
    "2" {
        Write-Host ""
        Write-Host "🚀 Levantando servicios..." -ForegroundColor Cyan
        docker-compose up -d
        Write-Host ""
        Write-Host "✓ Servicios iniciados" -ForegroundColor Green
    }
    "3" {
        Write-Host ""
        Write-Host "🛑 Deteniendo servicios..." -ForegroundColor Cyan
        docker-compose down
        Write-Host ""
        Write-Host "✓ Servicios detenidos" -ForegroundColor Green
    }
    "4" {
        Write-Host ""
        Write-Host "📋 Mostrando logs (Ctrl+C para salir)..." -ForegroundColor Cyan
        Write-Host ""
        docker-compose logs -f
    }
    "5" {
        Write-Host ""
        Write-Host "📊 Estado de servicios:" -ForegroundColor Cyan
        Write-Host ""
        docker-compose ps
    }
    default {
        Write-Host ""
        Write-Host "✗ Opción inválida" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
