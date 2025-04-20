package io.github.eddie4k.DuplicateMessageDetector.searchstrategies;

import java.util.HashMap;

public class SearchStrategyFactory {


    private static HashMap<String, SearchStrategy> cache = new HashMap<String, SearchStrategy>();

    public static SearchStrategy createSearchStrategy(String hasSchema, String strategyType) {
        var key = hasSchema.toString() + strategyType;
        
        var isCached = cache.get(key);

        // Check if the search strategy is already cached
        if (isCached != null) {
            return isCached;
        }

        

       switch (hasSchema) {
            case "true":
                if (strategyType == "recursive") {
                    storeInCache(key, new SchemaRecursiveSearchStrategy());
                    return getFromCache(key);
                } else {
                    storeInCache(key, new SchemaPathSearchStrategy());
                    return getFromCache(key);
                }
            case "false":
                if (strategyType == "path") {
                    storeInCache(key, new SchemalessPathSearchStrategy());
                    return getFromCache(key);
                } else {
                    storeInCache(key, new SchemalessRecursiveSearchStrategy());
                    return getFromCache(key);
                }
            default:
                throw new IllegalArgumentException("Invalid hasSchema value: " + hasSchema);
       }
    }
    
    

    private static void storeInCache(String key, SearchStrategy searchStrategy) {
        cache.put(key, searchStrategy);
    }

    private static SearchStrategy getFromCache(String key) {
        return cache.get(key);
    }
}
