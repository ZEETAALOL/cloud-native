package cl.duoc.catalog.repository;

import cl.duoc.catalog.model.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    private final ConcurrentHashMap<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ProductRepository() {
        // Datos de ejemplo
        save(new Product(null, "Laptop Dell XPS 15", "Laptop profesional de alto rendimiento", 
            new BigDecimal("1299.99"), 15, "Electrónica", true));
        save(new Product(null, "Mouse Logitech MX Master 3", "Mouse inalámbrico ergonómico", 
            new BigDecimal("99.99"), 50, "Accesorios", true));
        save(new Product(null, "Teclado Mecánico Keychron K2", "Teclado mecánico retroiluminado", 
            new BigDecimal("89.99"), 30, "Accesorios", true));
        save(new Product(null, "Monitor LG UltraWide 34\"", "Monitor curvo 21:9 WQHD", 
            new BigDecimal("599.99"), 8, "Electrónica", true));
        save(new Product(null, "Webcam Logitech C920", "Webcam Full HD 1080p", 
            new BigDecimal("79.99"), 25, "Accesorios", true));
        save(new Product(null, "Auriculares Sony WH-1000XM4", "Auriculares con cancelación de ruido", 
            new BigDecimal("349.99"), 12, "Audio", true));
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.getAndIncrement());
        }
        products.put(product.getId(), product);
        return product;
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public List<Product> findByCategory(String category) {
        return products.values().stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }

    public List<Product> findByActive(Boolean active) {
        return products.values().stream()
            .filter(p -> p.getActive().equals(active))
            .collect(Collectors.toList());
    }

    public Product findById(Long id) {
        return products.get(id);
    }

    public void deleteById(Long id) {
        products.remove(id);
    }

    public boolean existsById(Long id) {
        return products.containsKey(id);
    }
}
