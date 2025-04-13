package io.github.eddie4k.DuplicateMessageDetector.caches;

import java.util.HashMap;

import io.github.eddie4k.DuplicateMessageDetector.caches.Cache;

/* Implementation for a In Memory Cache */

public class InMemoryCache implements Cache {

    private HashMap<Object, Object> cache = new HashMap<>();

    @Override
    public void put(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public boolean exists(String key) {
        return cache.containsKey(key);
    }


}
