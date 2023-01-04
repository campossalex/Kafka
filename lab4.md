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
cat customers_v1.avsc
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


## Evolve the Schema


We will use Schema Registry to create a new version of the schema, adding a default value for `phone_number` field.

View the details of the customer_avro schema in Schema Registry, by clicking on it

### 1. Edit the customers_avro schema by clicking the "Pen" icon

![schema_registry_edit_schema](https://user-images.githubusercontent.com/32500181/210600632-e1fe8134-1428-4c5b-bf33-a435fc57fdf9.png)

### 2. An "Edit Version" dialog will appear with the current schema in the "Schema Text" box.

We recommend uploading the new version, to avoid typos. Press "Clear".

### 3. You should now see a prompt resembling the below screenshot

![schema_registry_clear_dialog](https://user-images.githubusercontent.com/32500181/210602550-662645cc-6c08-4a7f-926e-2009ba527454.png)

### 4. Copy the content of `customers_v2.asc`file and paste in the "Schema Text" field. Run following commands to print file content, so you can copy it:

````
cat customers_v2.avsc
````

Important review the schema to see the changes.

### 5. Provide a description for this revision

### 6. Press "Validate". You should see a "Schema is Valid" message

![schema_registry_add_version](https://user-images.githubusercontent.com/32500181/210601144-f3d09788-bed3-4475-8f7a-fd38c8d9a585.png)

Press "Save". You should see a green message from Schema Registry in the upper right corner indicating the schema was saved

### 7. Run the Producer again

In the second terminal tab/window, run the following command to produce only `10` message to `customers_avro`topic:

``` 
mvn -q exec:java \
    -Dexec.mainClass="com.cloudera.training.kafka.solution.SimpleProducer" \
    -Dexec.args="edge2ai-0.dim.local:9092 customers_avro 10" \
    -Dlog4j.configuration=file:src/main/resources/producer-log4j.properties
``` 

You should see output similar to the previous run of the producer. Note the differences between the previous run.

### 8. View the results in the Consumer

The consumer should still be running. Simply switch to the consumer terminal.

You should see the new default value for `phone_number` in the consumed records

### 9. View the data using Streams Messaging Manager

Revisit the data in the `customers_avro` topic using Streams Messaging Manager. Remember to use the Avro schema (either version 1 or 2 will work) to view the messages. You should see the new default phone number on the latest records. The earliest records will still show `null` for the `phone_number`. This is because the producer actually wrote null as the `phone_number` value in the earlier records.

