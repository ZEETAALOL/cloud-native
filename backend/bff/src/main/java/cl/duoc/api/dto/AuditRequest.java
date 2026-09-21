package cl.duoc.api.dto;

public class AuditRequest {
    private String userId;
    private String action;
    private String entity;
    private String details;
    private String ipAddress;

    public AuditRequest() {}

    public AuditRequest(String userId, String action, String entity, 
                       String details, String ipAddress) {
        this.userId = userId;
        this.action = action;
        this.entity = entity;
        this.details = details;
        this.ipAddress = ipAddress;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
