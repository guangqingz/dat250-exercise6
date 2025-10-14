package DAT250.exercises.rabbitmq;

import java.time.Instant;
import java.util.UUID;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

public class StandalonePublisher {
    public static void main(String[] args) {
        // args: <pollId> <optionId> [username]
        String pollId   = args.length > 0 ? args[0] : UUID.randomUUID().toString();
        String optionId = args.length > 1 ? args[1] : "0";
        String username = args.length > 2 ? args[2] : null;

        var cf = new CachingConnectionFactory("localhost", 5672);
        cf.setUsername("guest"); cf.setPassword("guest");
        var template = new RabbitTemplate(cf);
        template.setMessageConverter(new Jackson2JsonMessageConverter());

        VoteEvent event = new VoteEvent(pollId, optionId, username, Instant.now());
        String rk = "poll." + pollId + ".vote";
        template.convertAndSend(RabbitMQConfig.POLL_TOPIC_EXCHANGE, rk, event);
        System.out.println("Published vote to " + rk + " -> " + optionId + " (user=" + username + ")");
        cf.destroy();
    }
}
