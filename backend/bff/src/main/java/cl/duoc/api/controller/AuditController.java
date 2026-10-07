package cl.duoc.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

/**
 * BFF Controller para Audit - Delega al microservicio ms-pedidos360-audit
 * Endpoints read-only según caso PDF
 */
@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private static final Logger log = LoggerFactory.getLogger(AuditController.class);

    @Value("${microservices.audit.base-url:http://localhost:8083}")
    private String auditBaseUrl;

    private final RestClient restClient;

    public AuditController() {
        this.restClient = RestClient.builder().build();
    }

    /**
     * GET /api/audit - Obtener todos los eventos con filtros opcionales
     */
    @GetMapping
    public ResponseEntity<?> getAllEvents(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entity,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/audit - Filtros: userId={}, action={}, entity={}", userId, action, entity);
        
        try {
            StringBuilder uriBuilder = new StringBuilder(auditBaseUrl + "/api/audit?");
            if (userId != null) uriBuilder.append("userId=").append(userId).append("&");
            if (action != null) uriBuilder.append("action=").append(action).append("&");
            if (entity != null) uriBuilder.append("entity=").append(entity).append("&");
            if (from != null) uriBuilder.append("from=").append(from).append("&");
            if (to != null) uriBuilder.append("to=").append(to).append("&");
            
            return restClient.get()
                .uri(uriBuilder.toString())
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo eventos de auditoría", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de auditoría no disponible");
        }
    }

    /**
     * GET /api/audit/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/audit/{}", id);
        
        try {
            return restClient.get()
                .uri(auditBaseUrl + "/api/audit/" + id)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo evento {}", id, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de auditoría no disponible");
        }
    }

    /**
     * GET /api/audit/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getEventsByUser(
            @PathVariable String userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/audit/user/{}", userId);
        
        try {
            return restClient.get()
                .uri(auditBaseUrl + "/api/audit/user/" + userId)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo eventos del usuario {}", userId, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de auditoría no disponible");
        }
    }

    /**
     * GET /api/audit/action/{action}
     */
    @GetMapping("/action/{action}")
    public ResponseEntity<?> getEventsByAction(
            @PathVariable String action,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/audit/action/{}", action);
        
        try {
            return restClient.get()
                .uri(auditBaseUrl + "/api/audit/action/" + action)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo eventos por acción {}", action, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de auditoría no disponible");
        }
    }

    /**
     * GET /api/audit/order/{orderId} - Timeline de un pedido
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderTimeline(
            @PathVariable String orderId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/audit/order/{}", orderId);
        
        try {
            return restClient.get()
                .uri(auditBaseUrl + "/api/audit/order/" + orderId)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo timeline del pedido {}", orderId, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de auditoría no disponible");
        }
    }

    /**
     * GET /api/audit/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentEvents(
            @RequestParam(required = false, defaultValue = "50") Integer limit,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/audit/recent?limit={}", limit);
        
        try {
            return restClient.get()
                .uri(auditBaseUrl + "/api/audit/recent?limit=" + limit)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo eventos recientes", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de auditoría no disponible");
        }
    }

    /**
     * POST /api/audit - Registrar evento (uso interno)
     */
    @PostMapping
    public ResponseEntity<?> createEvent(
            @RequestBody Object request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: POST /api/audit");
        
        try {
            return restClient.post()
                .uri(auditBaseUrl + "/api/audit")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error registrando evento de auditoría", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de auditoría no disponible");
        }
    }
}
