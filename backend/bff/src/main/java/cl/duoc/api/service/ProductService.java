package cl.duoc.api.service;

import cl.duoc.api.dto.ProductRequest;
import cl.duoc.api.dto.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String CIRCUIT_BREAKER_NAME = "catalogService";
    
    private final RestClient restClient;

    public ProductService(@Value("${microservices.catalog.base-url}") String catalogBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(catalogBaseUrl)
                .build();
        logger.info("ProductService inicializado con URL: {}", catalogBaseUrl);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "obtenerTodosFallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public List<ProductResponse> obtenerTodos() {
        logger.debug("Obteniendo todos los productos desde microservicio Catalog");
        return restClient.get()
                .uri("/api/products")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductResponse>>() {});
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "obtenerPorIdFallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public ProductResponse obtenerPorId(Long id) {
        logger.debug("Obteniendo producto con id: {} desde microservicio Catalog", id);
        return restClient.get()
                .uri("/api/products/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ProductResponse.class);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "obtenerPorCategoriaFallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public List<ProductResponse> obtenerPorCategoria(String category) {
        logger.debug("Obteniendo productos de categoría: {} desde microservicio Catalog", category);
        return restClient.get()
                .uri("/api/products/category/{category}", category)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductResponse>>() {});
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "obtenerActivosFallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public List<ProductResponse> obtenerActivos() {
        logger.debug("Obteniendo productos activos desde microservicio Catalog");
        return restClient.get()
                .uri("/api/products/active")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductResponse>>() {});
    }

    public ProductResponse crear(ProductRequest request) {
        logger.debug("Creando nuevo producto en microservicio Catalog");
        return restClient.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ProductResponse.class);
    }

    public void actualizar(Long id, ProductRequest request) {
        logger.debug("Actualizando producto {} en microservicio Catalog", id);
        restClient.put()
                .uri("/api/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void eliminar(Long id) {
        logger.debug("Eliminando producto {} en microservicio Catalog", id);
        restClient.delete()
                .uri("/api/products/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    // ===== FALLBACK METHODS =====

    private List<ProductResponse> obtenerTodosFallback(Exception ex) {
        logger.error("⚠️ Circuit Breaker ACTIVADO - Servicio Catalog no disponible", ex);
        return new ArrayList<>(); // Retorna lista vacía en caso de falla
    }

    private ProductResponse obtenerPorIdFallback(Long id, Exception ex) {
        logger.error("⚠️ Circuit Breaker ACTIVADO - No se puede obtener producto {}", id, ex);
        return null; // Retorna null, el controller debe manejar esto
    }

    private List<ProductResponse> obtenerPorCategoriaFallback(String category, Exception ex) {
        logger.error("⚠️ Circuit Breaker ACTIVADO - No se pueden obtener productos de categoría {}", category, ex);
        return new ArrayList<>();
    }

    private List<ProductResponse> obtenerActivosFallback(Exception ex) {
        logger.error("⚠️ Circuit Breaker ACTIVADO - No se pueden obtener productos activos", ex);
        return new ArrayList<>();
    }
}
