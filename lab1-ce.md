# Lab 1

- Use the command-line interface to create and manage Kafka topics 
- Publish messages and read messages from the command line
- Understand topic offsets

### 1. Creating topics

``` 
/opt/kafka/bin/kafka-topics.sh  --create \
  --bootstrap-server localhost:9094 \
  --replication-factor 1 \
  --partitions 1 \
  --topic device_status
  ```
 
### 2. List topics

``` 
/opt/kafka/bin/kafka-topics.sh --list \
  --bootstrap-server localhost:9094
```
  
 ### 3. Describe topics

``` 
/opt/kafka/bin/kafka-topics.sh --describe \
  --bootstrap-server localhost:9094 \
  --topic device_status
```

### 4. Producing messages

``` 
/opt/kafka/bin/kafka-console-producer.sh \
  --bootstrap-server localhost:9094 \
  --topic device_status
```

### 5. Checking topic offsets

``` 
/opt/kafka/bin/kafka-run-class.sh kafka.tools.GetOffsetShell \
  --broker-list localhost:9094 \
  --topic device_status --time -1
```

> The numbers specified by --time -1 and -2 mean "latest offset" and "earliest offset", respectively

### 6. Consuming messages

``` 
/opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9094 \
  --topic device_status \
  --from-beginning \
  --group my-cli-consumer
```

### 7. Checking consumer offset

``` 
/opt/kafka/bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9094 \
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
/opt/kafka/bin/kafka-topics.sh --alter \
  --bootstrap-server localhost:9094 \
  --topic device_status \
  --partitions 2
```

> **WARNING**: Try to decrease the number of partitions and see the result!

### 10. Topic deletion

```
/opt/kafka/bin/kafka-topics.sh --delete \
  --bootstrap-server localhost:9094 \
  --topic device_status
  ```
