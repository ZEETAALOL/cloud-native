package cl.duoc.api.service;

import cl.duoc.api.dto.ReportDataResponse;
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
public class ReportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private static final String CIRCUIT_BREAKER_NAME = "reportService";
    
    private final RestClient restClient;
    
    public ReportService(@Value("${microservices.report.base-url}") String reportBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(reportBaseUrl)
                .build();
        logger.info("ReportService inicializado con URL: {}", reportBaseUrl);
    }
    
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getSalesReportFallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public ReportDataResponse getSalesReport() {
        logger.debug("Obteniendo reporte de ventas desde microservicio Report");
        return restClient.get()
                .uri("/api/reports/sales")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ReportDataResponse.class);
    }
    
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getInventoryReportFallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public ReportDataResponse getInventoryReport() {
        logger.debug("Obteniendo reporte de inventario desde microservicio Report");
        return restClient.get()
                .uri("/api/reports/inventory")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ReportDataResponse.class);
    }
    
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getCustomerReportFallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public ReportDataResponse getCustomerReport() {
        logger.debug("Obteniendo reporte de clientes desde microservicio Report");
        return restClient.get()
                .uri("/api/reports/customers")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ReportDataResponse.class);
    }
    
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getPerformanceReportFallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public ReportDataResponse getPerformanceReport() {
        logger.debug("Obteniendo reporte de rendimiento desde microservicio Report");
        return restClient.get()
                .uri("/api/reports/performance")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ReportDataResponse.class);
    }
    
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getAvailableReportTypesFallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public List<String> getAvailableReportTypes() {
        logger.debug("Obteniendo tipos de reportes disponibles desde microservicio Report");
        return restClient.get()
                .uri("/api/reports/types")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    // ===== FALLBACK METHODS =====

    private ReportDataResponse getSalesReportFallback(Exception ex) {
        logger.error("⚠️ Circuit Breaker ACTIVADO - Servicio Report no disponible para reporte de ventas", ex);
        ReportDataResponse fallback = new ReportDataResponse();
        fallback.setReportType("SALES");
        fallback.setTitle("Servicio no disponible");
        fallback.setStatus("UNAVAILABLE");
        return fallback;
    }

    private ReportDataResponse getInventoryReportFallback(Exception ex) {
        logger.error("⚠️ Circuit Breaker ACTIVADO - Servicio Report no disponible para reporte de inventario", ex);
        ReportDataResponse fallback = new ReportDataResponse();
        fallback.setReportType("INVENTORY");
        fallback.setTitle("Servicio no disponible");
        fallback.setStatus("UNAVAILABLE");
        return fallback;
    }

    private ReportDataResponse getCustomerReportFallback(Exception ex) {
        logger.error("⚠️ Circuit Breaker ACTIVADO - Servicio Report no disponible para reporte de clientes", ex);
        ReportDataResponse fallback = new ReportDataResponse();
        fallback.setReportType("CUSTOMER");
        fallback.setTitle("Servicio no disponible");
        fallback.setStatus("UNAVAILABLE");
        return fallback;
    }

    private ReportDataResponse getPerformanceReportFallback(Exception ex) {
        logger.error("⚠️ Circuit Breaker ACTIVADO - Servicio Report no disponible para reporte de rendimiento", ex);
        ReportDataResponse fallback = new ReportDataResponse();
        fallback.setReportType("PERFORMANCE");
        fallback.setTitle("Servicio no disponible");
        fallback.setStatus("UNAVAILABLE");
        return fallback;
    }

    private List<String> getAvailableReportTypesFallback(Exception ex) {
        logger.error("⚠️ Circuit Breaker ACTIVADO - No se pueden obtener tipos de reportes", ex);
        return new ArrayList<>();
    }
}
