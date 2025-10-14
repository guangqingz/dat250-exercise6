package DAT250.exercises.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import DAT250.exercises.PollManager;
import DAT250.exercises.jpa.polls.Vote;
import DAT250.exercises.rabbitmq.PollEventService;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/votes")
public class VoteController {

    private final PollManager pollManager;
    private final PollEventService pollEventService;

    public VoteController(PollManager pollManager, PollEventService pollEventService) {
        this.pollManager = pollManager;
        this.pollEventService = pollEventService;
    }

    @PostMapping("/{pollId}/{username}")
    public void addVote(@PathVariable String pollId, @PathVariable String username, 
                        @RequestBody Vote vote) {

        pollManager.addVote(pollId, vote, username);
        pollEventService.publishVoteEvent(
                pollId, 
                String.valueOf(vote.getVotesOn().getPresentationOrder()), 
                username);
    }

    @PutMapping("/{pollId}/{username}")
    public void updateVote(@PathVariable String pollId, @PathVariable String username, 
                            @RequestBody Vote vote) {

        pollManager.updateVote(pollId, vote, username);
    }

    @GetMapping
    public Collection<Vote> getAllVotes() {
        return pollManager.getVotes().values();
    }

    @GetMapping("/{pollId}/{username}")
    public Vote getVote(@PathVariable String pollId, @PathVariable String username) {
        Vote vote = pollManager.getVote(pollId, username);
        if (vote == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vote not found");
        }
        return vote;
    }

    @DeleteMapping("/{pollId}/{username}")
    public void deleteVote(@PathVariable String pollId, @PathVariable String username) {
        boolean removed = pollManager.removeVote(pollId, username);
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vote not found");
        }
    }
}
