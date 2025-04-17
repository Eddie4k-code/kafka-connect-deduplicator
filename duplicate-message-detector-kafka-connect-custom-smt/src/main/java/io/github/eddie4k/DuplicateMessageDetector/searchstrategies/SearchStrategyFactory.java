package io.github.eddie4k.DuplicateMessageDetector.searchstrategies;

import java.util.HashMap;

public class SearchStrategyFactory {


    private static HashMap<String, SearchStrategy> cache = new HashMap<String, SearchStrategy>();

    public static SearchStrategy createSearchStrategy(String hasSchema, String strategyType) {

        
        var isCached = cache.get(hasSchema.toString() + strategyType);

        // Check if the search strategy is already cached
        if (isCached != null) {
            return isCached;
        }

        

       switch (hasSchema) {
            case "true":
                if (strategyType == "recursive") {
                    storeInCache(hasSchema.toString() + strategyType, new SchemaRecursiveSearchStrategy());
                    return new SchemaRecursiveSearchStrategy();
                } else {
                    storeInCache(hasSchema.toString() + strategyType, new SchemaPathSearchStrategy());
                    return new SchemaPathSearchStrategy();
                }
            case "false":
                if (strategyType == "path") {
                    storeInCache(hasSchema.toString() + strategyType, new SchemalessPathSearchStrategy());
                    return new SchemalessPathSearchStrategy();
                } else {
                    storeInCache(hasSchema.toString() + strategyType, new SchemalessRecursiveSearchStrategy());
                    return new SchemalessRecursiveSearchStrategy();
                }
            default:
                throw new IllegalArgumentException("Invalid hasSchema value: " + hasSchema);
       }
    }
    
    

    private static void storeInCache(String key, SearchStrategy searchStrategy) {
        cache.put(key, searchStrategy);
    }
}
