package cl.duoc.api.controller;

import cl.duoc.api.dto.ProductRequest;
import cl.duoc.api.dto.ProductResponse;
import cl.duoc.api.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> obtenerTodos() {
        return ResponseEntity.ok(productService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productService.obtenerPorId(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> obtenerPorCategoria(@PathVariable String category) {
        return ResponseEntity.ok(productService.obtenerPorCategoria(category));
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductResponse>> obtenerActivos() {
        return ResponseEntity.ok(productService.obtenerActivos());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> crear(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody ProductRequest request) {
        productService.actualizar(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
