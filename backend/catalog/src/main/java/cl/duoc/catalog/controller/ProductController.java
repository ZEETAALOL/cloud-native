package cl.duoc.catalog.controller;

import cl.duoc.catalog.dto.ProductRequest;
import cl.duoc.catalog.model.Product;
import cl.duoc.catalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productRepository.findByCategory(category));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Product>> getActiveProducts() {
        return ResponseEntity.ok(productRepository.findByActive(true));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
        Product product = new Product(
            null,
            request.getName(),
            request.getDescription(),
            request.getPrice(),
            request.getStock(),
            request.getCategory(),
            request.getActive() != null ? request.getActive() : true
        );
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Product product = new Product(
            id,
            request.getName(),
            request.getDescription(),
            request.getPrice(),
            request.getStock(),
            request.getCategory(),
            request.getActive() != null ? request.getActive() : true
        );
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/products/{id}/stock - Actualizar stock
     * Body: { "quantity": 10, "operation": "INCREMENT" | "DECREMENT" }
     * 
     * Usado por Orders para decrementar stock al aceptar pedido
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(
            @PathVariable Long id, 
            @RequestBody StockUpdateRequest request) {
        
        Product product = productRepository.findById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        int currentStock = product.getStock();
        int newStock;

        if ("DECREMENT".equals(request.getOperation())) {
            newStock = currentStock - request.getQuantity();
            if (newStock < 0) {
                return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Stock insuficiente"));
            }
        } else if ("INCREMENT".equals(request.getOperation())) {
            newStock = currentStock + request.getQuantity();
        } else {
            return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", "Operación inválida. Usar INCREMENT o DECREMENT"));
        }

        product.setStock(newStock);
        Product updated = productRepository.save(product);
        return ResponseEntity.ok(updated);
    }

    // DTO para actualización de stock
    public static class StockUpdateRequest {
        private Integer quantity;
        private String operation; // INCREMENT o DECREMENT

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }
    }
}
