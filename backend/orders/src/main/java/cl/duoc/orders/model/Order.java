package cl.duoc.orders.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Order - Gestión de Pedidos
 * Estados: CREADO -> ACEPTADO -> EN_PREPARACION -> DESPACHADO -> ENTREGADO / CANCELADO
 */
public class Order {
    private Long id;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String deliveryAddress;
    private OrderStatus status;
    private List<OrderItem> items;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String notes;

    public Order() {
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREADO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Order(Long id, String customerId, String customerName, String customerEmail, 
                 String deliveryAddress, OrderStatus status, List<OrderItem> items, 
                 Double totalAmount, LocalDateTime createdAt, LocalDateTime updatedAt,
                 String createdBy, String updatedBy, String notes) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.items = items != null ? items : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.notes = notes;
    }

    // Validación de transición de estados según reglas del caso
    public boolean canTransitionTo(OrderStatus newStatus) {
        if (this.status == newStatus) {
            return false;
        }

        return switch (this.status) {
            case CREADO -> newStatus == OrderStatus.ACEPTADO || newStatus == OrderStatus.CANCELADO;
            case ACEPTADO -> newStatus == OrderStatus.EN_PREPARACION || newStatus == OrderStatus.CANCELADO;
            case EN_PREPARACION -> newStatus == OrderStatus.DESPACHADO || newStatus == OrderStatus.CANCELADO;
            case DESPACHADO -> newStatus == OrderStatus.ENTREGADO || newStatus == OrderStatus.CANCELADO;
            case ENTREGADO, CANCELADO -> false; // Estados finales
        };
    }

    public void updateStatus(OrderStatus newStatus, String updatedBy) {
        if (!canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("No se puede cambiar de estado %s a %s", this.status, newStatus)
            );
        }
        this.status = newStatus;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void calculateTotal() {
        this.totalAmount = items.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        calculateTotal();
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
