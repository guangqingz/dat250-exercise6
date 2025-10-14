package DAT250.exercises.redis;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.UnifiedJedis;

public class PollCache {

    private final UnifiedJedis jedis;
    private final int CACHE_TTL_SECONDS = 300; // 5 minutes

    public PollCache(String redisUrl) {
        this.jedis = new UnifiedJedis(redisUrl);
        System.out.println("Connected to Redis: " + jedis.ping());
    }

    private String key(String pollId) {
        return "poll:" + pollId;
    }

    public Map<String, Integer> getPollVotes(String pollId) {
        Map<String, String> result = jedis.hgetAll(key(pollId));
        Map<String, Integer> converted = new HashMap<>();

        for (Map.Entry<String, String> entry : result.entrySet()) {
            converted.put(entry.getKey(), Integer.parseInt(entry.getValue()));
        }

        return converted;
    }

    public void cachePollVotes(String pollId, Map<String, Integer> votes) {
        if (votes == null || votes.isEmpty()) return;

        Map<String, String> toStore = new HashMap<>();
        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            toStore.put(entry.getKey(), entry.getValue().toString());
        }

        jedis.hset(key(pollId), toStore);
        jedis.expire(key(pollId), CACHE_TTL_SECONDS);
    }

    public void incrementVote(String pollId, String optionId) {
        jedis.hincrBy(key(pollId), optionId, 1);
    }

    public void invalidatePoll(String pollId) {
        jedis.del(key(pollId));
    }

    public void close() {
        jedis.close();
    }
}
