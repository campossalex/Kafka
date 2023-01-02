# Lab 1

- Use the command-line interface to create and manage Kafka topics 
- Publish messages and read messages from the command line

### 1. Creating topics

``` 
kafka-topics --create \
  --bootstrap-server edge2ai-0.dim.local:9092 \
  --replication-factor 1 \
  --partitions 1 \
  --topic device_status
  ```
 
### 2. List topics

``` 
kafka-topics --list \
  --bootstrap-server edge2ai-0.dim.local:9092
```
  
 ### 3. Describe topics

``` 
kafka-topics --describe \
  --bootstrap-server edge2ai-0.dim.local:9092 \
  --topic device_status
```

### 4. Publishing messages

``` 
kafka-console-producer \
  --bootstrap-server edge2ai-0.dim.local:9092 \
  --topic device_status
```

### 5. Checking topic offsets

``` 
kafka-run-class kafka.tools.GetOffsetShell \
  --broker-list edge2ai-0.dim.local:9092 \
  --topic device_status --time -1
```

> The numbers specified by --time -1 and -2 mean "latest offset" and "earliest offset", respectively

### 6. Consuming messages

``` 
kafka-console-consumer \
  --bootstrap-server edge2ai-0.dim.local:9092 \
  --topic device_status \
  --from-beginning \
  --group my-consumer
```

### 8. Change topic replicas

```             
kafka-topics --alter \
  --bootstrap-server edge2ai-0.dim.local:9092 \
  --topic device_status \
  --partitions 2
```

> **WARNING**: Try to decrease the number of partitions and see the result!
  
