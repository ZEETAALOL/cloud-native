package cl.duoc.notify.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /**
     * Cola "q.cmd.email" para comandos de envío de email.
     * Idempotente: si no existe la crea; si existe, la reutiliza.
     * durable=false → se borra si RabbitMQ se reinicia (en producción: true).
     */
    @Bean
    public Queue emailQueue() {
        return new Queue("q.cmd.email", false);
    }
}
