# Lab 1

- Use the command-line interface to create and manage Kafka topics 
- Publish messages and read messages from the command line

### 1. Creating topics

``` 
kafka-topics --create \
  --bootstrap-server edge2ai-0.dim.local:9092
  --replication-factor 1 \
  --partitions 5 \
  --topic device_status
  ```
 
 ### 2. List topics

``` 
kafka-topics --list \
  --bootstrap-server edge2ai-0.dim.local:9092
  ```
  
