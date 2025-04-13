package io.github.eddie4k.DuplicateMessageDetector.caches;

public interface Cache {
    public abstract void put(String key, String value);
    public abstract boolean exists(String key);
}
