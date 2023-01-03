# Lab 2

- Produce and consume Kafka message in Java applications 

Open two terminals and cd to this exercise directory:

```
cd /tmp/resource/exercise-code/producer-consumer-java
```
Compile / package the code. Note you will need to issue this command if any code is changed

```
mvn clean package
```
### 1. Create the topic

``` 
kafka-topics --create \
  --bootstrap-server edge2ai-0.dim.local:9092 \
  --replication-factor 1 \
  --partitions 1 \
  --topic customers
  ```
 
### 2. Start the consumer

In one of the terminals, run the following command:

``` 
mvn exec:java \
    -Dexec.mainClass="com.cloudera.training.kafka.solution.SimpleConsumer" \
    -Dexec.args="edge2ai-0.dim.local:9092 customers"
```

You should see the consumer pause, waiting for messages in the `customers` topic

### 3. Start the Producer

The producer expects the following arguments:
- List of Brokers
- Topic name
- Number of messages to produce

In the second terminal tab/window, run the following command to produce `500` message to `customers`topic:

``` 
mvn exec:java \
    -Dexec.mainClass="com.cloudera.training.kafka.solution.SimpleProducer" \
    -Dexec.args="edge2ai-0.dim.local:9092 customers 500"
``` 
