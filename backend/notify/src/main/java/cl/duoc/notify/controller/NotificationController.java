package cl.duoc.notify.controller;

import cl.duoc.notify.dto.NotificationRequest;
import cl.duoc.notify.model.Notification;
import cl.duoc.notify.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable String id) {
        Notification notification = notificationService.getNotificationById(id);
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<Notification>> getNotificationsByRecipient(@PathVariable String recipient) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(recipient));
    }

    @GetMapping("/channel/{channel}")
    public ResponseEntity<List<Notification>> getNotificationsByChannel(@PathVariable String channel) {
        return ResponseEntity.ok(notificationService.getNotificationsByChannel(channel));
    }

    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@RequestBody NotificationRequest request) {
        Notification notification = notificationService.sendNotification(
            request.getRecipient(),
            request.getChannel(),
            request.getSubject(),
            request.getMessage()
        );
        return ResponseEntity.ok(notification);
    }
}
