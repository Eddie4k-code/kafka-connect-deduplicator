package io.github.eddie4k.DuplicateMessageDetector.caches;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import io.github.eddie4k.DuplicateMessageDetector.caches.Cache;

/* Implementation for a In Memory Cache */

public class InMemoryCache implements Cache {
    
    private ConcurrentHashMap<Object, Object> cache = new ConcurrentHashMap<Object, Object>();

    @Override
    public void put(Object key, Object value) {
        cache.put(key, value);
    }

    @Override
    public boolean exists(Object key) {
        return cache.containsKey(key);
    }

    public void clear() {
        cache.clear();
    }


}
