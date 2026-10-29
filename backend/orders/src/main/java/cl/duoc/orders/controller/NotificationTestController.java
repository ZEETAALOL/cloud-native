package cl.duoc.orders.controller;

import cl.duoc.orders.dto.NotificationMessage;
import cl.duoc.orders.service.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class NotificationTestController {

    private final NotificationPublisher publisher;

    @PostMapping("/test-notification")
    public ResponseEntity<String> testNotification(@RequestBody NotificationMessage mensaje) {
        publisher.enviarNotificacion(mensaje);
        return ResponseEntity.accepted()
                .body("Solicitud de notificación publicada en RabbitMQ");
    }
}
