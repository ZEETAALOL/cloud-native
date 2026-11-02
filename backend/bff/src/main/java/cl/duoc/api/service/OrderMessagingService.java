package cl.duoc.api.service;

import cl.duoc.api.dto.OrderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class OrderMessagingService {

    private static final Logger log = LoggerFactory.getLogger(OrderMessagingService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(String orderId, String customerEmail, double total) {
        // 1. Publicar a q.cmd.email (notificación al cliente)
        OrderMessage emailMsg = new OrderMessage(
            "ORDER_CREATED",
            orderId,
            customerEmail,
            "Pedido Confirmado",
            "Tu pedido #" + orderId + " ha sido recibido. Total: $" + total,
            Instant.now().toString()
        );
        
        rabbitTemplate.convertAndSend("cmd.direct", "email.send", emailMsg);
        log.info("📧 Email publicado a RabbitMQ para orderId: {}", orderId);

        // 2. Publicar a q.cmd.kitchen (ticket para cocina)
        OrderMessage kitchenMsg = new OrderMessage(
            "KITCHEN_TICKET",
            orderId,
            "cocina@pedidos360.cl",
            "Nuevo Pedido #" + orderId,
            "Preparar pedido - Total: $" + total,
            Instant.now().toString()
        );
        
        rabbitTemplate.convertAndSend("cmd.direct", "kitchen.ticket", kitchenMsg);
        log.info("👨‍🍳 Kitchen ticket publicado a RabbitMQ para orderId: {}", orderId);

        // 3. Publicar a q.cmd.invoice (factura)
        OrderMessage invoiceMsg = new OrderMessage(
            "INVOICE_GENERATION",
            orderId,
            customerEmail,
            "Factura Pedido #" + orderId,
            "Generar factura - Total: $" + total,
            Instant.now().toString()
        );
        
        rabbitTemplate.convertAndSend("cmd.direct", "invoice.gen", invoiceMsg);
        log.info("🧾 Invoice publicado a RabbitMQ para orderId: {}", orderId);
    }
}
