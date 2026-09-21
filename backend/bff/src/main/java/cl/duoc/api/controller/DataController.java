package cl.duoc.api.controller;

import cl.duoc.api.dto.ClienteResponse;
import cl.duoc.api.dto.EmailMessage;
import cl.duoc.api.service.DataService;
import cl.duoc.api.service.MessagingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {

    private final DataService dataService;
    private final MessagingService messagingService;

    public DataController(DataService dataService, MessagingService messagingService) {
        this.dataService = dataService;
        this.messagingService = messagingService;
    }

    @GetMapping("/api/data")
    public ClienteResponse data() {
        return dataService.obtenerData();
    }

    @GetMapping("/public/test-rabbitmq")
    public String testRabbitMQ() {
        messagingService.sendEmailNotification(
            "test@example.com",
            "Test desde BFF",
            "Probando integración RabbitMQ - Fase 4 completada!",
            "TEST-001"
        );
        return "✅ Mensaje enviado a RabbitMQ exitosamente!";
    }
}
