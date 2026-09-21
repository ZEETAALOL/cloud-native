package cl.duoc.api.controller;

import cl.duoc.api.dto.ReportDataResponse;
import cl.duoc.api.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    
    private final ReportService reportService;
    
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    @GetMapping("/sales")
    public ResponseEntity<ReportDataResponse> getSalesReport() {
        logger.info("GET /api/reports/sales - Solicitando reporte de ventas");
        ReportDataResponse report = reportService.getSalesReport();
        logger.info("Reporte de ventas obtenido exitosamente: {}", report.getReportId());
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/inventory")
    public ResponseEntity<ReportDataResponse> getInventoryReport() {
        logger.info("GET /api/reports/inventory - Solicitando reporte de inventario");
        ReportDataResponse report = reportService.getInventoryReport();
        logger.info("Reporte de inventario obtenido exitosamente: {}", report.getReportId());
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/customers")
    public ResponseEntity<ReportDataResponse> getCustomerReport() {
        logger.info("GET /api/reports/customers - Solicitando reporte de clientes");
        ReportDataResponse report = reportService.getCustomerReport();
        logger.info("Reporte de clientes obtenido exitosamente: {}", report.getReportId());
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/performance")
    public ResponseEntity<ReportDataResponse> getPerformanceReport() {
        logger.info("GET /api/reports/performance - Solicitando reporte de rendimiento");
        ReportDataResponse report = reportService.getPerformanceReport();
        logger.info("Reporte de rendimiento obtenido exitosamente: {}", report.getReportId());
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/types")
    public ResponseEntity<List<String>> getAvailableReportTypes() {
        logger.info("GET /api/reports/types - Solicitando tipos de reportes disponibles");
        List<String> types = reportService.getAvailableReportTypes();
        logger.info("Tipos de reportes obtenidos: {}", types);
        return ResponseEntity.ok(types);
    }
}
