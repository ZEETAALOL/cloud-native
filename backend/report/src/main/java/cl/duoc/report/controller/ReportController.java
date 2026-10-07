package cl.duoc.report.controller;

import cl.duoc.report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller de Reportería - Endpoints según especificación del caso PDF
 * 
 * Endpoints esenciales:
 * - GET /api/report/kpis?range=last24h
 * - GET /api/report/top-products?range=last7d
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    /**
     * GET /api/report/kpis?range=last24h
     * Panel de KPIs: ventas por hora, lead time, estados activos
     */
    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> getKPIs(
            @RequestParam(required = false, defaultValue = "last24h") String range) {
        
        log.info("GET /api/report/kpis?range={}", range);
        Map<String, Object> kpis = reportService.calculateKPIs(range);
        return ResponseEntity.ok(kpis);
    }

    /**
     * GET /api/report/top-products?range=last7d
     * Productos más vendidos en el rango especificado
     */
    @GetMapping("/top-products")
    public ResponseEntity<Map<String, Object>> getTopProducts(
            @RequestParam(required = false, defaultValue = "last7d") String range,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        log.info("GET /api/report/top-products?range={}&limit={}", range, limit);
        Map<String, Object> topProducts = reportService.getTopProducts(range, limit);
        return ResponseEntity.ok(topProducts);
    }

    /**
     * GET /api/report/sales-by-hour
     * Ventas agrupadas por hora del día
     */
    @GetMapping("/sales-by-hour")
    public ResponseEntity<Map<String, Object>> getSalesByHour(
            @RequestParam(required = false, defaultValue = "today") String date) {
        
        log.info("GET /api/report/sales-by-hour?date={}", date);
        Map<String, Object> salesByHour = reportService.getSalesByHour(date);
        return ResponseEntity.ok(salesByHour);
    }

    /**
     * GET /api/report/lead-time
     * Tiempo promedio entre creación y entrega de pedidos
     */
    @GetMapping("/lead-time")
    public ResponseEntity<Map<String, Object>> getLeadTime(
            @RequestParam(required = false, defaultValue = "last7d") String range) {
        
        log.info("GET /api/report/lead-time?range={}", range);
        Map<String, Object> leadTime = reportService.calculateLeadTime(range);
        return ResponseEntity.ok(leadTime);
    }

    /**
     * GET /api/report/active-orders
     * Pedidos activos por estado
     */
    @GetMapping("/active-orders")
    public ResponseEntity<Map<String, Object>> getActiveOrders() {
        log.info("GET /api/report/active-orders");
        Map<String, Object> activeOrders = reportService.getActiveOrdersByStatus();
        return ResponseEntity.ok(activeOrders);
    }

    /**
     * GET /api/report/revenue
     * Ingresos totales por rango de tiempo
     */
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenue(
            @RequestParam(required = false, defaultValue = "last30d") String range) {
        
        log.info("GET /api/report/revenue?range={}", range);
        Map<String, Object> revenue = reportService.calculateRevenue(range);
        return ResponseEntity.ok(revenue);
    }
}
