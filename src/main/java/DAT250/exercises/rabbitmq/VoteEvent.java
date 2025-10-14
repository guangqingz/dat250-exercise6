package DAT250.exercises.rabbitmq;

import java.time.Instant;

public class VoteEvent {
    private String pollId;
    private String optionId;
    private String username;
    private Instant publishedAt;

    public VoteEvent() {}

    public VoteEvent(String pollId, String optionId, String username, Instant publishedAt) {
        this.pollId = pollId;
        this.optionId = optionId;
        this.username = username;
        this.publishedAt = publishedAt;
    }

    public String getPollId() { return pollId; }
    public void setPollId(String pollId) { this.pollId = pollId; }

    public String getOptionId() { return optionId; }
    public void setOptionId(String optionId) { this.optionId = optionId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
}
