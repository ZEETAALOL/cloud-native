package cl.duoc.api.dto;

import java.time.LocalDateTime;

public class AuditEventResponse {
    private Long id;
    private String userId;
    private String action;
    private String entity;
    private String details;
    private LocalDateTime timestamp;
    private String ipAddress;

    public AuditEventResponse() {}

    public AuditEventResponse(Long id, String userId, String action, String entity, 
                             String details, LocalDateTime timestamp, String ipAddress) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.entity = entity;
        this.details = details;
        this.timestamp = timestamp;
        this.ipAddress = ipAddress;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
