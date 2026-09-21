package cl.duoc.audit.controller;

import cl.duoc.audit.dto.AuditRequest;
import cl.duoc.audit.model.AuditEvent;
import cl.duoc.audit.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditRepository auditRepository;

    @GetMapping
    public ResponseEntity<List<AuditEvent>> getAllEvents() {
        return ResponseEntity.ok(auditRepository.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditEvent>> getEventsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(auditRepository.findByUserId(userId));
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditEvent>> getEventsByAction(@PathVariable String action) {
        return ResponseEntity.ok(auditRepository.findByAction(action));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditEvent> getEventById(@PathVariable Long id) {
        AuditEvent event = auditRepository.findById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<AuditEvent> createEvent(@RequestBody AuditRequest request) {
        AuditEvent event = new AuditEvent(
            null,
            request.getUserId(),
            request.getAction(),
            request.getEntity(),
            request.getDetails(),
            LocalDateTime.now(),
            request.getIpAddress()
        );
        AuditEvent saved = auditRepository.save(event);
        return ResponseEntity.ok(saved);
    }
}
