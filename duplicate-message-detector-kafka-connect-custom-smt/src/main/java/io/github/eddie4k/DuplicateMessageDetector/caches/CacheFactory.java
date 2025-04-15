package io.github.eddie4k.DuplicateMessageDetector.caches;

public class CacheFactory {
    public static Cache createCache(String cacheType) {
        switch (cacheType) {
            case "in_memory":
                return new InMemoryCache();
            default:
                throw new IllegalArgumentException("Invalid cache type: " + cacheType);
        }
    }    
}
