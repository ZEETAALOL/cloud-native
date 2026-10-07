package cl.duoc.orders.controller;

import cl.duoc.orders.dto.CreateOrderRequest;
import cl.duoc.orders.dto.OrderResponse;
import cl.duoc.orders.dto.UpdateOrderStatusRequest;
import cl.duoc.orders.model.Order;
import cl.duoc.orders.model.OrderStatus;
import cl.duoc.orders.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller de Órdenes - Endpoints según especificación del caso PDF
 * 
 * Endpoints esenciales:
 * - POST /api/orders (crear pedido)
 * - GET /api/orders/{id}
 * - PUT /api/orders/{id}/status body: { "status": "CREADO|ACEPTADO|EN_PREPARACION|DESPACHADO|ENTREGADO|CANCELADO" }
 * - GET /api/orders?status=...&from=...&to=...
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * POST /api/orders - Crear pedido
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        
        try {
            log.info("POST /api/orders - Creando pedido para: {}", request.getCustomerName());
            Order order = orderService.createOrder(request, userId);
            OrderResponse response = OrderResponse.fromOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creando pedido", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/orders/{id} - Obtener pedido por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.info("GET /api/orders/{} - Obteniendo pedido", id);
        
        return orderService.getOrderById(id)
            .map(order -> ResponseEntity.ok(OrderResponse.fromOrder(order)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/orders/{id}/status - Cambiar estado del pedido
     * Body: { "status": "CREADO|ACEPTADO|EN_PREPARACION|DESPACHADO|ENTREGADO|CANCELADO" }
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        
        try {
            log.info("PUT /api/orders/{}/status - Cambiando a {}", id, request.getStatus());
            
            if (request.getUpdatedBy() == null) {
                request.setUpdatedBy(userId != null ? userId : "system");
            }
            
            Order updated = orderService.updateOrderStatus(id, request);
            OrderResponse response = OrderResponse.fromOrder(updated);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Pedido no encontrado: {}", id);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.error("Transición de estado inválida: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error actualizando estado del pedido", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * GET /api/orders?status=...&from=...&to=... - Filtrar pedidos
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String customerId) {
        
        log.info("GET /api/orders - Filtrando: status={}, from={}, to={}, customerId={}", 
            status, from, to, customerId);

        List<Order> orders;

        // Filtrar por cliente
        if (customerId != null) {
            orders = orderService.getOrdersByCustomer(customerId);
        }
        // Filtrar por estado y rango de fechas
        else if (status != null && from != null && to != null) {
            orders = orderService.getOrdersByStatusAndDateRange(status, from, to);
        }
        // Filtrar solo por rango de fechas
        else if (from != null && to != null) {
            orders = orderService.getOrdersByDateRange(from, to);
        }
        // Filtrar solo por estado
        else if (status != null) {
            orders = orderService.getOrdersByStatus(status);
        }
        // Sin filtros, todos los pedidos
        else {
            orders = orderService.getAllOrders();
        }

        List<OrderResponse> responses = orders.stream()
            .map(OrderResponse::fromOrder)
            .collect(Collectors.toList());

        log.info("Retornando {} pedidos", responses.size());
        return ResponseEntity.ok(responses);
    }

    /**
     * DELETE /api/orders/{id} - Cancelar pedido (alternativa a PUT status)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(required = false, defaultValue = "Cancelado por el usuario") String reason) {
        
        try {
            log.info("DELETE /api/orders/{} - Cancelando pedido", id);
            Order canceled = orderService.cancelOrder(id, userId != null ? userId : "system", reason);
            OrderResponse response = OrderResponse.fromOrder(canceled);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error cancelando pedido", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * GET /api/orders/stats - Estadísticas de pedidos
     */
    @GetMapping("/stats")
    public ResponseEntity<OrderService.OrderStats> getStats() {
        log.info("GET /api/orders/stats - Obteniendo estadísticas");
        OrderService.OrderStats stats = orderService.getStats();
        return ResponseEntity.ok(stats);
    }
}
