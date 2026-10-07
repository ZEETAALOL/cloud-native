package cl.duoc.api.controller;

import cl.duoc.api.service.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/messaging")
public class MessagingController {

    private static final Logger logger = LoggerFactory.getLogger(MessagingController.class);

    private final MessagingService messagingService;

    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody Map<String, String> request) {
        logger.info("POST /api/messaging/send-email - Enviando notificación por email");

        String recipient = request.getOrDefault("recipient", "cliente@example.com");
        String subject = request.getOrDefault("subject", "Pedido Confirmado");
        String body = request.getOrDefault("body", "Tu pedido ha sido confirmado y está en preparación");
        String orderId = request.getOrDefault("orderId", "TEST-" + System.currentTimeMillis());

        messagingService.sendEmailNotification(recipient, subject, body, orderId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Mensaje enviado a RabbitMQ",
                "recipient", recipient,
                "orderId", orderId
        ));
    }
}
