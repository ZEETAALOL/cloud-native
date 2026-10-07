package cl.duoc.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportData {
    private String reportId;
    private String reportType;
    private String title;
    private LocalDateTime generatedAt;
    private Map<String, Object> data;
    private String status;
}
