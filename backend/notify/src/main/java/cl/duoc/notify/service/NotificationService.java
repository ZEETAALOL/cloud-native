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
        // Notificaciones iniciales del sistema
        Notification n1 = new Notification(
            UUID.randomUUID().toString(),
            "pedidos360@duocuc.cl",
            "EMAIL",
            "Sistema Iniciado",
            "Pedidos360 ha iniciado correctamente en AWS EC2",
            "SENT",
            LocalDateTime.now().minusHours(2)
        );
        notifications.put(n1.getId(), n1);

        Notification n2 = new Notification(
            UUID.randomUUID().toString(),
            "admin@pedidos360.cl",
            "EMAIL",
            "Servicios Activos",
            "Todos los microservicios están operacionales",
            "SENT",
            LocalDateTime.now().minusMinutes(30)
        );
        notifications.put(n2.getId(), n2);

        Notification n3 = new Notification(
            UUID.randomUUID().toString(),
            "soporte@pedidos360.cl",
            "EMAIL",
            "Base de Datos Sincronizada",
            "PostgreSQL y MongoDB sincronizados correctamente",
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

    public java.util.Map<String, Object> getStats() {
        logger.info("Calculando estadísticas de notificaciones");
        
        java.util.Map<String, Object> stats = new java.util.LinkedHashMap<>();
        stats.put("total", notifications.size());
        
        // Contar por canal
        java.util.Map<String, Long> byChannel = notifications.values().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Notification::getChannel, 
                java.util.stream.Collectors.counting()
            ));
        stats.put("byChannel", byChannel);
        
        // Contar por estado
        java.util.Map<String, Long> byStatus = notifications.values().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Notification::getStatus, 
                java.util.stream.Collectors.counting()
            ));
        stats.put("byStatus", byStatus);
        
        // Última notificación enviada
        notifications.values().stream()
            .max((n1, n2) -> n1.getSentAt().compareTo(n2.getSentAt()))
            .ifPresent(last -> stats.put("lastNotification", last));
        
        return stats;
    }
}