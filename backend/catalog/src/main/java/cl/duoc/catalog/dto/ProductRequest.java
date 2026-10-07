package cl.duoc.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    private Boolean active;
}
