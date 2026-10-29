package cl.duoc.orders.service;

import cl.duoc.orders.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void enviarNotificacion(NotificationMessage mensaje) {
        // Convertir al formato que espera Notify
        Map<String, String> emailMessage = new HashMap<>();
        emailMessage.put("type", "email");
        emailMessage.put("recipient", mensaje.getEmail());
        emailMessage.put("subject", mensaje.getAsunto());
        emailMessage.put("body", mensaje.getMensaje());
        emailMessage.put("orderId", String.valueOf(mensaje.getPedidoId()));
        emailMessage.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        rabbitTemplate.convertAndSend("cmd.direct", "email.send", emailMessage);
        log.info("📧 Notificación publicada en RabbitMQ para pedido: {}", mensaje.getPedidoId());
    }
}
