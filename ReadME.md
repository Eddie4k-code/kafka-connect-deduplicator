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

⚠️ Warning: In-memory caching is not recommended for production use. Consider using a distributed cache like Redis for better reliability and scalability.

---

## 🛠 Configuration

| Config Key              | Type    | Required | Default     | Description |
|------------------------|---------|----------|-------------|-------------|
| `unique.key`           | string  | ✅        | -           | The field name used to uniquely identify each record |
| `cache.method`         | string  | ❌        | `in_memory` | Options: `in_memory`, `redis` |
| `field.search.strategy`| string  | ✅        | `path`      | Options: `path`, `recursive` |
| `enable.cache.clear`   | boolean | ❌        | `false`     | Enables periodic cache clearing |
| `clear.cache.ms`       | long    | ❌        | `1000`      | Interval in milliseconds for clearing the cache |

---


## 🔍 Search Strategies

The `field.search.strategy` config allows two strategies for finding the `unique.key` in a message:

### 1. `path` (Default)
- **How it works**: Uses dot-notation to traverse nested fields (`a.b.c`).
- **Performance**: ✅ Fastest. Direct lookup without unnecessary recursion.
- **Best for**: Consistently structured records with known nesting.
- **Fails if**: Any part of the path doesn't exist.

> 🧪 Example:
> 
> With record:
> ```json
> {
>   "outer": {
>     "inner": {
>       "id": "abc123"
>     }
>   }
> }
> ```
> And `unique.key=outer.inner.id` ➜ value `"abc123"` is found.

---

### 2. `recursive`
- **How it works**: Recursively inspects all nested fields until it finds a match for the field name.
- **Performance**: ❌ Slower. Visits lots of nested fields before finding match.
- **Best for**: Inconsistent or deeply nested record structures.
- **Fails if**: Field name doesn't exist at all (but less brittle than `path`).

> 🧪 Example:
> With record:
> ```json
> {
>   "meta": {
>     "tracking": {
>       "id": "abc123"
>     }
>   }
> }
> ```
> And `unique.key=id` ➜ value `"abc123"` is found by deep search.

---

## 🧠 Cache Strategies

### `in_memory`
- Keeps a simple map of keys in memory.
- ✅ Fast, but not distributed.
- ❌ All state is lost on restart.

### `redis` (Planned or in development)
- Distributed cache.
- Survives process restarts.
- Slower than in-memory, but suitable for horizontal scaling.

---



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