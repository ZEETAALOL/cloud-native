# Script de Verificación Pre-Ejecución
# Verifica que todo esté configurado correctamente antes de ejecutar

Write-Host "=== Verificación de Setup - Pedidos360 ===" -ForegroundColor Cyan
Write-Host ""

$errores = 0

# 1. Verificar Node.js
Write-Host "1. Verificando Node.js..." -ForegroundColor Yellow
try {
    $nodeVersion = node --version
    Write-Host "   ✓ Node.js instalado: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Node.js NO encontrado. Instalar desde https://nodejs.org" -ForegroundColor Red
    $errores++
}

# 2. Verificar Java
Write-Host "2. Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "   ✓ Java instalado: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Java NO encontrado. Instalar JDK 17+" -ForegroundColor Red
    $errores++
}

# 3. Verificar Maven
Write-Host "3. Verificando Maven..." -ForegroundColor Yellow
try {
    $mvnVersion = mvn --version 2>&1 | Select-String "Apache Maven"
    Write-Host "   ✓ Maven instalado: $mvnVersion" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Maven NO encontrado. Instalar desde https://maven.apache.org" -ForegroundColor Red
    $errores++
}

# 4. Verificar dependencias npm
Write-Host "4. Verificando dependencias npm..." -ForegroundColor Yellow
if (Test-Path "frontend\node_modules") {
    Write-Host "   ✓ node_modules existe" -ForegroundColor Green
} else {
    Write-Host "   ✗ node_modules NO existe. Ejecutar: cd frontend && npm install" -ForegroundColor Red
    $errores++
}

# 5. Verificar .env.local
Write-Host "5. Verificando .env.local..." -ForegroundColor Yellow
if (Test-Path "frontend\.env.local") {
    Write-Host "   ✓ .env.local existe" -ForegroundColor Green
    
    $envContent = Get-Content "frontend\.env.local" -Raw
    if ($envContent -match "TU_TENANT_ID|TU-TENANT-ID|<.*>") {
        Write-Host "   ⚠ ADVERTENCIA: .env.local contiene placeholders. Reemplazar con valores reales." -ForegroundColor Yellow
        $errores++
    } else {
        Write-Host "   ✓ .env.local parece configurado" -ForegroundColor Green
    }
} else {
    Write-Host "   ✗ .env.local NO existe. Copiar desde .env.local.example" -ForegroundColor Red
    $errores++
}

# 6. Verificar variables de entorno PowerShell
Write-Host "6. Verificando variables de entorno..." -ForegroundColor Yellow
if ($env:ENTRA_ISSUER_URI -and $env:ENTRA_API_CLIENT_ID) {
    Write-Host "   ✓ ENTRA_ISSUER_URI: $env:ENTRA_ISSUER_URI" -ForegroundColor Green
    Write-Host "   ✓ ENTRA_API_CLIENT_ID: $env:ENTRA_API_CLIENT_ID" -ForegroundColor Green
} else {
    Write-Host "   ⚠ Variables de entorno NO configuradas (necesarias para BFF)" -ForegroundColor Yellow
    Write-Host "     Ejecutar antes de iniciar BFF:" -ForegroundColor Yellow
    Write-Host '     $env:ENTRA_ISSUER_URI="https://login.microsoftonline.com/<TENANT-ID>/v2.0"' -ForegroundColor Gray
    Write-Host '     $env:ENTRA_API_CLIENT_ID="<API-CLIENT-ID>"' -ForegroundColor Gray
}

# 7. Verificar puertos libres
Write-Host "7. Verificando puertos..." -ForegroundColor Yellow
$puertos = @(5173, 8080, 8081)
foreach ($puerto in $puertos) {
    $conexion = Get-NetTCPConnection -LocalPort $puerto -ErrorAction SilentlyContinue
    if ($conexion) {
        Write-Host "   ⚠ Puerto $puerto YA EN USO" -ForegroundColor Yellow
    } else {
        Write-Host "   ✓ Puerto $puerto disponible" -ForegroundColor Green
    }
}

# Resumen
Write-Host ""
Write-Host "=== Resumen ===" -ForegroundColor Cyan
if ($errores -eq 0) {
    Write-Host "✓ Todo listo para ejecutar!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Próximos pasos:" -ForegroundColor Cyan
    Write-Host "1. Terminal 1: cd services\orders && mvn spring-boot:run" -ForegroundColor Gray
    Write-Host "2. Terminal 2: cd services\bff && mvn spring-boot:run" -ForegroundColor Gray
    Write-Host "3. Terminal 3: cd frontend && npm run dev" -ForegroundColor Gray
} else {
    Write-Host "✗ Encontrados $errores errores. Corregir antes de ejecutar." -ForegroundColor Red
    Write-Host "Ver: docs\INSTRUCCIONES_SETUP.md para más detalles" -ForegroundColor Yellow
}
