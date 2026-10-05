package cl.duoc.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Servicio de Reportería - Calcula KPIs y métricas del sistema
 * 
 * En producción consumirá eventos de Kafka (orders.events) para analítica en tiempo real
 */
@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    /**
     * Calcula KPIs principales del sistema
     * Ventas por hora, lead time promedio, estados activos
     */
    public Map<String, Object> calculateKPIs(String range) {
        log.info("Calculando KPIs para rango: {}", range);

        Map<String, Object> kpis = new LinkedHashMap<>();
        
        // Información del período
        kpis.put("range", range);
        kpis.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        
        // KPIs simulados (en producción vendrán de Kafka)
        kpis.put("totalOrders", 156);
        kpis.put("totalRevenue", 1_245_890.50);
        kpis.put("averageOrderValue", 7_986.47);
        kpis.put("completedOrders", 142);
        kpis.put("canceledOrders", 8);
        kpis.put("activeOrders", 6);
        
        // Lead time promedio (en minutos)
        Map<String, Object> leadTime = new LinkedHashMap<>();
        leadTime.put("average", 45.3);
        leadTime.put("min", 18);
        leadTime.put("max", 125);
        leadTime.put("unit", "minutes");
        kpis.put("leadTime", leadTime);
        
        // Ventas por hora del día (últimas 24h)
        kpis.put("salesByHour", generateSalesByHourData());
        
        // Estados activos
        Map<String, Integer> activeStates = new LinkedHashMap<>();
        activeStates.put("CREADO", 2);
        activeStates.put("ACEPTADO", 1);
        activeStates.put("EN_PREPARACION", 2);
        activeStates.put("DESPACHADO", 1);
        kpis.put("ordersByStatus", activeStates);
        
        log.info("KPIs calculados: {} pedidos, ${} en ventas", 
            kpis.get("totalOrders"), kpis.get("totalRevenue"));
        
        return kpis;
    }

    /**
     * Obtiene los productos más vendidos
     */
    public Map<String, Object> getTopProducts(String range, Integer limit) {
        log.info("Obteniendo top {} productos para rango: {}", limit, range);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("range", range);
        result.put("limit", limit);
        result.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        
        // Top productos simulados (en producción vendrán de Kafka)
        List<Map<String, Object>> topProducts = new ArrayList<>();
        
        topProducts.add(createProductStat(1L, "Café Latte Grande", 89, 267_500.0));
        topProducts.add(createProductStat(2L, "Croissant de Almendras", 76, 152_000.0));
        topProducts.add(createProductStat(3L, "Cappuccino", 68, 204_000.0));
        topProducts.add(createProductStat(4L, "Sandwich Integral", 54, 324_000.0));
        topProducts.add(createProductStat(5L, "Jugo Natural Naranja", 45, 135_000.0));
        topProducts.add(createProductStat(6L, "Brownie Chocolate", 38, 76_000.0));
        topProducts.add(createProductStat(7L, "Té Verde", 32, 64_000.0));
        topProducts.add(createProductStat(8L, "Ensalada César", 28, 196_000.0));
        topProducts.add(createProductStat(9L, "Muffin Arándanos", 24, 48_000.0));
        topProducts.add(createProductStat(10L, "Smoothie Frutilla", 20, 80_000.0));
        
        // Limitar resultados
        result.put("products", topProducts.subList(0, Math.min(limit, topProducts.size())));
        result.put("totalProducts", topProducts.size());
        
        return result;
    }

    /**
     * Ventas agrupadas por hora del día
     */
    public Map<String, Object> getSalesByHour(String date) {
        log.info("Obteniendo ventas por hora para: {}", date);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", date);
        result.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        result.put("data", generateSalesByHourData());
        
        return result;
    }

    /**
     * Calcula lead time promedio (tiempo entre creación y entrega)
     */
    public Map<String, Object> calculateLeadTime(String range) {
        log.info("Calculando lead time para rango: {}", range);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("range", range);
        result.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        
        // Lead time simulado (en producción vendrá de Kafka)
        Map<String, Object> leadTimeStats = new LinkedHashMap<>();
        leadTimeStats.put("average", 45.3);
        leadTimeStats.put("median", 42.0);
        leadTimeStats.put("min", 18);
        leadTimeStats.put("max", 125);
        leadTimeStats.put("unit", "minutes");
        leadTimeStats.put("totalOrders", 142);
        
        // Lead time por día de la semana
        Map<String, Double> byDayOfWeek = new LinkedHashMap<>();
        byDayOfWeek.put("Lunes", 48.5);
        byDayOfWeek.put("Martes", 42.3);
        byDayOfWeek.put("Miércoles", 45.1);
        byDayOfWeek.put("Jueves", 43.8);
        byDayOfWeek.put("Viernes", 52.6);
        byDayOfWeek.put("Sábado", 38.2);
        byDayOfWeek.put("Domingo", 41.5);
        leadTimeStats.put("byDayOfWeek", byDayOfWeek);
        
        result.put("leadTime", leadTimeStats);
        
        return result;
    }

    /**
     * Obtiene pedidos activos agrupados por estado
     */
    public Map<String, Object> getActiveOrdersByStatus() {
        log.info("Obteniendo pedidos activos por estado");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        
        Map<String, Integer> ordersByStatus = new LinkedHashMap<>();
        ordersByStatus.put("CREADO", 2);
        ordersByStatus.put("ACEPTADO", 1);
        ordersByStatus.put("EN_PREPARACION", 2);
        ordersByStatus.put("DESPACHADO", 1);
        
        result.put("ordersByStatus", ordersByStatus);
        result.put("totalActive", 6);
        
        return result;
    }

    /**
     * Calcula ingresos totales por rango de tiempo
     */
    public Map<String, Object> calculateRevenue(String range) {
        log.info("Calculando ingresos para rango: {}", range);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("range", range);
        result.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        
        // Ingresos simulados (en producción vendrán de Kafka)
        Map<String, Object> revenue = new LinkedHashMap<>();
        revenue.put("total", 1_245_890.50);
        revenue.put("orders", 156);
        revenue.put("averageOrderValue", 7_986.47);
        revenue.put("currency", "CLP");
        
        // Ingresos por día
        List<Map<String, Object>> dailyRevenue = new ArrayList<>();
        dailyRevenue.add(createDailyRevenue("2024-12-30", 145_230.0, 18));
        dailyRevenue.add(createDailyRevenue("2024-12-31", 198_450.0, 24));
        dailyRevenue.add(createDailyRevenue("2025-01-01", 167_890.0, 21));
        dailyRevenue.add(createDailyRevenue("2025-01-02", 189_670.0, 23));
        dailyRevenue.add(createDailyRevenue("2025-01-03", 156_340.0, 19));
        dailyRevenue.add(createDailyRevenue("2025-01-04", 201_560.0, 25));
        dailyRevenue.add(createDailyRevenue("2025-01-05", 186_750.0, 26));
        
        revenue.put("dailyBreakdown", dailyRevenue);
        result.put("revenue", revenue);
        
        return result;
    }

    // Métodos auxiliares privados

    private List<Map<String, Object>> generateSalesByHourData() {
        List<Map<String, Object>> salesByHour = new ArrayList<>();
        
        int[] sales = {2, 1, 0, 0, 0, 0, 3, 8, 15, 12, 10, 14, 18, 16, 14, 12, 15, 17, 14, 10, 8, 6, 4, 3};
        
        for (int hour = 0; hour < 24; hour++) {
            Map<String, Object> hourData = new LinkedHashMap<>();
            hourData.put("hour", String.format("%02d:00", hour));
            hourData.put("orders", sales[hour]);
            hourData.put("revenue", sales[hour] * 8_500.0); // Promedio por pedido
            salesByHour.add(hourData);
        }
        
        return salesByHour;
    }

    private Map<String, Object> createProductStat(Long id, String name, int quantity, double revenue) {
        Map<String, Object> product = new LinkedHashMap<>();
        product.put("productId", id);
        product.put("productName", name);
        product.put("quantitySold", quantity);
        product.put("revenue", revenue);
        product.put("averagePrice", revenue / quantity);
        return product;
    }

    private Map<String, Object> createDailyRevenue(String date, double revenue, int orders) {
        Map<String, Object> daily = new LinkedHashMap<>();
        daily.put("date", date);
        daily.put("revenue", revenue);
        daily.put("orders", orders);
        daily.put("averageOrderValue", revenue / orders);
        return daily;
    }
}
