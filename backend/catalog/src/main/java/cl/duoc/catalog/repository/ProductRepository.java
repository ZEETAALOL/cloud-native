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
        // Catálogo inicial de Pedidos360
        save(new Product(null, "Smartphone Samsung Galaxy S24", "Teléfono inteligente última generación", 
            new BigDecimal("899000"), 25, "Tecnología", true));
        save(new Product(null, "Notebook HP Pavilion", "Computador portátil para trabajo y estudio", 
            new BigDecimal("650000"), 15, "Tecnología", true));
        save(new Product(null, "Smart TV LG 55 pulgadas", "Televisor inteligente 4K UHD", 
            new BigDecimal("550000"), 10, "Electrodomésticos", true));
        save(new Product(null, "Refrigerador Samsung", "Refrigerador No Frost 300L", 
            new BigDecimal("450000"), 8, "Electrodomésticos", true));
        save(new Product(null, "Aspiradora Robot Xiaomi", "Robot aspiradora inteligente", 
            new BigDecimal("250000"), 20, "Hogar", true));
        save(new Product(null, "Cafetera Nespresso", "Cafetera de cápsulas automática", 
            new BigDecimal("120000"), 30, "Hogar", true));
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
