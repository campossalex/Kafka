# Lab 4

- Integrate Schema Registry to Kafka pipelines 

Open two terminals and cd to this exercise directory:

```
cd /tmp/resource/exercise-code/producer-consumer-java
```
Compile / package the code. Note you will need to issue this command if any code is changed

```
mvn clean package
```

## Register Schema

### 1. In the Schema Registry, click the green "Plus button" to add a schema

![add-schema-button](https://user-images.githubusercontent.com/32500181/210563701-c1b420c8-14cb-4138-9916-a71102bfd949.png)

### 2. An "Add New Schema" dialog will pop up. See below

![add-schema-dialog](https://user-images.githubusercontent.com/32500181/210563773-b04b4b59-3058-47f2-90d2-e51ec09e71d6.png)

### 3. Enter `customers_avro` for the Name of the schema Important ensure it matches the kafka topic name customers_avro exactly.

> Streams Messaging Manager assumes the schema name for a particular topic is the exact same as the topic name. This is configurable using SMM's REST API
Add a description for the schema

### 4. Click anywhere in the "Schema Text" field, not the green "Browse" button.

### 5.Copy the content of `customers_v1.asc`file and paste in the "Schema Text" field. Run following commands to print file content, so you can copy it:
````
cat customers_v1.asc
````

### 6. Leave the other fields unchanged

### 7. Review the schema, then click "Save"

You should see the schema listed in the schema registry

### 8. Click the customers_avro schema to display the details

Note there is one version "v1" of the schema

![schema-details](https://user-images.githubusercontent.com/32500181/210565090-1c1d384a-4dc6-436e-8d73-f51713cf81c5.png)



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

 
