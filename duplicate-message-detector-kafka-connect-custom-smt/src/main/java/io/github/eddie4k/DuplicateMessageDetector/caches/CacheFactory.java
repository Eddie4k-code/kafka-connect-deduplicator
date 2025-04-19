package io.github.eddie4k.DuplicateMessageDetector.caches;

import java.util.HashMap;

public class CacheFactory {


    private static HashMap<String, Cache> CacheFactory = new HashMap<String, Cache>();

    public static Cache createCache(String cacheType) {

        /* Cache the cache ;) */
        if (CacheFactory.get(cacheType) != null) {
            return CacheFactory.get(cacheType);

        }

        switch (cacheType) {
            case "in_memory":
                CacheFactory.put(cacheType, new InMemoryCache());
                return CacheFactory.get(cacheType);
            default:
                throw new IllegalArgumentException("Invalid cache type: " + cacheType);
        }
    }    
}
