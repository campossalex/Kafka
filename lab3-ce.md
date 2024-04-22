# Lab 3

- Produce and consume Kafka message in Java applications 

Open two terminals and cd to this exercise directory:

```
cd exercise-code/producer-consumer-java
```
Compile / package the code. Note you will need to issue this command if any code is changed

```
mvn clean package
```
### 1. Create the topic

``` 
Create a new topic (customers) using the web UI in SMM. Use the same configuration as below.
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

### 4. See consumed messages

Switch to the consumer terminal. You should see output resembling the following in the consumer terminal:

```
Received record with offset = 0, key = null, value = 185001,Holly,Hawkins,909-555-8546
Received record with offset = 1, key = null, value = 185002,Linda,Ortega,602-555-6596
```

### 5. Check SMM

Opem SMM in the Overview tab and click `customers` topic, then the only partition of this topic.  
You should be able to see one partition, a `producer` (left hand side) and a `consumer group`(right hands side) connected to the topic.

<img width="1405" alt="lab3_smm_1" src="https://user-images.githubusercontent.com/32500181/210392825-13f6e87d-2aaa-4d49-b90c-b71f6ea17752.png">
