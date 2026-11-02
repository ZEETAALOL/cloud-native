package cl.duoc.notify.listener;

import cl.duoc.notify.dto.OrderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class KitchenListener {

    private static final Logger log = LoggerFactory.getLogger(KitchenListener.class);

    @RabbitListener(queues = "q.cmd.kitchen")
    public void handleKitchenTicket(OrderMessage message) {
        log.info("========================================");
        log.info("👨‍🍳 TICKET COCINA RECIBIDO DE RABBITMQ");
        log.info("========================================");
        log.info("Pedido: {}", message.getOrderId());
        log.info("Asunto: {}", message.getSubject());
        log.info("Detalles: {}", message.getBody());
        log.info("Timestamp: {}", message.getTimestamp());
        log.info("========================================");

        try {
            // Simular impresión de ticket
            Thread.sleep(500);
            log.info("🖨️  Ticket impreso exitosamente para pedido: {}", message.getOrderId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error procesando ticket de cocina", e);
        }
    }
}
