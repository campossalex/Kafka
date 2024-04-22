/**
 * Copyright (C) Cloudera, Inc. 2019
 */
package com.cloudera.training.kafka.solution;

import static com.hortonworks.registries.schemaregistry.serdes.avro.AbstractAvroSnapshotSerializer.SERDES_PROTOCOL_VERSION;
import static com.hortonworks.registries.schemaregistry.serdes.avro.SerDesProtocolHandlerRegistry.CURRENT_PROTOCOL;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.apache.avro.generic.GenericData;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hortonworks.registries.schemaregistry.serdes.avro.kafka.KafkaAvroDeserializer;

import java.util.*;  

public class SimpleConsumer {

	static private final Logger logger = Logger.getLogger(SimpleProducer.class);
	private Properties props = new Properties();
	private String topic;

	public static void main(String[] args) throws IOException {

		String bootstrapServers = args[0];
		String topic = args[1];
			SimpleConsumer simpleConsumer = new SimpleConsumer(topic, bootstrapServers);
		simpleConsumer.consume();
	}

	public SimpleConsumer(String topic, String bootstrapServers) throws IOException {

		this.topic = topic;
		try (FileInputStream fileInputStream = new FileInputStream("consumer.properties")) {
			props.load(fileInputStream);
		} catch (Exception e) {
			System.err.println(e);
			System.exit(42);
		}

		// Specify the protocol version being used to serialize the avro messages
		// This is not the *schema* version, nor the Schema Registry REST API version
		props.put(SERDES_PROTOCOL_VERSION, CURRENT_PROTOCOL);

		/*
		 * If "schemaregistry.reader.schema.versions" is present, then use that version
		 * of the schema to read the data.
		 * 
		 * The consumer.properties file shows an example. This would be used if we
		 * wanted to specify an exact version of the schema to use You can un-comment
		 * the property schemaregistry.reader.schema.versions in consumer.properties to
		 * force the consumers to use a specific version of the schema when reading
		 */
		if (props.containsKey(KafkaAvroDeserializer.READER_VERSIONS)) {
			ObjectMapper mapper = new ObjectMapper();
			String json = props.getProperty(KafkaAvroDeserializer.READER_VERSIONS);
			props.put(KafkaAvroDeserializer.READER_VERSIONS, mapper.readValue(json, Map.class));
		}

		props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

	}

	public void consume() {
		try (KafkaConsumer<String, GenericData.Record> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Arrays.asList(topic));

			System.out.printf("Starting consumption from topic %s:%n", topic);
			while (true) {
				ConsumerRecords<String, GenericData.Record> records = consumer.poll(Duration.ofMillis(500));
				for (ConsumerRecord<String, GenericData.Record> record : records) {

					try {
						System.out.printf("========%n");
						System.out.printf("Record partition:offset = %d:%d%n", record.partition(), record.offset());
						System.out.printf("Record key = %s%n", record.key());
						System.out.printf("Record headers = %s%n", Arrays.toString(record.headers().toArray()));
						System.out.printf("Record value :%n %s%n", record.value());

						consumer.commitSync(Collections.singletonMap(new TopicPartition(topic, record.partition()), new OffsetAndMetadata(record.offset() + 1)));

						Thread.sleep(1500);
					} catch (Exception e) {
						String message = String.format(
								"ERROR: Could not deserialize message:%n" + "Record partition = %d%n"
										+ "Record offset = %d%n" + "Record key = %s%n" + "Record headers = %s%n"
										+ "Record value = %s%n",
								record.partition(), record.offset(), record.key(), record.value());
						System.out.println(message);
						logger.error(message);
						logger.error("Caught exception while processing records", e);
					}
				}
			}
		}
	}

}