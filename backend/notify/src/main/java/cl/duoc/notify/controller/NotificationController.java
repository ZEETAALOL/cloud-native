package cl.duoc.notify.controller;

import cl.duoc.notify.dto.NotificationRequest;
import cl.duoc.notify.model.Notification;
import cl.duoc.notify.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller de Notificaciones - Según especificación del caso PDF
 * 
 * No tiene endpoints públicos (consumidor RabbitMQ)
 * Estos endpoints son para monitoreo y testing
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    /**
     * GET /api/notifications - Ver todas las notificaciones enviadas
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        log.info("GET /api/notifications");
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    /**
     * GET /api/notifications/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable String id) {
        log.info("GET /api/notifications/{}", id);
        
        Notification notification = notificationService.getNotificationById(id);
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(notification);
    }

    /**
     * GET /api/notifications/recipient/{recipient}
     */
    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<Notification>> getNotificationsByRecipient(@PathVariable String recipient) {
        log.info("GET /api/notifications/recipient/{}", recipient);
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(recipient));
    }

    /**
     * GET /api/notifications/channel/{channel}
     */
    @GetMapping("/channel/{channel}")
    public ResponseEntity<List<Notification>> getNotificationsByChannel(@PathVariable String channel) {
        log.info("GET /api/notifications/channel/{}", channel);
        return ResponseEntity.ok(notificationService.getNotificationsByChannel(channel));
    }

    /**
     * POST /api/notifications/send - Enviar notificación (para testing)
     */
    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@RequestBody NotificationRequest request) {
        log.info("POST /api/notifications/send - Canal: {}, Destinatario: {}", 
            request.getChannel(), request.getRecipient());
        
        Notification notification = notificationService.sendNotification(
            request.getRecipient(),
            request.getChannel(),
            request.getSubject(),
            request.getMessage()
        );
        return ResponseEntity.ok(notification);
    }

    /**
     * POST /api/notifications/email - Enviar email específicamente
     */
    @PostMapping("/email")
    public ResponseEntity<Notification> sendEmail(@RequestBody EmailNotificationRequest request) {
        log.info("POST /api/notifications/email - To: {}", request.getTo());
        
        Notification notification = notificationService.sendNotification(
            request.getTo(),
            "EMAIL",
            request.getSubject(),
            request.getBody()
        );
        return ResponseEntity.ok(notification);
    }

    /**
     * POST /api/notifications/webpush - Enviar notificación web push
     */
    @PostMapping("/webpush")
    public ResponseEntity<Notification> sendWebPush(@RequestBody WebPushNotificationRequest request) {
        log.info("POST /api/notifications/webpush - To: {}", request.getUserId());
        
        Notification notification = notificationService.sendNotification(
            request.getUserId(),
            "WEBPUSH",
            request.getTitle(),
            request.getMessage()
        );
        return ResponseEntity.ok(notification);
    }

    /**
     * GET /api/notifications/stats - Estadísticas de notificaciones
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("GET /api/notifications/stats");
        Map<String, Object> stats = notificationService.getStats();
        return ResponseEntity.ok(stats);
    }

    // DTOs adicionales
    public static class EmailNotificationRequest {
        private String to;
        private String subject;
        private String body;

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    public static class WebPushNotificationRequest {
        private String userId;
        private String title;
        private String message;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
