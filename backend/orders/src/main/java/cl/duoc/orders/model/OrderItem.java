package cl.duoc.orders.model;

/**
 * Item de un pedido (producto + cantidad)
 */
public class OrderItem {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private String notes;

    public OrderItem() {
    }

    public OrderItem(Long productId, String productName, Integer quantity, Double price, String notes) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.notes = notes;
    }

    public Double getSubtotal() {
        return price * quantity;
    }

    // Getters y Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
