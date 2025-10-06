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

    public Map<String, Integer> getPollVotes(long pollId) {
        String key = "poll:" + pollId;
        Map<String, String> result = jedis.hgetAll(key);
        Map<String, Integer> converted = new HashMap<>();

        for (Map.Entry<String, String> entry : result.entrySet()) {
            converted.put(entry.getKey(), Integer.parseInt(entry.getValue()));
        }

        return converted;
    }

    public void cachePollVotes(long pollId, Map<String, Integer> votes) {
        if (votes == null || votes.isEmpty()) return;

        String key = "poll:" + pollId;
        Map<String, String> toStore = new HashMap<>();
        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            toStore.put(entry.getKey(), entry.getValue().toString());
        }

        jedis.hset(key, toStore);
        jedis.expire(key, CACHE_TTL_SECONDS);
    }

    public void incrementVote(long pollId, String optionId) {
        String key = "poll:" + pollId;
        jedis.hincrBy(key, optionId, 1);
    }

    public void invalidatePoll(long pollId) {
        String key = "poll:" + pollId;
        jedis.del(key);
    }

    public void close() {
        jedis.close();
    }
}
