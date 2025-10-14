package DAT250.exercises.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // One topic exchange for all poll events
    public static final String POLL_TOPIC_EXCHANGE = "polls.topic";

    // One queue for this PollApp to consume all votes
    public static final String POLLAPP_VOTES_QUEUE = "pollapp.votes";

    // Routing key pattern for all votes
    public static final String ALL_VOTES_ROUTING_KEY = "poll.*.vote";

    @Bean
    TopicExchange pollExchange() {
        return new TopicExchange(POLL_TOPIC_EXCHANGE, true, false);
    }

    @Bean
    Queue pollAppVotesQueue() {
        return QueueBuilder.durable(POLLAPP_VOTES_QUEUE).build();
    }

    @Bean
    Binding allVotesBinding(Queue pollAppVotesQueue, TopicExchange pollExchange) {
        // Subscribe to *all* poll votes by default
        return BindingBuilder.bind(pollAppVotesQueue)
                .to(pollExchange)
                .with(ALL_VOTES_ROUTING_KEY);
    }

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory cf, Jackson2JsonMessageConverter converter) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(converter);
        return t;
    }

    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory cf) {
        return new RabbitAdmin(cf);
    }
}
