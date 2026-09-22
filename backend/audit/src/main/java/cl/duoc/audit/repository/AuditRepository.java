package cl.duoc.audit.repository;

import cl.duoc.audit.model.AuditEvent;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class AuditRepository {
    private final ConcurrentHashMap<Long, AuditEvent> events = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public AuditRepository() {
        // Eventos iniciales del sistema Pedidos360
        save(new AuditEvent(null, "sistema", "INICIO_SISTEMA", "Sistema", "Sistema Pedidos360 iniciado", 
            LocalDateTime.now().minusHours(2), "10.0.0.1"));
        save(new AuditEvent(null, "admin", "CONFIGURACION", "Sistema", "Configuración inicial completada", 
            LocalDateTime.now().minusHours(1), "10.0.0.1"));
        save(new AuditEvent(null, "bff", "HEALTH_CHECK", "Monitoreo", "Verificación de servicios", 
            LocalDateTime.now().minusMinutes(30), "10.0.0.2"));
        save(new AuditEvent(null, "catalog", "SYNC_PRODUCTOS", "Catálogo", "Sincronización de productos completada", 
            LocalDateTime.now().minusMinutes(15), "10.0.0.3"));
    }

    public AuditEvent save(AuditEvent event) {
        if (event.getId() == null) {
            event.setId(idGenerator.getAndIncrement());
        }
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }
        events.put(event.getId(), event);
        return event;
    }

    public List<AuditEvent> findAll() {
        return new ArrayList<>(events.values());
    }

    public List<AuditEvent> findByUserId(String userId) {
        return events.values().stream()
            .filter(e -> e.getUserId().equals(userId))
            .collect(Collectors.toList());
    }

    public List<AuditEvent> findByAction(String action) {
        return events.values().stream()
            .filter(e -> e.getAction().equals(action))
            .collect(Collectors.toList());
    }

    public AuditEvent findById(Long id) {
        return events.get(id);
    }
}
