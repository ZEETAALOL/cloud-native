// Script de inicialización de MongoDB para Pedidos360
// Se ejecuta automáticamente al crear el contenedor

print('🔧 Inicializando MongoDB para Pedidos360...');

// Usar la base de datos pedidos360
db = db.getSiblingDB('pedidos360');

// Crear colección de eventos de auditoría
db.createCollection('audit_events');

// Crear índices para mejorar performance
db.audit_events.createIndex({ "userId": 1 });
db.audit_events.createIndex({ "action": 1 });
db.audit_events.createIndex({ "entity": 1 });
db.audit_events.createIndex({ "timestamp": -1 });
db.audit_events.createIndex({ "userId": 1, "timestamp": -1 });

// Insertar eventos de ejemplo
db.audit_events.insertMany([
    {
        userId: "system",
        action: "SYSTEM_START",
        entity: "System",
        details: "Sistema Pedidos360 iniciado en EC2",
        timestamp: new Date(),
        ipAddress: "10.0.0.1"
    },
    {
        userId: "admin",
        action: "DATABASE_INIT",
        entity: "Database",
        details: "Base de datos MongoDB inicializada correctamente",
        timestamp: new Date(),
        ipAddress: "10.0.0.1"
    },
    {
        userId: "system",
        action: "HEALTH_CHECK",
        entity: "Monitoring",
        details: "Verificación de salud del sistema",
        timestamp: new Date(),
        ipAddress: "10.0.0.2"
    }
]);

// Crear colección para notificaciones
db.createCollection('notifications');

// Crear índices para notificaciones
db.notifications.createIndex({ "recipient": 1 });
db.notifications.createIndex({ "channel": 1 });
db.notifications.createIndex({ "status": 1 });
db.notifications.createIndex({ "sentAt": -1 });

// Mensajes de confirmación
var auditCount = db.audit_events.countDocuments();
print('✅ MongoDB inicializado correctamente');
print('📊 ' + auditCount + ' eventos de auditoría de ejemplo');
print('🔍 Índices creados para audit_events y notifications');
print('🎉 Pedidos360 MongoDB listo para usar');
