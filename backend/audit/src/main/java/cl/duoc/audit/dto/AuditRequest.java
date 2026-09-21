package cl.duoc.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditRequest {
    private String userId;
    private String action;
    private String entity;
    private String details;
    private String ipAddress;
}
