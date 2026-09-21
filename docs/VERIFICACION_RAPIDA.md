# ✅ Verificación Rápida - Tutorial Parte 1 + 2

## 🎯 Objetivo
Verificar que el **flujo completo funciona** siguiendo exactamente los tutoriales PDF.

## 📋 Checklist Pre-Ejecución

### Azure AD Configurado
- [ ] Tenant creado y tienes el `TENANT_ID`
- [ ] App Registration `api-fullstack` creada → `API_CLIENT_ID`
- [ ] App Registration `spa-fullstack` creada → `SPA_CLIENT_ID`
- [ ] Scope `access_as_user` expuesto en API
- [ ] Permisos delegados otorgados y consentimiento dado

### Variables de Entorno
- [ ] Archivo `frontend/.env.local` creado con tus IDs
- [ ] Variables `$env:ENTRA_ISSUER_URI` y `$env:ENTRA_API_CLIENT_ID` configuradas en PowerShell

### Dependencias Instaladas
- [ ] `cd frontend && npm install` ejecutado

## 🚀 Ejecución Rápida

### Terminal 1: Orders
```powershell
cd services\orders
mvn spring-boot:run
```

**Espera ver:**
```
Started OrdersApplication in X.XXX seconds
```

**Prueba manual:**
```powershell
Invoke-WebRequest http://localhost:8081/clientes
```

**Debería retornar:**
```json
{
  "rut": "12345678-9",
  "nombre": "Wacoldo",
  "apellido": "Soto",
  "direccion": "Calle Falsa 123",
  "comuna": "Santiago"
}
```

---

### Terminal 2: BFF
```powershell
cd services\bff

# Configurar variables (reemplaza con tus valores)
$env:ENTRA_ISSUER_URI="https://login.microsoftonline.com/TU-TENANT-ID/v2.0"
$env:ENTRA_API_CLIENT_ID="TU-API-CLIENT-ID"

# Ejecutar
mvn spring-boot:run
```

**Espera ver:**
```
Started ApiApplication in X.XXX seconds
```

---

### Terminal 3: Frontend
```powershell
cd frontend
npm run dev
```

**Espera ver:**
```
VITE v6.x.x ready in XXX ms
➜  Local:   http://localhost:5173/
```

---

## 🧪 Pruebas del Flujo

### 1. Abrir el Browser
Abre: **http://localhost:5173**

### 2. Tutorial Parte 1: Autenticación + Token

1. **Clic en "Iniciar sesión"**
   - ✅ Se abre popup de Azure AD
   - ✅ Ingresas credenciales
   - ✅ Te redirige de vuelta
   - ✅ Ves tu email en "Sesión: tu-usuario@..."

2. **Clic en "Obtener token API"**
   - ✅ Ves mensaje: "Token de API obtenido. Vence: [fecha]"
   - ✅ Sin errores en consola

3. **Clic en "Consultar API"** (este botón puede estar deshabilitado si `VITE_API_BASE_URL` está vacío)
   - Si está vacío → Es correcto, solo Parte 1 no necesita esto

### 3. Tutorial Parte 2: BFF + Microservicio

1. **Clic en "Consultar BFF + ms-clientes"**
   - ✅ Ves en pantalla:
   ```json
   {
     "rut": "12345678-9",
     "nombre": "Wacoldo",
     "apellido": "Soto",
     "direccion": "Calle Falsa 123",
     "comuna": "Santiago"
   }
   ```

**Si ves estos datos → ✅ TODO FUNCIONA!**

---

## 🐛 Troubleshooting Rápido

### Error: "Invalid token" en BFF
```powershell
# Verifica que las variables estén configuradas
echo $env:ENTRA_ISSUER_URI
echo $env:ENTRA_API_CLIENT_ID
```

**Solución:** Reinicia el BFF después de configurar las variables.

---

### Error: "Connection refused" en BFF
**Causa:** Microservicio Orders no está corriendo.

**Solución:** 
```powershell
cd services\orders
mvn spring-boot:run
```

Espera a que inicie completamente antes de probar el BFF.

---

### Error: CORS en navegador
**Causa:** BFF no está corriendo o está en otro puerto.

**Solución:**
1. Verifica que BFF esté en http://localhost:8080
2. Revisa la consola del BFF para ver errores

---

### Frontend no carga variables de entorno
**Causa:** `.env.local` no existe o tiene nombre incorrecto.

**Solución:**
```powershell
cd frontend
# Verifica que exista
Get-Item .env.local

# Si no existe, cópialo del ejemplo
Copy-Item .env.local.example .env.local
# Luego edita .env.local con tus IDs
```

**IMPORTANTE:** Después de editar `.env.local`, **reinicia Vite** (Ctrl+C y `npm run dev` de nuevo).

---

## 📊 Logs Útiles

### BFF Logs (Terminal 2)
Busca estas líneas:
```
DEBUG cl.duoc.api : Validating JWT token
INFO  cl.duoc.api.controller.DataController : GET /api/data called
DEBUG cl.duoc.api.repository : Calling http://localhost:8081/clientes
```

### Orders Logs (Terminal 1)
Busca estas líneas:
```
INFO cl.duoc.orders.controller : Solicitud recibida en /clientes
INFO cl.duoc.orders.controller : Retornando cliente: Wacoldo Soto
```

---

## ✅ Flujo Completo Verificado

Si ves los datos de Wacoldo Soto en el navegador, significa que:

1. ✅ Frontend se autenticó con Azure AD
2. ✅ Frontend obtuvo un token JWT válido
3. ✅ Frontend llamó al BFF con el token
4. ✅ BFF validó el token con Azure AD
5. ✅ BFF llamó al microservicio Orders
6. ✅ Orders retornó los datos
7. ✅ BFF devolvió los datos al Frontend
8. ✅ Frontend mostró los datos

**🎉 ¡TODO FUNCIONA!**

---

## 📝 Notas Importantes

- **Parte 1**: Solo necesitas autenticación + token. La consulta a API puede estar deshabilitada.
- **Parte 2**: El flujo completo Frontend → BFF → Orders debe funcionar.
- **Datos dummy**: Es correcto que Orders retorne datos hardcodeados. El objetivo es demostrar que la **comunicación funciona**.
- **No es versión final**: Este es solo para demostrar el concepto, no la implementación completa.

---

## 🎯 Qué Mostrar en la Demo

1. **Abrir las 3 terminales** mostrando que los 3 servicios están corriendo
2. **Abrir el navegador** en http://localhost:5173
3. **Hacer login** con Azure AD
4. **Obtener token** (mostrar que expira en X tiempo)
5. **Consultar BFF** y **mostrar los datos de Wacoldo Soto**
6. **Abrir DevTools → Network** para mostrar las llamadas HTTP
7. **Mostrar los logs** en las terminales del BFF y Orders

**Con eso demuestras que todo el flujo funciona end-to-end.**

---

¿Algún problema? Revisa `INSTRUCCIONES_SETUP.md` para configuración detallada.
