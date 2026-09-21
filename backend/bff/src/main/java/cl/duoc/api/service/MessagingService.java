package cl.duoc.api.service;

import cl.duoc.api.dto.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessagingService {

    private static final Logger log = LoggerFactory.getLogger(MessagingService.class);
    private final RabbitTemplate rabbitTemplate;

    public MessagingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmailNotification(String recipient, String subject, String body, String orderId) {
        EmailMessage message = new EmailMessage();
        message.setType("ORDER_CREATED");
        message.setRecipient(recipient);
        message.setSubject(subject);
        message.setBody(body);
        message.setOrderId(orderId);
        message.setTimestamp(LocalDateTime.now().toString());

        rabbitTemplate.convertAndSend("cmd.direct", "notify.email", message);
        log.info("📧 Mensaje de email enviado a RabbitMQ para orderId: {}", orderId);
    }
}
