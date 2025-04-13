package io.github.eddie4k.DuplicateMessageDetector;



import org.apache.kafka.common.cache.SynchronizedCache;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SchemaUtil;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

import io.github.eddie4k.DuplicateMessageDetector.caches.InMemoryCache;
import io.github.eddie4k.DuplicateMessageDetector.caches.Cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.apache.kafka.connect.transforms.util.Requirements.requireMap;
import static org.apache.kafka.connect.transforms.util.Requirements.requireStruct;

public class DuplicateMessageDetector<R extends ConnectRecord<R>> implements Transformation<R>
{

    /* Describe transformation */
    public static final String OVERVIEW_DOC = "Detects duplicate messages based on a unique key";

    public final static HashSet<String> supportedMethods = new HashSet<String>(Arrays.asList("in_memory", "redis"));

    private String cacheMethod;
    private String uniqueKey;

    private Cache cache;

 

    private interface ConfigName {
        String UNIQUE_KEY = "unique.key";
        String CACHE_METHOD = "cache.method";
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
            "The cache method to use for storing seen messages. Options: " + String.join(", ", supportedMethods));




    /* Initalize configurations */
    @Override
    public void configure(Map<String, ?> props) {
        SimpleConfig config = new SimpleConfig(CONFIG_DEF, props);
        this.uniqueKey = config.getString(ConfigName.UNIQUE_KEY);
        this.cacheMethod = config.getString(ConfigName.CACHE_METHOD);

        /* Make sure the cache method is supported */
        if (!supportedMethods.contains(cacheMethod)) {
            throw new IllegalArgumentException("Unsupported cache method: " + cacheMethod);
        }
    }

    @Override
    public R apply(R record) {
        if (record.value() == null) {
            return record;
        } else if (record.value() instanceof Map) {
            return applySchemaless(record);
        } else {
            return applySchema(record);
        }
    }

       
    @SuppressWarnings("unchecked")
    private R applySchemaless(R record) {
        Map<String, Object> value = (Map<String, Object>) record.value();

        if (!value.containsKey(uniqueKey)) {
            throw new IllegalArgumentException("Unique key not found in record");
        }

        this.initalizeCache();

        String uniqueValue = value.get(uniqueKey).toString();

        if (cache.exists(uniqueValue)) {
            return null;
        } else {
            cache.put(uniqueValue, uniqueValue);
            return record;
        }
    }

    private R applySchema(R record) {
        return record;
    }

    private void initalizeCache() {
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

}
