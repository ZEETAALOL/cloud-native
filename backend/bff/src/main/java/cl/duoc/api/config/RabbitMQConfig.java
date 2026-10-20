package cl.duoc.api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nombres de exchanges, colas y routing keys
    public static final String EXCHANGE_CMD = "cmd.direct";
    public static final String QUEUE_EMAIL = "q.cmd.email";
    public static final String ROUTING_KEY_EMAIL = "notify.email";

    /**
     * Exchange directo para comandos.
     * durable=true para producción (persiste reinicios de RabbitMQ).
     */
    @Bean
    public DirectExchange commandExchange() {
        return new DirectExchange(EXCHANGE_CMD, true, false);
    }

    /**
     * Cola para notificaciones de email.
     * durable=false para desarrollo (se borra si RabbitMQ se reinicia).
     * En producción cambiar a: new Queue(QUEUE_EMAIL, true);
     */
    @Bean
    public Queue emailQueue() {
        return new Queue(QUEUE_EMAIL, false);
    }

    /**
     * Binding: conecta el exchange con la cola usando la routing key.
     * Mensajes enviados a "cmd.direct" con routing key "notify.email"
     * llegarán a la cola "q.cmd.email".
     */
    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange commandExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(commandExchange)
                .with(ROUTING_KEY_EMAIL);
    }
}
