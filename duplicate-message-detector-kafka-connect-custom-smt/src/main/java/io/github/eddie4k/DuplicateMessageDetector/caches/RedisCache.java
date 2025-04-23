package io.github.eddie4k.DuplicateMessageDetector.caches;
import redis.clients.jedis.Jedis;

public class RedisCache implements Cache {

    private Jedis jedis;


    public RedisCache(String host, int port) {
        this.jedis = new Jedis(host, port);
    }

    @Override
    public void put(Object key, Object value) {
        jedis.set(key.toString(), value.toString());
    }

    @Override
    public boolean exists(Object key) {
        return jedis.exists(key.toString());
    }

    @Override
    public void clear() {
        jedis.flushDB();
    }


}