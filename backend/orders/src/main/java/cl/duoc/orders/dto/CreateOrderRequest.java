package cl.duoc.orders.dto;

import cl.duoc.orders.model.OrderItem;
import java.util.List;

public class CreateOrderRequest {
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String deliveryAddress;
    private List<OrderItem> items;
    private String notes;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(String customerId, String customerName, String customerEmail, 
                             String deliveryAddress, List<OrderItem> items, String notes) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.deliveryAddress = deliveryAddress;
        this.items = items;
        this.notes = notes;
    }

    // Getters y Setters
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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
