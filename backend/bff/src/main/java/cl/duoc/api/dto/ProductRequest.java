package cl.duoc.api.dto;

import java.math.BigDecimal;

public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    private Boolean active;

    public ProductRequest() {}

    public ProductRequest(String name, String description, BigDecimal price, 
                         Integer stock, String category, Boolean active) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.active = active;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
