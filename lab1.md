# Lab 1

- Use the command-line interface to create and manage Kafka topics 
- Publish messages and read messages from the command line
- Understand topic offsets

### 1. Creating topics

``` 
kafka-topics --create \
  --bootstrap-server $HOSTNAME:9092 \
  --replication-factor 1 \
  --partitions 1 \
  --topic device_status
  ```
 
### 2. List topics

``` 
kafka-topics --list \
  --bootstrap-server $HOSTNAME:9092
```
  
 ### 3. Describe topics

``` 
kafka-topics --describe \
  --bootstrap-server $HOSTNAME:9092 \
  --topic device_status
```

### 4. Producing messages

``` 
kafka-console-producer \
  --bootstrap-server $HOSTNAME:9092 \
  --topic device_status
```

### 5. Checking topic offsets

``` 
kafka-run-class kafka.tools.GetOffsetShell \
  --broker-list $HOSTNAME:9092 \
  --topic device_status --time -1
```

> The numbers specified by --time -1 and -2 mean "latest offset" and "earliest offset", respectively

### 6. Consuming messages

``` 
kafka-console-consumer \
  --bootstrap-server $HOSTNAME:9092 \
  --topic device_status \
  --from-beginning \
  --group my-cli-consumer
```

### 7. Checking consumer offset

``` 
kafka-consumer-groups \
  --bootstrap-server $HOSTNAME:9092 \
  --group my-cli-consumer \
  --describe
```

### 8. Produce and consume messages

a. Open a second terminal and produce more message (step 4). You should see them been consumed inmediately in the consumer terminal.  
b. Check consumer offsets again (step 7). Lag should be zero, which means all produced messages were consumed.  
c. Now stop consumer and produce more messages (step 4). Check consumer offset again (step 7) to see lag, which means there are messages that were not consumed yet.  
d. Start the consumer again (step 6), then check consumer offset (step 7).  

### 9. Change topic replicas

```             
kafka-topics --alter \
  --bootstrap-server $HOSTNAME:9092 \
  --topic device_status \
  --partitions 2
```

> **WARNING**: Try to decrease the number of partitions and see the result!

### 10. Topic deletion

```
kafka-topics --delete \
  --bootstrap-server $HOSTNAME:9092 \
  --topic device_status
  ```
