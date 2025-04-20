# 🔁 DuplicateMessageDetector

A Kafka Connect **Single Message Transform (SMT)** that detects and filters out duplicate messages based on a **unique key** field in the record. This is useful in streaming pipelines where upstream systems may occasionally produce repeated records.

Supports both **schemaless** and **schema-based** records, and allows flexible configuration of how to find and identify the unique key.

---

## ⚙️ Features

- Detects and drops duplicate Kafka messages
- Configurable unique key for identifying duplicates
- Supports both schema-based and schemaless records
- Supports field search strategies: `recursive` or `path`
- Pluggable cache strategies (currently supports: `in_memory`)
- Automatic cache clearing based on a specified ms interval

---

## 🔧 Configuration

### Kafka Connect config example

```json
"transforms": "Deduplicator",
"transforms.Deduplicator.type": "io.github.eddie4k.DuplicateMessageDetector.DuplicateMessageDetector",
"transforms.Deduplicator.unique.key": "after.order_id",
"transforms.Deduplicator.cache.method": "in_memory",
"transforms.Deduplicator.field.search.strategy": "recursive"
"transforms.Deduplicator.
```


## 🧹 Automatic Cache Clearing
To prevent unbounded memory growth or to support time-based deduplication, this transformation supports automatic cache clearing at a configurable interval.

### 🔧 Configuration Options

| Property             | Type    | Default | Description                                      |
|----------------------|---------|---------|--------------------------------------------------|
| `enable.cache.clear` | boolean | `false` | Whether to enable automatic cache clearing       |
| `clear.cache.ms`     | long    | `60000` | How often (in milliseconds) to clear the cache   |

### ⚠️ Performance Considerations
The cache is cleared in a background thread, independently of record processing.

Clearing the cache too frequently (e.g., every few milliseconds) may reduce deduplication accuracy and introduce unnecessary CPU overhead.

A thread-safe cache (like ConcurrentHashMap) is used by default to ensure safe access from multiple threads.

For Redis-based caches, frequent clearing could increase I/O and reduce efficiency — consider using Redis TTLs configuration instead. (Coming soon)

## Download
You can download the latest compiled .jar file from the Releases section of this repository.

🔽 Latest Release
Go to the Releases page.

Find the most recent version (e.g., v1.0.0).

Download the .jar file under Assets.