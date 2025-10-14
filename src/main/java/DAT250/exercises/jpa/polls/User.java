package DAT250.exercises.jpa.polls;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Poll> created = new LinkedHashSet<>();

    public User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Long getId() { 
        return this.id; 
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Poll> getCreated() {
        return this.created;
    }

    public Poll createPoll(String question) {
        Poll poll = new Poll();
        poll.setQuestion(question);
        poll.setCreatedBy(this);
        this.created.add(poll);
        return poll;
    }

    public Vote voteFor(VoteOption option) {
        Vote vote = new Vote();
        vote.setVotesOn(option);
        vote.setPublishedAt(Instant.now());
        return vote;
    }
}