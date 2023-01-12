# Lab 6

- Create a Hive talbe to consume Kafka topics
- Query the Hive Table
 
### 1. Open Hue 

Select the Hive query editor once in Hue

### 2. Create the table 

```
CREATE EXTERNAL TABLE customers 
   (`customer_id` int , `first_name` string,  `last_name` string,  `phone_number` string)
   STORED BY 'org.apache.hadoop.hive.kafka.KafkaStorageHandler'
   TBLPROPERTIES
   ("kafka.topic" = "customers", 
   "kafka.bootstrap.servers"="edge2ai-0.dim.local:9092",
   "kafka.serde.class"="org.apache.hadoop.hive.serde2.OpenCSVSerde");
```

Take a look to the see the CREATE statement.

### 3. Execute a SELECT query

```
SELECT * from customers
```
