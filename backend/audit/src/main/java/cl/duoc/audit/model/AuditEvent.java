package cl.duoc.audit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {
    private Long id;
    private String userId;
    private String action;
    private String entity;
    private String details;
    private LocalDateTime timestamp;
    private String ipAddress;
}
