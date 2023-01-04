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

### 5. Copy the content of `customers_v1.asc`file and paste in the "Schema Text" field. Run following commands to print file content, so you can copy it:

````
cat customers_v1.asc
````

### 6. Leave the other fields unchanged

### 7. Review the schema, then click "Save"

You should see the schema listed in the schema registry

### 8. Click the customers_avro schema to display the details

Note there is one version "v1" of the schema

![schema-details](https://user-images.githubusercontent.com/32500181/210565090-1c1d384a-4dc6-436e-8d73-f51713cf81c5.png)

## Execute the pipeline

### 1. Create the topic

``` 
kafka-topics --create \
  --bootstrap-server edge2ai-0.dim.local:9092 \
  --replication-factor 1 \
  --partitions 1 \
  --topic customers_avro
  ```
 
### 2. Start the consumer

In one of the terminals, run the following command:

``` 
mvn -q exec:java \
    -Dexec.mainClass="com.cloudera.training.kafka.solution.SimpleConsumer" \
    -Dexec.args="edge2ai-0.dim.local:9092 customers_avro" \
    -Dlog4j.configuration=file:src/main/consumer-log4j.properties
```

You should see the consumer pause, waiting for messages in the `customers_avro` topic

### 3. Start the Producer

In the second terminal tab/window, run the following command to produce only `10` message to `customers_avro`topic:

``` 
mvn -q exec:java \
    -Dexec.mainClass="com.cloudera.training.kafka.solution.SimpleProducer" \
    -Dexec.args="edge2ai-0.dim.local:9092 customers_avro 10" \
    -Dlog4j.configuration=file:src/main/resources/producer-log4j.properties
``` 

The producer will output the schema which it has downloaded from Schema Registry

```
Using internal version [1] of schema [customers_avro]
Here's the schema:
{
  "type": "record",
  "name": "Customer",
  "namespace": "com.cloudera.training.kafka.data",
  "doc": "Represents a customer for Loudacre Mobile",
  "fields": [
    {
      "name": "customer_id",
      "type": "int"
    },
    ...
  ]
}
```

You will then see information about the records sent by the producer:
```
Sending message 1 of 10
Sending customer id 185004 with NULL phone number
Sent message: {"customer_id": 185001, "first_name": "Jamie", "last_name": "Rodriguez", "phone_number": null}
```

### 4. See consumed messages

Switch to the consumer terminal. You should see output resembling the following in the consumer terminal:

```
========
Record partition:offset = 0:0
Record key = null
Record headers = [RecordHeader(key = value.schema.version.id, value = [3, 0, 0, 0, 1])]
Record value :
 {"customer_id": 185001, "first_name": "Jamie", "last_name": "Rodriguez", "phone_number": null}
========
```

### 5. View records in SMM

a. Navigate to Streams Messaging Manager and find the customers_avro topic.

![smm-topics-customers-avro](https://user-images.githubusercontent.com/32500181/210568865-c9bda9a2-2317-4260-b70e-50dd75db5b33.png)

b. Click the magnifying glass to open the "Data Explorer"

c. Notice the records appear with odd characters in the "Value" field

> This is because the data is Avro-serialized and SMM thinks the values are String

![smm-data-explorer](https://user-images.githubusercontent.com/32500181/210568922-e6446fe3-48b3-47da-a8ae-1a61fe6236c9.png)

d. Change the deserializer for the "Values" field to "Avro" using the `Values` drop-down. (See screenshot above)

e. You should now see the values displayed in readable JSON format

![smm-topics-customers-avro-with-schema](https://user-images.githubusercontent.com/32500181/210569160-5c6ba9cb-a54a-4255-9b70-c40883e3f27c.png)

- The data is in Avro format, but SMM is rendering the data using JSON notation (this is common with Avro data)

- Note the `null` values for the `phone_number` field


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

 
