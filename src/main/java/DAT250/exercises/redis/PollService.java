package DAT250.exercises.redis;

import DAT250.exercises.jpa.polls.Vote;
import DAT250.exercises.jpa.polls.VoteRepository; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PollService {

    private final PollCache cache;
    private final VoteRepository voteRepository;

    public PollService(VoteRepository voteRepository) {
        this.cache = new PollCache("redis://localhost:6379");
        this.voteRepository = voteRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getPollResults(String pollId) {
        Map<String, Integer> cached = cache.getPollVotes(pollId);
        if (!cached.isEmpty()) {
            System.out.println("Returning from Redis cache");
            return cached;
        }

        // Cache miss — querying H2 database
        Map<String, Integer> result = new HashMap<>();
        try {
            Long numericId = Long.parseLong(pollId);
            List<Vote> votes = voteRepository.findByPollId(numericId);
            for (Vote vote : votes) {
                String optionId = String.valueOf(vote.getVotesOn().getPresentationOrder());
                result.put(optionId, result.getOrDefault(optionId, 0) + 1);
            }
        } catch (NumberFormatException e) {
            System.out.println("Poll ID is not numeric; skipping DB lookup.");
        }

        cache.cachePollVotes(pollId, result);
        return result;
    }

    @Transactional
    public void registerVote(String pollId, String optionId) {
        cache.incrementVote(pollId, optionId);
        System.out.println("Incremented Redis cache for poll " + pollId + ", option " + optionId);
    }

    public void invalidateCache(String pollId) {
        cache.invalidatePoll(pollId);
        System.out.println("Cache invalidated for poll " + pollId);
    }
}
