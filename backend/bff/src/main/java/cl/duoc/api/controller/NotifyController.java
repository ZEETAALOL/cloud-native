package cl.duoc.api.controller;

import cl.duoc.api.dto.NotificationRequest;
import cl.duoc.api.dto.NotificationResponse;
import cl.duoc.api.service.NotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotifyController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotifyController.class);
    
    private final NotifyService notifyService;
    
    public NotifyController(NotifyService notifyService) {
        this.notifyService = notifyService;
    }
    
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        logger.info("GET /api/notifications - Solicitando todas las notificaciones");
        List<NotificationResponse> notifications = notifyService.getAllNotifications();
        logger.info("Obtenidas {} notificaciones", notifications.size());
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable String id) {
        logger.info("GET /api/notifications/{} - Solicitando notificación específica", id);
        NotificationResponse notification = notifyService.getNotificationById(id);
        logger.info("Notificación {} obtenida exitosamente", id);
        return ResponseEntity.ok(notification);
    }
    
    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByRecipient(
            @PathVariable String recipient) {
        logger.info("GET /api/notifications/recipient/{} - Solicitando notificaciones por destinatario", recipient);
        List<NotificationResponse> notifications = notifyService.getNotificationsByRecipient(recipient);
        logger.info("Obtenidas {} notificaciones para destinatario: {}", notifications.size(), recipient);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/channel/{channel}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByChannel(
            @PathVariable String channel) {
        logger.info("GET /api/notifications/channel/{} - Solicitando notificaciones por canal", channel);
        List<NotificationResponse> notifications = notifyService.getNotificationsByChannel(channel);
        logger.info("Obtenidas {} notificaciones para canal: {}", notifications.size(), channel);
        return ResponseEntity.ok(notifications);
    }
    
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendNotification(@RequestBody NotificationRequest request) {
        logger.info("POST /api/notifications/send - Enviando notificación a {} por canal {}", 
                   request.getRecipient(), request.getChannel());
        Map<String, String> response = notifyService.sendNotification(request);
        logger.info("Notificación enviada exitosamente");
        return ResponseEntity.ok(response);
    }
}
