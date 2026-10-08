# Informe de mejoras pendientes del proyecto

## Objetivo
Esta lista resume las mejoras que conviene hacer en el proyecto antes de integrar RabbitMQ y Kafka de forma completa.

## 1. Dejar cada microservicio realmente listo
- `orders`: conectar persistencia real y dejar validaciones fuertes para pedidos.
- `catalog`: guardar productos en una base real y controlar mejor el stock.
- `audit`: asegurar que registre eventos de forma consistente.
- `report`: reemplazar datos simulados por datos reales o por eventos.
- `notify`: dejar preparada la lógica real de envío, aunque RabbitMQ se use después.
- `bff`: mantenerlo como entrada única y revisar que todas las rutas coincidan con los servicios.

## 2. Agregar validaciones básicas
- No permitir campos vacíos en pedidos, productos y notificaciones.
- Validar cantidades, precios, estados y correos.
- Evitar que se creen datos inválidos desde el frontend o el BFF.

## 3. Unificar contratos entre servicios
- Definir claramente qué recibe y qué devuelve cada endpoint.
- Evitar usar modelos internos directamente en la API.
- Mantener DTOs separados para requests y responses.

## 4. Mejorar el manejo de errores
- Respuestas claras para `400`, `404` y `500`.
- Mensajes consistentes en todos los microservicios.
- Unificar el formato de error para que el frontend pueda manejarlo fácil.

## 5. Ordenar la documentación
- Revisar README, ENDPOINTS y descripciones de cada servicio.
- Corregir diferencias entre lo que dice la documentación y lo que realmente hace el código.
- Dejar claro qué está terminado y qué sigue pendiente.

## 6. Limpiar partes simuladas
- Quitar hardcodes innecesarios.
- Reducir datos de prueba a lo mínimo.
- Dejar solo lo que sirva para demostrar el flujo mientras no haya BD o mensajería final.

## 7. Mejorar la trazabilidad
- Agregar logs más uniformes.
- Registrar mejor cambios de estado en pedidos.
- Dejar preparado el camino para auditoría automática.

## 8. Preparar integración futura con RabbitMQ y Kafka
- Definir eventos de negocio desde ahora.
- Separar la lógica del dominio de la mensajería.
- Dejar interfaces o puntos de extensión para cuando entren colas y streaming.

## 9. Agregar pruebas
- Pruebas unitarias de servicios.
- Pruebas básicas de controladores.
- Probar especialmente:
  - creación de pedidos
  - cambio de estado
  - stock
  - consultas del BFF

## 10. Revisar despliegue y configuración
- Unificar puertos en documentación y Docker.
- Revisar variables de entorno.
- Confirmar que cada servicio arranca igual en local y en contenedor.

## Orden recomendado para avanzar
1. Validaciones básicas
2. Manejo de errores
3. Contratos y DTOs
4. Persistencia real
5. Pruebas
6. Documentación
7. Preparación RabbitMQ/Kafka
