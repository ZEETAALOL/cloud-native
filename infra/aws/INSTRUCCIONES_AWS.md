# 🚀 Guía de Deploy en AWS Academy

## ⏱️ Tiempo estimado: 2-3 horas

---

## 📋 **PASO 1: Acceder a AWS Academy (10 min)**

### 1.1 Iniciar Learner Lab
1. Ve a tu curso en AWS Academy
2. Click en **"Modules"** → **"Learner Lab"**
3. Click en **"Start Lab"** (espera que el círculo se ponga verde ●)
4. Click en **"AWS"** para abrir la consola

### 1.2 Verificar región
- Asegúrate de estar en **us-east-1 (N. Virginia)** (arriba a la derecha)

---

## 📋 **PASO 2: Crear EC2 Instance (20 min)**

### 2.1 Ir a EC2
1. En la consola AWS, busca **"EC2"** en el buscador
2. Click en **"Launch Instance"** (botón naranja)

### 2.2 Configurar instancia

#### **Name:**
```
pedidos360-production
```

#### **Application and OS Images:**
- **Ubuntu Server 22.04 LTS**
- 64-bit (x86)
- Click en "Select"

#### **Instance type:**
- Buscar y seleccionar: **t3.xlarge**
  - 4 vCPU
  - 16 GB RAM
  - ⚠️ Importante: Necesitamos esta capacidad para 10+ containers

#### **Key pair:**
1. Click en **"Create new key pair"**
2. Nombre: `pedidos360-key`
3. Type: **RSA**
4. Format: **.pem** (para SSH)
5. Click **"Create key pair"**
6. ⚠️ **GUARDA EL ARCHIVO .pem** en un lugar seguro

#### **Network settings:**
1. Click en **"Edit"**
2. **Firewall (security groups):** Create new security group
3. Nombre: `pedidos360-sg`
4. Descripción: `Security group para Pedidos360`

**Agregar reglas:**

| Type | Protocol | Port | Source | Description |
|------|----------|------|--------|-------------|
| SSH | TCP | 22 | My IP | SSH access |
| HTTP | TCP | 80 | Anywhere (0.0.0.0/0) | Traefik HTTP |
| HTTPS | TCP | 443 | Anywhere (0.0.0.0/0) | Traefik HTTPS |
| Custom TCP | TCP | 8080 | Anywhere (0.0.0.0/0) | Keycloak |
| Custom TCP | TCP | 5672 | My IP | RabbitMQ (debugging) |
| Custom TCP | TCP | 15672 | My IP | RabbitMQ Admin |

#### **Configure storage:**
- **30 GiB** gp3
- (Suficiente para imágenes Docker + logs)

### 2.3 Launch!
1. Revisa el resumen a la derecha
2. Click en **"Launch Instance"** (botón naranja)
3. Espera ~2 minutos a que inicie
4. Click en el **Instance ID** (i-xxxxx)

### 2.4 Obtener IP pública
1. En la lista de instancias, encuentra tu instancia
2. Copia la **"Public IPv4 address"**
3. **Anota esta IP** (ej: `3.80.123.45`)

---

## 📋 **PASO 3: Conectar por SSH (10 min)**

### 3.1 Preparar el archivo .pem (Windows)

```powershell
# Ir a la carpeta donde descargaste el .pem
cd C:\Users\TU_USUARIO\Downloads

# Verificar que existe
ls pedidos360-key.pem

# Moverlo a una ubicación segura
Move-Item pedidos360-key.pem C:\Users\TU_USUARIO\.ssh\
```

### 3.2 Conectar (PowerShell)

```powershell
# Cambiar permisos (Windows)
icacls C:\Users\TU_USUARIO\.ssh\pedidos360-key.pem /inheritance:r
icacls C:\Users\TU_USUARIO\.ssh\pedidos360-key.pem /grant:r "$($env:USERNAME):R"

# Conectar por SSH (reemplaza la IP)
ssh -i C:\Users\TU_USUARIO\.ssh\pedidos360-key.pem ubuntu@3.80.123.45
```

### 3.3 Verificar conexión

Si sale:
```
The authenticity of host '3.80.123.45' can't be established.
Are you sure you want to continue connecting (yes/no)?
```
Escribe: **`yes`** y Enter

✅ Deberías ver:
```
Welcome to Ubuntu 22.04.3 LTS
ubuntu@ip-172-31-xx-xx:~$
```

---

## 📋 **PASO 4: Ejecutar Setup Automático (30 min)**

### 4.1 Descargar script de setup

```bash
# Descargar directamente desde tu repo (una vez subido a GitHub)
wget https://raw.githubusercontent.com/TU-USUARIO/proyecto_cloudnative_ev1/main/infra/aws/setup-ec2.sh

# O crear manualmente:
nano setup-ec2.sh
# (Pegar contenido del script)
# Ctrl+X, Y, Enter
```

### 4.2 Dar permisos y ejecutar

```bash
chmod +x setup-ec2.sh
./setup-ec2.sh
```

El script te pedirá la URL de tu repositorio GitHub:
```
Por favor ingresa la URL de tu repositorio GitHub:
```

Pega: `https://github.com/TU-USUARIO/proyecto_cloudnative_ev1.git`

⏱️ **Esto tomará ~5-10 minutos**

### 4.3 Logout y login

```bash
exit
```

Volver a conectar:
```powershell
ssh -i C:\Users\TU_USUARIO\.ssh\pedidos360-key.pem ubuntu@3.80.123.45
```

---

## 📋 **PASO 5: Deploy con Docker Compose (20 min)**

### 5.1 Navegar al proyecto

```bash
cd /home/ubuntu/proyecto_cloudnative_ev1/infra/docker
```

### 5.2 Verificar archivos

```bash
ls -la
# Deberías ver: docker-compose.yml, .env.aws, etc.
```

### 5.3 Levantar servicios

```bash
docker compose up -d
```

⏱️ **Primera vez: ~10-15 minutos** (descarga imágenes)

### 5.4 Ver el progreso

```bash
# Ver qué está corriendo
docker compose ps

# Ver logs en tiempo real
docker compose logs -f

# Ver logs de un servicio específico
docker compose logs -f bff
docker compose logs -f keycloak

# Salir de los logs: Ctrl+C
```

### 5.5 Verificar que todo esté UP

```bash
docker compose ps
```

✅ Todos los servicios deben mostrar **"Up"** o **"healthy"**

---

## 📋 **PASO 6: Configurar Keycloak (30 min)**

### 6.1 Acceder a Keycloak

Desde tu navegador:
```
http://3.80.123.45:8080
```
(Reemplaza con tu IP pública)

### 6.2 Login admin

- Username: `admin`
- Password: `admin123`

### 6.3 Crear Realm

1. Click en el dropdown arriba a la izquierda (dice "master")
2. Click en **"Create Realm"**
3. Realm name: `pedidos360`
4. Click **"Create"**

### 6.4 Crear Client para API (BFF)

1. En el menú izquierdo: **"Clients"** → **"Create client"**

**General Settings:**
- Client type: `OpenID Connect`
- Client ID: `pedidos360-api`
- Name: `Pedidos360 API`
- Click **"Next"**

**Capability config:**
- Client authentication: **ON** ✅
- Authorization: **OFF**
- Authentication flow:
  - ✅ Standard flow
  - ✅ Direct access grants
  - ❌ Todo lo demás OFF
- Click **"Next"**

**Login settings:**
- Root URL: `http://3.80.123.45`
- Valid redirect URIs: `http://3.80.123.45/*`
- Web origins: `http://3.80.123.45`
- Click **"Save"**

### 6.5 Obtener Client Secret

1. Ve a la tab **"Credentials"**
2. Copia el **"Client secret"**
3. **⚠️ ANOTA ESTE SECRET** (lo necesitarás para el BFF)

### 6.6 Crear Client para Frontend (SPA)

1. **"Clients"** → **"Create client"**

**General Settings:**
- Client ID: `pedidos360-frontend`
- Name: `Pedidos360 Frontend`
- Click **"Next"**

**Capability config:**
- Client authentication: **OFF** ❌
- Authorization: **OFF**
- Authentication flow:
  - ✅ Standard flow
  - ❌ Todo lo demás OFF
- Click **"Next"**

**Login settings:**
- Root URL: `http://3.80.123.45`
- Valid redirect URIs: `http://3.80.123.45/*`
- Valid post logout redirect URIs: `http://3.80.123.45/*`
- Web origins: `http://3.80.123.45`
- Click **"Save"**

### 6.7 Crear usuario de prueba

1. Menú izquierdo: **"Users"** → **"Add user"**
2. Username: `testuser`
3. Email: `test@pedidos360.com`
4. First name: `Test`
5. Last name: `User`
6. Email verified: **ON** ✅
7. Click **"Create"**

**Establecer password:**
1. Tab **"Credentials"**
2. Click **"Set password"**
3. Password: `test123`
4. Password confirmation: `test123`
5. Temporary: **OFF** ❌
6. Click **"Save"**
7. Confirmar **"Save password"**

---

## 📋 **PASO 7: Testing (20 min)**

### 7.1 Health checks

```bash
# Desde EC2
curl http://localhost/actuator/health
curl http://localhost:8080/health
```

### 7.2 Desde tu computadora

```bash
# Health check
curl http://3.80.123.45/actuator/health

# BFF
curl http://3.80.123.45/api/products

# Keycloak
curl http://3.80.123.45:8080/realms/pedidos360
```

### 7.3 Obtener token (Postman)

**Request:**
```
POST http://3.80.123.45:8080/realms/pedidos360/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password
client_id=pedidos360-api
client_secret=<TU_CLIENT_SECRET>
username=testuser
password=test123
```

✅ Deberías recibir un `access_token`

### 7.4 Llamar API con token

```
GET http://3.80.123.45/api/products
Authorization: Bearer <ACCESS_TOKEN>
```

---

## 📋 **PASO 8: Anotar Información (5 min)**

### Crear archivo con toda la info:

```bash
cd ~
nano DATOS_AWS.txt
```

Pega:
```
========================================
PEDIDOS360 - INFORMACIÓN DE ACCESO AWS
========================================

FECHA: <HOY>

EC2 INSTANCE:
- Instance ID: i-xxxxxxxxxxxxx
- Public IP: 3.80.123.45
- Instance Type: t3.xlarge
- SSH: ssh -i ~/.ssh/pedidos360-key.pem ubuntu@3.80.123.45

URLS:
- Frontend: http://3.80.123.45
- Keycloak: http://3.80.123.45:8080
- BFF API: http://3.80.123.45/api

KEYCLOAK:
- Admin user: admin
- Admin pass: admin123
- Realm: pedidos360
- API Client ID: pedidos360-api
- API Client Secret: <TU_SECRET>
- Frontend Client ID: pedidos360-frontend
- Test user: testuser / test123

REPOSITORIO:
- GitHub: https://github.com/TU-USUARIO/proyecto_cloudnative_ev1

COMANDOS ÚTILES:
- Ver servicios: docker compose ps
- Ver logs: docker compose logs -f
- Restart: docker compose restart
- Stop: docker compose down
- Start: docker compose up -d

========================================
```

Guardar: `Ctrl+X`, `Y`, `Enter`

---

## 🎯 **CHECKLIST FINAL**

- [ ] EC2 instance creada y corriendo
- [ ] SSH funcionando
- [ ] Docker instalado
- [ ] Proyecto clonado
- [ ] Docker Compose corriendo (todos los servicios UP)
- [ ] Keycloak accesible
- [ ] Realm y clients creados
- [ ] Usuario de prueba creado
- [ ] Token obtenido correctamente
- [ ] API responde con token
- [ ] Toda la información documentada

---

## 🚨 **TROUBLESHOOTING**

### Problema: No puedo conectar por SSH
```bash
# Verificar permisos del .pem
icacls pedidos360-key.pem

# Verificar security group permite tu IP
# Console AWS → EC2 → Security Groups → pedidos360-sg → Edit inbound rules
```

### Problema: Servicios no levantan
```bash
# Ver logs detallados
docker compose logs

# Verificar memoria
free -h

# Restart
docker compose down
docker compose up -d
```

### Problema: Keycloak no responde
```bash
# Ver logs de Keycloak
docker compose logs keycloak

# Verificar puerto
sudo netstat -tulpn | grep 8080
```

### Problema: "Cannot connect to Docker daemon"
```bash
# Verificar que Docker esté corriendo
sudo systemctl status docker

# Verificar permisos
groups
# Debe incluir "docker"

# Si no, logout y login de nuevo
```

---

## 📝 **NOTAS IMPORTANTES**

⚠️ **AWS Academy Learner Lab:**
- Las instancias se **APAGAN automáticamente** después de 4 horas de inactividad
- El presupuesto es limitado ($100 USD)
- La IP pública **CAMBIA** cada vez que inicias el lab
- Guarda todo tu trabajo en GitHub

⚠️ **Antes de la evaluación:**
- Inicia el Learner Lab con **al menos 6 horas de anticipación**
- Verifica que todo esté funcionando
- Ten el token de acceso listo
- Screenshots y videos preparados

---

## ✅ **SIGUIENTE: Frontend React**

Una vez que el backend esté funcionando en AWS, continúa con:
`/infra/aws/FRONTEND_REACT.md`
