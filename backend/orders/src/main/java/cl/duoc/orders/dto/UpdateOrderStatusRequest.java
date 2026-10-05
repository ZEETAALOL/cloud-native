package cl.duoc.orders.dto;

import cl.duoc.orders.model.OrderStatus;

public class UpdateOrderStatusRequest {
    private OrderStatus status;
    private String updatedBy;
    private String notes;

    public UpdateOrderStatusRequest() {
    }

    public UpdateOrderStatusRequest(OrderStatus status, String updatedBy, String notes) {
        this.status = status;
        this.updatedBy = updatedBy;
        this.notes = notes;
    }

    // Getters y Setters
    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
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
