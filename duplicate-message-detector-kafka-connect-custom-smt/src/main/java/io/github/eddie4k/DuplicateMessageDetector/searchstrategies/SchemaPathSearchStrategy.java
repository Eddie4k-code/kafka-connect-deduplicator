package io.github.eddie4k.DuplicateMessageDetector.searchstrategies;

import org.apache.kafka.connect.data.Struct;

public class SchemaPathSearchStrategy implements SearchStrategy<Struct> {

    @Override
    public Object Search(Struct object, String fieldName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'Search'");
    }
    
}
