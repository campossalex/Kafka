# Lab 5

- Enable interceptor for Kafka clients
- Review End to End latency metrics in SMM

Open two terminals and cd to this exercise directory:

```
cd /tmp/resources/exercise-code/avro-schema-kafka
```


To enabled the application to start logging end-to-end latency metrics we use an interceptor for the Kafka client, which can be set through the configuration file.

### 1. Enable consumer interceptor 

Edit the `consumer.properties` file and uncomment the following line:

```
interceptor.classes=com.hortonworks.smm.kafka.monitoring.interceptors.MonitoringConsumerInterceptor
```

### 2. Enable producer interceptor 

Edit the `producer.properties` file and uncomment the following line:

```
interceptor.classes=com.hortonworks.smm.kafka.monitoring.interceptors.MonitoringProducerInterceptor

```

### 3. Compile / package the code

```
mvn clean package
```

### 4. Start the consumer

In one of the terminals, run the following command:

``` 
mvn -q exec:java \
    -Dexec.mainClass="com.cloudera.training.kafka.solution.SimpleConsumer" \
    -Dexec.args="edge2ai-0.dim.local:9092 customers_avro" \
    -Dlog4j.configuration=file:src/main/consumer-log4j.properties
```

You should see the consumer pause, waiting for messages in the `customers_avro` topic

### 5. Start the Producer

In the second terminal tab/window, run the following command to produce `1000` message to `customers_avro`topic:

``` 
mvn -q exec:java \
    -Dexec.mainClass="com.cloudera.training.kafka.solution.SimpleProducer" \
    -Dexec.args="edge2ai-0.dim.local:9092 customers_avro 1000" \
    -Dlog4j.configuration=file:src/main/resources/producer-log4j.properties
``` 

### 6. On the SMM UI, click the Overview tab (overview icon).

### 7. Click on the profile icon (profile icon) for the machine-data-avro topic to open the topic page.

You now should see data in the charts Messages Consumed and End-to-end latency. You may have to wait a few seconds until the charts appear. Refresh the page after a little while to see the data get updated.

![end-to-end-metrics](https://user-images.githubusercontent.com/32500181/210613548-cf200c6b-49ec-49ef-8143-7f49caa3f1e0.png)


> Tip: use following comnand to describe the consumer group
```
kafka-consumer-groups   --bootstrap-server edge2ai-0.dim.local:9092   --group cust-relations   --describe
```
