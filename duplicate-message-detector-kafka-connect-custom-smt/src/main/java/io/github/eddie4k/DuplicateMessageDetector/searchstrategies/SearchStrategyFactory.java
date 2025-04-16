package io.github.eddie4k.DuplicateMessageDetector.searchstrategies;

public class SearchStrategyFactory {

    public static SearchStrategy<?> createSearchStrategy(String hasSchema, String strategyType) {
       switch (hasSchema) {
            case "true":
                if (strategyType == "recursive") {
                    return new SchemaRecursiveSearchStrategy();
                } else {
                    return new SchemaPathSearchStrategy();
                }
            case "false":
                if (strategyType == "path") {
                    return new SchemaPathSearchStrategy();
                } else {
                    return new SchemaRecursiveSearchStrategy();
                }
            default:
                throw new IllegalArgumentException("Invalid hasSchema value: " + hasSchema);
       }
    }    
}
