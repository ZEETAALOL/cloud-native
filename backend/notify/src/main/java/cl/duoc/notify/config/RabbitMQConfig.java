package cl.duoc.notify.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // No declaramos la cola aquí porque ya existe en RabbitMQ con su configuración DLX
    // Solo la escuchamos con @RabbitListener
}
