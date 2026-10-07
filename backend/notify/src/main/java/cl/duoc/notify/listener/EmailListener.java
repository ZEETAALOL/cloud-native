package cl.duoc.notify.listener;

import cl.duoc.notify.dto.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailListener {

    private static final Logger logger = LoggerFactory.getLogger(EmailListener.class);

    @RabbitListener(queues = "q.cmd.email")
    public void receiveEmailMessage(EmailMessage message) {
        logger.info("========================================");
        logger.info("📧 MENSAJE RECIBIDO DE RABBITMQ");
        logger.info("========================================");
        logger.info("Destinatario: {}", message.getRecipient());
        logger.info("Asunto: {}", message.getSubject());
        logger.info("Cuerpo: {}", message.getBody());
        logger.info("Order ID: {}", message.getOrderId());
        logger.info("Timestamp: {}", message.getTimestamp());
        logger.info("========================================");
        
        // Simulación de envío de email
        try {
            Thread.sleep(1000); // Simula proceso de envío
            logger.info("✅ Email enviado exitosamente a: {}", message.getRecipient());
        } catch (InterruptedException e) {
            logger.error("❌ Error al enviar email", e);
            Thread.currentThread().interrupt();
        }
    }
}
