package DAT250.exercises.rabbitmq;

import com.rabbitmq.client.*;

public class StandaloneSubscriber {
    public static void main(String[] args) throws Exception {
        // Raw client showing you messages on the app queue
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        try (Connection conn = factory.newConnection();
            Channel ch = conn.createChannel()) {
            String queue = RabbitMQConfig.POLLAPP_VOTES_QUEUE;
            System.out.println("Consuming from queue: " + queue);
            ch.basicConsume(queue, true, (tag, delivery) -> {
                String body = new String(delivery.getBody());
                System.out.println(" [x] Got: " + body + " rk=" + delivery.getEnvelope().getRoutingKey());
            }, tag -> {});
            Thread.sleep(Long.MAX_VALUE);
        }
    }
}
