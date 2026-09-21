package cl.duoc.notify.service;

import cl.duoc.notify.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final ConcurrentHashMap<String, Notification> notifications = new ConcurrentHashMap<>();

    public NotificationService() {
        // Datos de ejemplo
        Notification n1 = new Notification(
            UUID.randomUUID().toString(),
            "user@example.com",
            "EMAIL",
            "Pedido Confirmado",
            "Tu pedido #1234 ha sido confirmado",
            "SENT",
            LocalDateTime.now().minusHours(2)
        );
        notifications.put(n1.getId(), n1);

        Notification n2 = new Notification(
            UUID.randomUUID().toString(),
            "+56912345678",
            "SMS",
            "Código de verificación",
            "Tu código es: 123456",
            "SENT",
            LocalDateTime.now().minusMinutes(30)
        );
        notifications.put(n2.getId(), n2);

        Notification n3 = new Notification(
            UUID.randomUUID().toString(),
            "admin@example.com",
            "EMAIL",
            "Alerta de stock bajo",
            "El producto XYZ tiene stock bajo",
            "SENT",
            LocalDateTime.now().minusMinutes(5)
        );
        notifications.put(n3.getId(), n3);
    }

    public Notification sendNotification(String recipient, String channel, String subject, String message) {
        String id = UUID.randomUUID().toString();
        
        // Simular envío con log
        logger.info("📧 Enviando notificación [{}] a {} via {}: {}", 
            id, recipient, channel, subject);
        
        Notification notification = new Notification(
            id,
            recipient,
            channel,
            subject,
            message,
            "SENT",
            LocalDateTime.now()
        );
        
        notifications.put(id, notification);
        
        logger.info("✅ Notificación {} enviada exitosamente", id);
        
        return notification;
    }

    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications.values());
    }

    public Notification getNotificationById(String id) {
        return notifications.get(id);
    }

    public List<Notification> getNotificationsByRecipient(String recipient) {
        return notifications.values().stream()
            .filter(n -> n.getRecipient().equals(recipient))
            .toList();
    }

    public List<Notification> getNotificationsByChannel(String channel) {
        return notifications.values().stream()
            .filter(n -> n.getChannel().equalsIgnoreCase(channel))
            .toList();
    }
}
