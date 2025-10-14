package DAT250.exercises.rabbitmq;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import DAT250.exercises.PollManager;
import DAT250.exercises.jpa.polls.Vote;
import DAT250.exercises.jpa.polls.VoteOption;
import static DAT250.exercises.rabbitmq.RabbitMQConfig.POLLAPP_VOTES_QUEUE;
import static DAT250.exercises.rabbitmq.RabbitMQConfig.POLL_TOPIC_EXCHANGE;
import DAT250.exercises.redis.PollService;

@Service
public class PollEventService {
    private static final Logger log = LoggerFactory.getLogger(PollEventService.class);

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange exchange;
    private final RabbitAdmin rabbitAdmin;
    private final Queue pollAppQueue;

    private final PollManager pollManager;
    private final PollService pollService;

    public PollEventService(
            RabbitTemplate rabbitTemplate,
            TopicExchange pollExchange,
            RabbitAdmin rabbitAdmin,
            Queue pollAppVotesQueue,
            PollManager pollManager,
            PollService pollService
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = pollExchange;
        this.rabbitAdmin = rabbitAdmin;
        this.pollAppQueue = pollAppVotesQueue;
        this.pollManager = pollManager;
        this.pollService = pollService;
    }

    // register a topic for a poll = bind our queue to an exact routing key for that poll
    public void registerPollTopic(String pollId) {
        String routingKey = "poll." + pollId + ".vote";
        Binding binding = BindingBuilder.bind(pollAppQueue).to(exchange).with(routingKey);
        rabbitAdmin.declareBinding(binding);
        log.info("Registered poll topic: routingKey={}", routingKey);
    }

    public void publishVoteEvent(String pollId, String optionId, String username) {
        String routingKey = "poll." + pollId + ".vote";
        VoteEvent event = new VoteEvent(pollId, optionId, username, Instant.now());
        rabbitTemplate.convertAndSend(POLL_TOPIC_EXCHANGE, routingKey, event);
        log.info("Published vote event: rk={}, {}", routingKey, event);
    }

    // Consume all vote events (we're bound with wildcard + per-poll bindings)
    @RabbitListener(queues = POLLAPP_VOTES_QUEUE)
    public void onVoteEvent(VoteEvent event) {
        log.info("Received vote event: pollId={}, optionId={}, username={}",
                event.getPollId(), event.getOptionId(), event.getUsername());

        // 1) Update in-memory PollManager for anonymous votes:
        // We store votes under key (pollId-username). For anonymous, use a synthetic key.
        String usernameKey = event.getUsername() != null ? event.getUsername() : "anonymous-" + event.getPublishedAt().toEpochMilli();

        Vote v = new Vote();
        v.setPublishedAt(event.getPublishedAt());

        // Find the matching VoteOption by presentationOrder on the Poll in memory
        var poll = pollManager.getPoll(event.getPollId());
        if (poll != null) {
            int optOrder = Integer.parseInt(event.getOptionId());
            VoteOption target = poll.getOptions().stream()
                    .filter(o -> o.getPresentationOrder() == optOrder)
                    .findFirst().orElse(null);
            if (target != null) {
                v.setVotesOn(target);
                pollManager.addVote(event.getPollId(), v, usernameKey);
            } else {
                log.warn("Option not found for pollId={}, optionId={}", event.getPollId(), event.getOptionId());
            }
        } else {
            log.warn("Poll not found in memory for pollId={}", event.getPollId());
        }

        // 2) Update Redis cache counter fast-path
        try {
            pollService.registerVote(event.getPollId(), event.getOptionId());

        } catch (Exception e) {
            log.debug("Cache increment skipped (non-numeric pollId?). Consider adapting PollService to String keys.");
        }
    }

    private long parseAsLongOrZero(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return 0L; }
    }
}
