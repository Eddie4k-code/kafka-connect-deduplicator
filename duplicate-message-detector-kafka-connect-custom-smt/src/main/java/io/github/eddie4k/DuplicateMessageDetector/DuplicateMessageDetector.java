package io.github.eddie4k.DuplicateMessageDetector;



import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

import io.github.eddie4k.DuplicateMessageDetector.caches.InMemoryCache;
import io.github.eddie4k.DuplicateMessageDetector.searchstrategies.SearchStrategy;
import io.github.eddie4k.DuplicateMessageDetector.caches.Cache;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DuplicateMessageDetector<R extends ConnectRecord<R>> implements Transformation<R>
{

    /* Describe transformation */
    public static final String OVERVIEW_DOC = "Detects duplicate messages based on a unique key";

    public final static HashSet<String> supportedMethods = new HashSet<String>(Arrays.asList("in_memory", "redis"));
    private static final Logger log = LoggerFactory.getLogger(DuplicateMessageDetector.class);
    private String cacheMethod;
    private String uniqueKey;
    private String fieldSearchStrategy;
    private SearchStrategy searchStrategy;

    private Cache cache;

 

    private interface ConfigName {
        String UNIQUE_KEY = "unique.key";
        String CACHE_METHOD = "cache.method";
        String FIELD_SEARCH_STRATEGY = "field.search.strategy";
    }

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
        .define(ConfigName.UNIQUE_KEY,
            ConfigDef.Type.STRING,
            ConfigDef.Importance.HIGH,
            "The field name to use as the unique key for detecting duplicates.")
        .define(ConfigName.CACHE_METHOD,
            ConfigDef.Type.STRING,
            "in_memory",
            ConfigDef.Importance.MEDIUM,
            "The cache method to use for storing seen messages. Options: " + String.join(", ", supportedMethods))
        .define(ConfigName.FIELD_SEARCH_STRATEGY,
            ConfigDef.Type.STRING,
            1000,
            ConfigDef.Importance.HIGH,
            "The field search strategy to use for searching the unique key. Options: recursive, path");




    /* Initalize configurations */
    @Override
    public void configure(Map<String, ?> props) {
        SimpleConfig config = new SimpleConfig(CONFIG_DEF, props);
        this.uniqueKey = config.getString(ConfigName.UNIQUE_KEY);
        this.cacheMethod = config.getString(ConfigName.CACHE_METHOD);
        this.fieldSearchStrategy = config.getString(ConfigName.FIELD_SEARCH_STRATEGY);

        /* Make sure the cache method is supported */
        if (!supportedMethods.contains(cacheMethod)) {
            throw new IllegalArgumentException("Unsupported cache method: " + cacheMethod);
        }

        /* Make sure the field search strategy is supported */
        if (!fieldSearchStrategy.equals("recursive") && !fieldSearchStrategy.equals("path")) {
            throw new IllegalArgumentException("Unsupported field search strategy: " + fieldSearchStrategy);
        }


        this.searchStrategy = SearchStrategyFactory.createSearchStrategy(fieldSearchStrategy);
    }

    @Override
    public R apply(R record) {
        log.info("Applying transformation to record");
        if (record.value() instanceof Map) {
            log.info("Applying schemaless transformation");
            return applySchemaless(record);
        } else {
            log.info("Applying schema transformation");
            return applySchema(record);
        }
    }

       
    @SuppressWarnings("unchecked")
    private R applySchemaless(R record) {
        Map<String, Object> value = (Map<String, Object>) record.value();

        if (!value.containsKey(uniqueKey)) {
            throw new IllegalArgumentException("Unique key not found in record");
        }

        System.out.println("Initializing cache");
        log.info("Initializing cache");

        this.initalizeCache();

        String uniqueValue = value.get(uniqueKey).toString();

        if (cache.exists(uniqueValue)) {
            System.out.println("Duplicate message detected, skipping");
            log.info("Duplicate message detected, skipping");
            return null;
        } else {
            System.out.println("Storing unique message in cache");
            log.info("Storing unique message in cache");
            cache.put(uniqueValue, uniqueValue);
            return record;
        }

    }

    private R applySchema(R record) {
        if (record.value() == null) {
            return record;
        }

        log.info(record.toString());

        log.info("log fields");
        

        Struct struct = (Struct) record.value();

        log.info("log fields");

        struct.schema().fields().forEach(field -> {
            log.info("Field: " + field.name());
        });

        Object uniqueValue = this.fieldSearchStrategy.equals("recursive") ?
            searchStructRecursive(struct, uniqueKey) :
            searchPath(uniqueKey.split("\\."), struct);
        
        if (uniqueValue == null) {
            throw new IllegalArgumentException("Unique key not found in record");
        }

        System.out.println("Initializing cache");
        log.info("Initializing cache");
        this.initalizeCache();



        if (cache.exists(uniqueValue)) {
            System.out.println("Duplicate message detected, skipping");
            log.info("Duplicate message detected, skipping");
            return null;
        } else {
            System.out.println("Storing unique message in cache");
            log.info("Storing unique message in cache");
            cache.put(uniqueValue, uniqueValue);
            return record;
        }

    }

    private void initalizeCache() {
        if (cache != null) {
            return;
        }

        if (cacheMethod.equals("in_memory")) {
            this.cache = new InMemoryCache();
        }
    }



    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public void close() {
       
    }

    
    private Object searchStructRecursive(Struct struct, String fieldName) {
        if (struct == null) {
            return null;
        }

        if (struct.schema().fields().stream().anyMatch(f -> f.name().equals(fieldName))) {
            return struct.get(fieldName);
        }

        for (org.apache.kafka.connect.data.Field field : struct.schema().fields()) {
            Object value = struct.get(field);
            if (value instanceof Struct) {
                Object result = searchStructRecursive((Struct) value, fieldName);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
       
    }




    private Object searchPath(String[] fieldNames, Struct struct) {

        Object lastSeen = null;


        for (String fieldName:  fieldNames) {
            if (lastSeen == null) {
                lastSeen = struct.get(fieldName);
                continue;
            }  else if (lastSeen instanceof Struct) {
                    lastSeen = ((Struct)lastSeen).get(fieldName);
                    continue;
            } else {
                return lastSeen;
            }
            
        }

        return lastSeen;

    }

}
