package cl.duoc.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

/**
 * BFF Controller para Reports - Delega al microservicio ms-pedidos360-report
 * Endpoints según caso PDF: /api/report/kpis, /api/report/top-products
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {
    
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    @Value("${microservices.report.base-url:http://localhost:8085}")
    private String reportBaseUrl;

    private final RestClient restClient;

    public ReportController() {
        this.restClient = RestClient.builder().build();
    }

    /**
     * GET /api/report/kpis?range=last24h
     */
    @GetMapping("/kpis")
    public ResponseEntity<?> getKPIs(
            @RequestParam(required = false, defaultValue = "last24h") String range,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/report/kpis?range={}", range);
        
        try {
            return restClient.get()
                .uri(reportBaseUrl + "/api/report/kpis?range=" + range)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo KPIs", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de reportes no disponible");
        }
    }

    /**
     * GET /api/report/top-products?range=last7d
     */
    @GetMapping("/top-products")
    public ResponseEntity<?> getTopProducts(
            @RequestParam(required = false, defaultValue = "last7d") String range,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/report/top-products?range={}&limit={}", range, limit);
        
        try {
            return restClient.get()
                .uri(reportBaseUrl + "/api/report/top-products?range=" + range + "&limit=" + limit)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo top productos", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de reportes no disponible");
        }
    }

    /**
     * GET /api/report/sales-by-hour
     */
    @GetMapping("/sales-by-hour")
    public ResponseEntity<?> getSalesByHour(
            @RequestParam(required = false, defaultValue = "today") String date,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/report/sales-by-hour?date={}", date);
        
        try {
            return restClient.get()
                .uri(reportBaseUrl + "/api/report/sales-by-hour?date=" + date)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo ventas por hora", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de reportes no disponible");
        }
    }

    /**
     * GET /api/report/lead-time
     */
    @GetMapping("/lead-time")
    public ResponseEntity<?> getLeadTime(
            @RequestParam(required = false, defaultValue = "last7d") String range,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/report/lead-time?range={}", range);
        
        try {
            return restClient.get()
                .uri(reportBaseUrl + "/api/report/lead-time?range=" + range)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo lead time", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de reportes no disponible");
        }
    }

    /**
     * GET /api/report/active-orders
     */
    @GetMapping("/active-orders")
    public ResponseEntity<?> getActiveOrders(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/report/active-orders");
        
        try {
            return restClient.get()
                .uri(reportBaseUrl + "/api/report/active-orders")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo pedidos activos", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de reportes no disponible");
        }
    }

    /**
     * GET /api/report/revenue
     */
    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenue(
            @RequestParam(required = false, defaultValue = "last30d") String range,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("BFF: GET /api/report/revenue?range={}", range);
        
        try {
            return restClient.get()
                .uri(reportBaseUrl + "/api/report/revenue?range=" + range)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error obteniendo ingresos", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Servicio de reportes no disponible");
        }
    }
}