package cl.duoc.api.controller;

import cl.duoc.api.dto.AuditEventResponse;
import cl.duoc.api.dto.AuditRequest;
import cl.duoc.api.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public ResponseEntity<List<AuditEventResponse>> obtenerTodos() {
        return ResponseEntity.ok(auditService.obtenerTodos());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditEventResponse>> obtenerPorUsuario(@PathVariable String userId) {
        return ResponseEntity.ok(auditService.obtenerPorUsuario(userId));
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditEventResponse>> obtenerPorAccion(@PathVariable String action) {
        return ResponseEntity.ok(auditService.obtenerPorAccion(action));
    }

    @PostMapping
    public ResponseEntity<AuditEventResponse> registrarEvento(@RequestBody AuditRequest request) {
        return ResponseEntity.ok(auditService.registrarEvento(request));
    }
}
