package io.github.eddie4k.DuplicateMessageDetector.searchstrategies;


public interface SearchStrategy<T> {
    public Object Search(T object, String fieldName);
}
