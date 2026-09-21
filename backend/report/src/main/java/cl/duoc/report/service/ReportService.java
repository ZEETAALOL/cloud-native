package cl.duoc.report.service;

import cl.duoc.report.model.ReportData;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportService {

    public ReportData generateSalesReport() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalSales", 125000.50);
        data.put("totalOrders", 342);
        data.put("averageOrderValue", 365.50);
        data.put("topProduct", "Laptop Dell XPS 15");
        data.put("topCategory", "Electrónica");
        
        Map<String, Integer> salesByMonth = new HashMap<>();
        salesByMonth.put("Enero", 28000);
        salesByMonth.put("Febrero", 32000);
        salesByMonth.put("Marzo", 35000);
        salesByMonth.put("Abril", 30000);
        data.put("salesByMonth", salesByMonth);

        return new ReportData(
            UUID.randomUUID().toString(),
            "SALES",
            "Reporte de Ventas",
            LocalDateTime.now(),
            data,
            "COMPLETED"
        );
    }

    public ReportData generateInventoryReport() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalProducts", 156);
        data.put("lowStockProducts", 12);
        data.put("outOfStockProducts", 3);
        data.put("totalInventoryValue", 458900.00);
        
        Map<String, Integer> stockByCategory = new HashMap<>();
        stockByCategory.put("Electrónica", 45);
        stockByCategory.put("Accesorios", 89);
        stockByCategory.put("Audio", 22);
        data.put("stockByCategory", stockByCategory);

        return new ReportData(
            UUID.randomUUID().toString(),
            "INVENTORY",
            "Reporte de Inventario",
            LocalDateTime.now(),
            data,
            "COMPLETED"
        );
    }

    public ReportData generateCustomerReport() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalCustomers", 1234);
        data.put("activeCustomers", 856);
        data.put("newCustomersThisMonth", 45);
        data.put("customerRetentionRate", 85.5);
        
        Map<String, Integer> customersByRegion = new HashMap<>();
        customersByRegion.put("Metropolitana", 678);
        customersByRegion.put("Valparaíso", 234);
        customersByRegion.put("Bío-Bío", 189);
        customersByRegion.put("Otras", 133);
        data.put("customersByRegion", customersByRegion);

        return new ReportData(
            UUID.randomUUID().toString(),
            "CUSTOMER",
            "Reporte de Clientes",
            LocalDateTime.now(),
            data,
            "COMPLETED"
        );
    }

    public ReportData generatePerformanceReport() {
        Map<String, Object> data = new HashMap<>();
        data.put("averageResponseTime", "245ms");
        data.put("successRate", 99.2);
        data.put("totalRequests", 45678);
        data.put("errorRate", 0.8);
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("cpu", "45%");
        metrics.put("memory", "62%");
        metrics.put("disk", "38%");
        data.put("systemMetrics", metrics);

        return new ReportData(
            UUID.randomUUID().toString(),
            "PERFORMANCE",
            "Reporte de Rendimiento",
            LocalDateTime.now(),
            data,
            "COMPLETED"
        );
    }
}
