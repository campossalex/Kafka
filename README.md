# Kafka

Kafka Streams hands-on

Open 3 shell terminals:

1. First, create the topics:

kafka-topics --create \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --partitions 1 \
    --topic streams-plaintext-input

kafka-topics --create \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --partitions 1 \
    --topic streams-wordcount-output \
    --config cleanup.policy=compact

2. Terminal one, run the WordCount app:

kafka-run-class org.apache.kafka.streams.examples.wordcount.WordCountDemo

3. Terminal two, run a Kafka producer:

kafka-console-producer --bootstrap-server localhost:9092 --topic streams-plaintext-input

4. Terminal three, run a Kafka consumer to print the aggregations:

kafka-console-consumer --bootstrap-server localhost:9092 \
    --topic streams-wordcount-output \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
