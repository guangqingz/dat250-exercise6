package DAT250.exercises.controllers;

import java.util.Collection;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import DAT250.exercises.PollManager;
import DAT250.exercises.jpa.polls.Poll;
import DAT250.exercises.jpa.polls.Vote;
import DAT250.exercises.jpa.polls.VoteOption;
import DAT250.exercises.rabbitmq.PollEventService;

@CrossOrigin
@RestController
@RequestMapping("/polls")
public class PollController {
    private final PollManager pollManager;
    private final PollEventService pollEventService;

    public PollController(PollManager pollManager, PollEventService pollEventService) {
        this.pollManager = pollManager;
        this.pollEventService = pollEventService;
    }

    @GetMapping
    public Collection<Poll> getAllPolls() {
        Collection<Poll> polls = pollManager.getPolls().values();

        for (Poll poll : polls) {
            for (VoteOption voteOption : poll.getOptions()) {
                voteOption.setVoteCount(0);
            }

            for (Vote vote : pollManager.getVotes().values()) {
                for (VoteOption voteOption : poll.getOptions()) {
                    if (voteOption.getPresentationOrder() == vote.getVotesOn().getPresentationOrder()) {
                        voteOption.setVoteCount(voteOption.getVoteCount() + 1);
                    }
                }
            }
        }
        return polls;
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
        pollEventService.registerPollTopic(pollId);
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
