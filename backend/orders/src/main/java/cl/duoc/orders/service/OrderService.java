package cl.duoc.orders.service;

import cl.duoc.orders.dto.CreateOrderRequest;
import cl.duoc.orders.dto.UpdateOrderStatusRequest;
import cl.duoc.orders.model.Order;
import cl.duoc.orders.model.OrderStatus;
import cl.duoc.orders.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Crear un nuevo pedido
     */
    public Order createOrder(CreateOrderRequest request, String createdBy) {
        log.info("Creando pedido para cliente: {}", request.getCustomerName());
        
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setItems(request.getItems());
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.CREADO);
        order.setCreatedBy(createdBy != null ? createdBy : "system");
        order.calculateTotal();

        Order saved = orderRepository.save(order);
        log.info("Pedido creado con ID: {}, Total: ${}", saved.getId(), saved.getTotalAmount());
        
        // TODO: Publicar evento OrderCreated a Kafka
        // TODO: Enviar notificación email a RabbitMQ
        
        return saved;
    }

    /**
     * Obtener pedido por ID
     */
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Obtener todos los pedidos
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Cambiar estado del pedido con validación de flujo
     */
    public Order updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        log.info("Actualizando estado del pedido {} a {}", orderId, request.getStatus());
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId));

        // Validar transición de estado
        if (!order.canTransitionTo(request.getStatus())) {
            String message = String.format(
                "Transición de estado inválida: %s -> %s", 
                order.getStatus(), 
                request.getStatus()
            );
            log.error(message);
            throw new IllegalStateException(message);
        }

        OrderStatus oldStatus = order.getStatus();
        order.updateStatus(request.getStatus(), request.getUpdatedBy());
        
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }

        Order updated = orderRepository.save(order);
        log.info("Estado actualizado: {} -> {}", oldStatus, updated.getStatus());

        // TODO: Publicar evento de cambio de estado a Kafka
        // TODO: Si es ACEPTADO, decrementar stock en Catalog
        // TODO: Enviar notificación al cliente
        
        return updated;
    }

    /**
     * Filtrar pedidos por estado
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Filtrar pedidos por cliente
     */
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    /**
     * Filtrar pedidos por rango de fechas
     */
    public List<Order> getOrdersByDateRange(LocalDateTime from, LocalDateTime to) {
        return orderRepository.findByDateRange(from, to);
    }

    /**
     * Filtrar pedidos por estado y rango de fechas
     */
    public List<Order> getOrdersByStatusAndDateRange(OrderStatus status, LocalDateTime from, LocalDateTime to) {
        return orderRepository.findByStatusAndDateRange(status, from, to);
    }

    /**
     * Cancelar pedido
     */
    public Order cancelOrder(Long orderId, String canceledBy, String reason) {
        log.info("Cancelando pedido {}", orderId);
        
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.CANCELADO);
        request.setUpdatedBy(canceledBy);
        request.setNotes("Cancelado: " + reason);
        
        return updateOrderStatus(orderId, request);
    }

    /**
     * Obtener estadísticas
     */
    public OrderStats getStats() {
        List<Order> allOrders = orderRepository.findAll();
        
        long total = allOrders.size();
        long creados = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.CREADO).count();
        long aceptados = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.ACEPTADO).count();
        long enPreparacion = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.EN_PREPARACION).count();
        long despachados = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.DESPACHADO).count();
        long entregados = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.ENTREGADO).count();
        long cancelados = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELADO).count();
        
        return new OrderStats(total, creados, aceptados, enPreparacion, despachados, entregados, cancelados);
    }

    public record OrderStats(
        long total,
        long creados,
        long aceptados,
        long enPreparacion,
        long despachados,
        long entregados,
        long cancelados
    ) {}
}
