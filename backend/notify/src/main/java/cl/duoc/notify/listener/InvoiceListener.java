package cl.duoc.notify.listener;

import cl.duoc.notify.dto.OrderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InvoiceListener {

    private static final Logger log = LoggerFactory.getLogger(InvoiceListener.class);

    @RabbitListener(queues = "q.cmd.invoice")
    public void handleInvoiceGeneration(OrderMessage message) {
        log.info("========================================");
        log.info("🧾 FACTURA RECIBIDA DE RABBITMQ");
        log.info("========================================");
        log.info("Pedido: {}", message.getOrderId());
        log.info("Cliente: {}", message.getRecipient());
        log.info("Asunto: {}", message.getSubject());
        log.info("Detalles: {}", message.getBody());
        log.info("Timestamp: {}", message.getTimestamp());
        log.info("========================================");

        try {
            // Simular generación de factura
            Thread.sleep(1000);
            log.info("📄 Factura generada exitosamente para pedido: {}", message.getOrderId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error generando factura", e);
        }
    }
}
