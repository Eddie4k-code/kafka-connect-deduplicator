package io.github.eddie4k.DuplicateMessageDetector.caches;

public interface Cache {
    public abstract void put(Object key, Object value);
    public abstract boolean exists(Object key);
}
