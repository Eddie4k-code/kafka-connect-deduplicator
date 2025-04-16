package io.github.eddie4k.DuplicateMessageDetector.searchstrategies;

import org.apache.kafka.connect.data.Struct;


public class SchemaRecursiveSearchStrategy implements SearchStrategy<Struct> {

    @Override
    public Object Search(Struct object, String fieldName) {

        if (object == null) {
            return null;
        }

        /* Search Top Level  */
        if (object.schema().fields().stream().anyMatch(f -> f.name().equals(fieldName) && f.schema().type().isPrimitive())) {
            return object.get(fieldName);
        }

        /* Search Nested Structs recursivley for field */
        for (org.apache.kafka.connect.data.Field field : object.schema().fields()) {
            Object value = object.get(field);
            if (value instanceof Struct) {
                Object result = this.Search((Struct) value, fieldName);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
    
}
