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
        // Datos de ejemplo
        save(new AuditEvent(null, "user1", "LOGIN", "Usuario", "Usuario inició sesión", 
            LocalDateTime.now().minusHours(2), "192.168.1.100"));
        save(new AuditEvent(null, "user2", "CREATE_ORDER", "Pedido", "Creó pedido #1234", 
            LocalDateTime.now().minusHours(1), "192.168.1.101"));
        save(new AuditEvent(null, "user1", "UPDATE_PROFILE", "Perfil", "Actualizó información personal", 
            LocalDateTime.now().minusMinutes(30), "192.168.1.100"));
        save(new AuditEvent(null, "admin", "DELETE_PRODUCT", "Producto", "Eliminó producto #567", 
            LocalDateTime.now().minusMinutes(15), "192.168.1.50"));
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
