package cl.duoc.api.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ReportDataResponse {
    private String reportId;
    private String reportType;
    private String title;
    private LocalDateTime generatedAt;
    private Map<String, Object> data;
    private String status;

    public ReportDataResponse() {
    }

    public ReportDataResponse(String reportId, String reportType, String title, 
                             LocalDateTime generatedAt, Map<String, Object> data, String status) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.title = title;
        this.generatedAt = generatedAt;
        this.data = data;
        this.status = status;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
