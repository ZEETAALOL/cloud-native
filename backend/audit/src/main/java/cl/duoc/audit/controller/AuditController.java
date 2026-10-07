package cl.duoc.audit.controller;

import cl.duoc.audit.dto.AuditRequest;
import cl.duoc.audit.model.AuditEvent;
import cl.duoc.audit.repository.AuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller de Auditoría - Endpoints read-only según especificación del caso PDF
 * 
 * GET /api/audit/* (read-only)
 * Timeline de eventos de negocio (quién creó/aceptó/entregó un pedido, etc.)
 * Permite filtros: usuario, rango de fechas, tipo de evento
 */
@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private static final Logger log = LoggerFactory.getLogger(AuditController.class);

    @Autowired
    private AuditRepository auditRepository;

    /**
     * GET /api/audit - Obtener todos los eventos (con filtros opcionales)
     */
    @GetMapping
    public ResponseEntity<List<AuditEvent>> getAllEvents(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        
        log.info("GET /api/audit - Filtros: userId={}, action={}, entity={}, from={}, to={}", 
            userId, action, entity, from, to);

        List<AuditEvent> events;

        // Aplicar filtros según parámetros
        if (userId != null && from != null && to != null) {
            events = auditRepository.findByUserIdAndDateRange(userId, from, to);
        } else if (action != null && from != null && to != null) {
            events = auditRepository.findByActionAndDateRange(action, from, to);
        } else if (entity != null && from != null && to != null) {
            events = auditRepository.findByEntityAndDateRange(entity, from, to);
        } else if (from != null && to != null) {
            events = auditRepository.findByDateRange(from, to);
        } else if (userId != null) {
            events = auditRepository.findByUserId(userId);
        } else if (action != null) {
            events = auditRepository.findByAction(action);
        } else if (entity != null) {
            events = auditRepository.findByEntity(entity);
        } else {
            events = auditRepository.findAll();
        }

        log.info("Retornando {} eventos de auditoría", events.size());
        return ResponseEntity.ok(events);
    }

    /**
     * GET /api/audit/{id} - Obtener evento específico
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuditEvent> getEventById(@PathVariable Long id) {
        log.info("GET /api/audit/{}", id);
        
        AuditEvent event = auditRepository.findById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

    /**
     * GET /api/audit/user/{userId} - Timeline de un usuario específico
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditEvent>> getEventsByUser(@PathVariable String userId) {
        log.info("GET /api/audit/user/{}", userId);
        return ResponseEntity.ok(auditRepository.findByUserId(userId));
    }

    /**
     * GET /api/audit/action/{action} - Filtrar por tipo de acción
     */
    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditEvent>> getEventsByAction(@PathVariable String action) {
        log.info("GET /api/audit/action/{}", action);
        return ResponseEntity.ok(auditRepository.findByAction(action));
    }

    /**
     * GET /api/audit/entity/{entity} - Filtrar por entidad (Order, Product, etc.)
     */
    @GetMapping("/entity/{entity}")
    public ResponseEntity<List<AuditEvent>> getEventsByEntity(@PathVariable String entity) {
        log.info("GET /api/audit/entity/{}", entity);
        return ResponseEntity.ok(auditRepository.findByEntity(entity));
    }

    /**
     * GET /api/audit/order/{orderId} - Timeline de un pedido específico
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<AuditEvent>> getOrderTimeline(@PathVariable String orderId) {
        log.info("GET /api/audit/order/{}", orderId);
        return ResponseEntity.ok(auditRepository.findByEntityAndEntityId("Order", orderId));
    }

    /**
     * GET /api/audit/recent - Eventos más recientes
     */
    @GetMapping("/recent")
    public ResponseEntity<List<AuditEvent>> getRecentEvents(
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        
        log.info("GET /api/audit/recent?limit={}", limit);
        List<AuditEvent> events = auditRepository.findRecent(limit);
        return ResponseEntity.ok(events);
    }

    /**
     * POST /api/audit - Crear evento de auditoría (solo para uso interno)
     */
    @PostMapping
    public ResponseEntity<AuditEvent> createEvent(@RequestBody AuditRequest request) {
        log.info("POST /api/audit - Creando evento: {} por {}", request.getAction(), request.getUserId());
        
        AuditEvent event = new AuditEvent(
            null,
            request.getUserId(),
            request.getAction(),
            request.getEntity(),
            request.getDetails(),
            LocalDateTime.now(),
            request.getIpAddress()
        );
        AuditEvent saved = auditRepository.save(event);
        return ResponseEntity.ok(saved);
    }
}
