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

---

## 🔧 Configuration

### Kafka Connect config example

```json
"transforms": "Deduplicator",
"transforms.Deduplicator.type": "io.github.eddie4k.DuplicateMessageDetector.DuplicateMessageDetector",
"transforms.Deduplicator.unique.key": "after.order_id",
"transforms.Deduplicator.cache.method": "in_memory",
"transforms.Deduplicator.field.search.strategy": "recursive"
```