package cl.duoc.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

/**
 * BFF Controller para Orders - Delega al microservicio ms-pedidos360-orders
 * Con Circuit Breaker para tolerancia a fallos
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Value("${microservices.orders.base-url:http://localhost:8081}")
    private String ordersBaseUrl;

    private final RestClient restClient;

    public OrderController() {
        this.restClient = RestClient.builder().build();
    }

    /**
     * POST /api/orders - Crear pedido
     */
    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody Object request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: POST /api/orders - Creando pedido");
        
        try {
            return restClient.post()
                .uri(ordersBaseUrl + "/api/orders")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error llamando a Orders service", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de pedidos no disponible");
        }
    }

    /**
     * GET /api/orders/{id} - Obtener pedido por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/orders/{}", id);
        
        try {
            return restClient.get()
                .uri(ordersBaseUrl + "/api/orders/" + id)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo pedido {}", id, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de pedidos no disponible");
        }
    }

    /**
     * GET /api/orders - Listar/filtrar pedidos
     */
    @GetMapping
    public ResponseEntity<?> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String customerId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/orders - Filtrando: status={}, from={}, to={}, customerId={}", 
            status, from, to, customerId);
        
        try {
            StringBuilder uriBuilder = new StringBuilder(ordersBaseUrl + "/api/orders?");
            if (status != null) uriBuilder.append("status=").append(status).append("&");
            if (from != null) uriBuilder.append("from=").append(from).append("&");
            if (to != null) uriBuilder.append("to=").append(to).append("&");
            if (customerId != null) uriBuilder.append("customerId=").append(customerId).append("&");
            
            return restClient.get()
                .uri(uriBuilder.toString())
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo pedidos", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de pedidos no disponible");
        }
    }

    /**
     * PUT /api/orders/{id}/status - Cambiar estado del pedido
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Object request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: PUT /api/orders/{}/status", id);
        
        try {
            return restClient.put()
                .uri(ordersBaseUrl + "/api/orders/" + id + "/status")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error actualizando estado del pedido {}", id, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de pedidos no disponible");
        }
    }

    /**
     * DELETE /api/orders/{id} - Cancelar pedido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: DELETE /api/orders/{}", id);
        
        try {
            String uri = ordersBaseUrl + "/api/orders/" + id;
            if (reason != null) {
                uri += "?reason=" + reason;
            }
            
            return restClient.delete()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error cancelando pedido {}", id, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de pedidos no disponible");
        }
    }

    /**
     * GET /api/orders/stats - Estadísticas de pedidos
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/orders/stats");
        
        try {
            return restClient.get()
                .uri(ordersBaseUrl + "/api/orders/stats")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo estadísticas de pedidos", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de pedidos no disponible");
        }
    }
}
