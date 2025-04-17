package io.github.eddie4k.DuplicateMessageDetector.searchstrategies;

import org.apache.kafka.connect.data.Struct;

public interface SearchStrategy<T> {
    public Object Search(T struct, String fieldName);
}
