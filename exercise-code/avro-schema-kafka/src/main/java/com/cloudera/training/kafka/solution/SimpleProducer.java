/**
 * Copyright (C) Cloudera, Inc. 2019
 */
package com.cloudera.training.kafka.solution;

import static com.hortonworks.registries.schemaregistry.serdes.avro.AbstractAvroSnapshotSerializer.SERDES_PROTOCOL_VERSION;
import static com.hortonworks.registries.schemaregistry.serdes.avro.SerDesProtocolHandlerRegistry.CURRENT_PROTOCOL;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import com.cloudera.training.kafka.datagen.CustomerSource;
import com.hortonworks.registries.schemaregistry.SchemaVersionInfo;
import com.hortonworks.registries.schemaregistry.client.SchemaRegistryClient;
import com.hortonworks.registries.schemaregistry.errors.SchemaNotFoundException;

public class SimpleProducer {

	private static final Logger logger = Logger.getLogger(SimpleProducer.class);
	Properties props = new Properties();
	private String schemaText;
	final private String propsFile = "producer.properties";
	final private Schema schema;

	private int maxMessages;
	private String topic;
	CustomerSource customers = new CustomerSource();

	public SimpleProducer(String topic, int maxMessages, String bootstrapServers) throws SchemaNotFoundException, NumberFormatException {
		try (FileInputStream fileInputStream = new FileInputStream(propsFile)) {
			props.load(fileInputStream);
		} catch (Exception e) {
			System.err.println(e);
			System.exit(42);
		}

		this.topic = topic;
		String schemaName = props.getProperty("com.cloudera.training.avro.exercise.schema.name");
		if (null == schemaName) {
			System.out.println("Missing com.cloudera.training.avro.exercise.schema.name property in " + propsFile);
			System.exit(1);
		}
		String schemaVersion = props.getProperty("com.cloudera.training.avro.exercise.schema.version");

		this.schemaText = retrieveSchema(schemaName, schemaVersion);

		this.schema = new Schema.Parser().parse(schemaText);
		this.maxMessages = maxMessages;

		props.put(SERDES_PROTOCOL_VERSION, CURRENT_PROTOCOL);
		props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
	}

	public static void main(String[] args) throws SchemaNotFoundException {

		String bootstrapServers = args[0];
		String topic = args[1];
		int maxMessages = Integer.parseInt(args[2]);

		// Instantiate the data generator for customer records
		SimpleProducer simpleProducer = new SimpleProducer(topic, maxMessages, bootstrapServers);
		simpleProducer.produceData();
	}

	public void produceData() {

		try (KafkaProducer<String, GenericData.Record> producer = new KafkaProducer<>(props)) {
			for (int messagesSent = 0; messagesSent < maxMessages; messagesSent++) {
				String customerData = customers.getNewCustomerInfo();

				String[] data = customerData.split(",");
				int customerId = Integer.parseInt(data[0]);
				String firstName = data[1];
				String lastName = data[2];
				String phoneNumber = data[3];

				// One out of every 3 customers has a NULL phone number :-(

				// TODO should this builder class be instantiated outside of the loop?
				// This producer retrieves the schema **when the producer starts**
				// and uses Avro's "GenericRecord" class.
				// "Static" classes can be used as well.
				System.out.printf("Sending message %d of %d%n", messagesSent, maxMessages);
				GenericRecordBuilder builder = new GenericRecordBuilder(schema);
				builder.set("customer_id", customerId);
				builder.set("first_name", firstName);
				builder.set("last_name", lastName);
				
				if (messagesSent % 3 != 0) {
					builder.set("phone_number", phoneNumber);
				} else {
					System.out.printf("Sending customer id %d with NULL phone number %n", customerId);
				}
				GenericData.Record customer = builder.build();

				ProducerRecord<String, GenericData.Record> message = 
						new ProducerRecord<>(this.topic, null, customer);
				producer.send(message);

				Thread.sleep(1000);
				System.out.println("Sent message: " + customer.toString());
			}

		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private String retrieveSchema(String schemaName, String schemaVersion) throws SchemaNotFoundException {

		Map<String, Object> config = new HashMap<>();
		SchemaVersionInfo schemaVersionInfo = null;

		for (final String name : props.stringPropertyNames())
			config.put(name, props.getProperty(name));
		
		try (SchemaRegistryClient client = new SchemaRegistryClient(config)) {
			if (null == schemaVersion) {
				// Use the latest version of the schema
				schemaVersionInfo = client.getLatestSchemaVersionInfo(schemaName);
			}
			else {
				for (SchemaVersionInfo sv : client.getAllVersions(schemaName)) {
					if (sv.getVersion() == Integer.getInteger(schemaVersion)) {
						schemaVersionInfo = sv;
					}
				};
					
				if (null == schemaVersionInfo) {
					String message = String.format("Could not find version %d of schema %s%nCheck the properties file %s%n", 
							schemaVersion, schemaName,
							this.propsFile); 
					throw new SchemaNotFoundException(message);
				}
			}
			System.out.printf("Using internal version [%d] of schema [%s]\nHere's the schema:\n%s\n",
					schemaVersionInfo.getVersion(),
					schemaVersionInfo.getName(),
					schemaVersionInfo.getSchemaText());
			return schemaVersionInfo.getSchemaText();
		} catch (SchemaNotFoundException e) {
			logger.error(e);
			throw e;
		}
	}
}
