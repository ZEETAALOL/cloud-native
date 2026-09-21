package cl.duoc.api.service;

import cl.duoc.api.dto.AuditEventResponse;
import cl.duoc.api.dto.AuditRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class AuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    
    private final RestClient restClient;

    public AuditService(@Value("${microservices.audit.base-url}") String auditBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(auditBaseUrl)
                .build();
        logger.info("AuditService inicializado con URL: {}", auditBaseUrl);
    }

    public List<AuditEventResponse> obtenerTodos() {
        logger.debug("Obteniendo todos los eventos de auditoría desde microservicio Audit");
        return restClient.get()
                .uri("/api/audit")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AuditEventResponse>>() {});
    }

    public List<AuditEventResponse> obtenerPorUsuario(String userId) {
        logger.debug("Obteniendo eventos de auditoría para usuario: {} desde microservicio Audit", userId);
        return restClient.get()
                .uri("/api/audit/user/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AuditEventResponse>>() {});
    }

    public List<AuditEventResponse> obtenerPorAccion(String action) {
        logger.debug("Obteniendo eventos de auditoría para acción: {} desde microservicio Audit", action);
        return restClient.get()
                .uri("/api/audit/action/{action}", action)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AuditEventResponse>>() {});
    }

    public AuditEventResponse registrarEvento(AuditRequest request) {
        logger.debug("Registrando nuevo evento de auditoría en microservicio Audit");
        return restClient.post()
                .uri("/api/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AuditEventResponse.class);
    }
}
