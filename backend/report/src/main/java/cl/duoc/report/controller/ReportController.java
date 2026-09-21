package cl.duoc.report.controller;

import cl.duoc.report.model.ReportData;
import cl.duoc.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/sales")
    public ResponseEntity<ReportData> getSalesReport() {
        return ResponseEntity.ok(reportService.generateSalesReport());
    }

    @GetMapping("/inventory")
    public ResponseEntity<ReportData> getInventoryReport() {
        return ResponseEntity.ok(reportService.generateInventoryReport());
    }

    @GetMapping("/customers")
    public ResponseEntity<ReportData> getCustomerReport() {
        return ResponseEntity.ok(reportService.generateCustomerReport());
    }

    @GetMapping("/performance")
    public ResponseEntity<ReportData> getPerformanceReport() {
        return ResponseEntity.ok(reportService.generatePerformanceReport());
    }

    @GetMapping("/types")
    public ResponseEntity<Map<String, String>> getReportTypes() {
        Map<String, String> types = new HashMap<>();
        types.put("SALES", "Reporte de Ventas");
        types.put("INVENTORY", "Reporte de Inventario");
        types.put("CUSTOMER", "Reporte de Clientes");
        types.put("PERFORMANCE", "Reporte de Rendimiento");
        return ResponseEntity.ok(types);
    }
}
