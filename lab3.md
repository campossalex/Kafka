# Lab 3

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

As you can see, `producer` is identified as `producer-1`. Not very descriptive. Time to change this.

### 6. Add the client.id to producer

Open the Producer java to edit the code. Use vi or other editor:

```
vi src/main/java/com/cloudera/training/kafka/solution/SimpleProducer.java
```
After line 30, add the following instruction to the code:

```
        props.setProperty(ProducerConfig.CLIENT_ID_CONFIG, "java-cli-producer");
```
> Take care with the code indentation. Align the new instruction with the previous one already declared in the code.

Now our `producer` will be call `java-cli-producer`

Compile / package the code again.

```
mvn clean package
```

Start the Producer again

``` 
mvn exec:java \
    -Dexec.mainClass="com.cloudera.training.kafka.solution.SimpleProducer" \
    -Dexec.args="edge2ai-0.dim.local:9092 customers 500"
``` 

### 7. Check SMM

It takes some seconds, but you will be able to see a `java-cli-producer` producer linked to `customers` topic.

<img width="1410" alt="lab3_smm_2" src="https://user-images.githubusercontent.com/32500181/210400178-54f45fe0-c148-4d21-b6b4-c5266efecdd0.png">

