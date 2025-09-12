package DAT250.exercises.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import DAT250.exercises.Poll;
import DAT250.exercises.PollManager;

import java.util.Collection;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/polls")
public class PollController {
    private final PollManager pollManager;

    public PollController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @GetMapping
    public Collection<Poll> getAllPolls() {
        return pollManager.getPolls().values();
    }

    @GetMapping("/{pollId}")
    public Poll getPoll(@PathVariable String pollId) {
        Poll poll = pollManager.getPoll(pollId);
    if (poll == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
    return poll;
    }

    @PostMapping
    public String createPoll(@RequestBody Poll poll) {
        String pollId = UUID.randomUUID().toString();
        pollManager.addPoll(pollId, poll);
        return pollId;
    }

    @PutMapping("/{pollId}")
    public void updatePoll(@PathVariable String pollId, @RequestBody Poll poll) {
        pollManager.addPoll(pollId, poll);
    }

    @DeleteMapping("/{pollId}")
    public void deletePoll(@PathVariable String pollId) {
        boolean removed = pollManager.removePoll(pollId);
        if (!removed) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        pollManager.removeVotes(pollId);
    }
}
