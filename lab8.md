# Lab 8

- Implementing Consumer Groups 

Open two terminals and cd to this exercise directory:

```
cd /tmp/resources/exercise-code/consumer-groups
```
Compile / package the code. Note you will need to issue this command if any code is changed

```
mvn clean package
```

### Set HOSTNAME

```
export HOSTNAME=localhost
```

### 1. Recreate the topic


``` 
kafka-topics --delete \
  --bootstrap-server edge2ai-0.dim.local:9092 \
  --topic customers
```

``` 
kafka-topics --create \
  --bootstrap-server edge2ai-0.dim.local:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic customers \
  --config retention.bytes=1073742000 \
  --config segment.bytes=1073742000
  ```

### 2. Start the Producer

In the second terminal tab/window, run the following command to produce `1000` message to `customers`topic:

``` 
mvn exec:java \
    -Dexec.mainClass="com.cloudera.training.kafka.solution.SimpleProducer" \
    -Dexec.args="edge2ai-0.dim.local:9092 customers 1000"
``` 
 
### 3. Start the consumer

In one terminal window, start one consumer using the command below. The last value in the arguments (a) is the consumer group ID, 
which we're passing in from the command line instead of hardcoding as we did in earlier exercises.


``` 
mvn exec:java \
    -Dexec.mainClass="com.cloudera.training.kafka.solution.SimpleConsumer" \
    -Dexec.args="edge2ai-0.dim.local:9092 customers a"
```

In the other terminal run a second consumer, this time using a different value (b) for the consumer group ID.

``` 
mvn exec:java \
    -Dexec.mainClass="com.cloudera.training.kafka.solution.SimpleConsumer" \
    -Dexec.args="edge2ai-0.dim.local:9092 customers b"
```

Once the consumers start running, switch back and forth between the two terminals where they're running. 
You should find that they both receive the same records. In other words, because they have two different values for 
the consumer group ID, they are operating as two unrelated applications that each receive every message on the topic.


### 4. Use Streams Messaging Manager (SMM) to View Groups' Information

Use the "Consumer Groups" section of SMM to view information about Consumer Groups A and B.

- Which topic(s) they're reading from
- How many consumer clients are in each group?
- Do the clients have any lag?
- Which host(s) the clients are running on?


### 5. Run Both Consumers in a Single Consumer Group

Kill the consumers by hitting Ctrl-C in both terminal windows. 
Afterwards, re-run the commands in each window, but change the consumer group ID in both cases to a.

``` 
mvn exec:java \
    -Dexec.mainClass="com.cloudera.training.kafka.solution.SimpleConsumer" \
    -Dexec.args="edge2ai-0.dim.local:9092 customers a"
```

In the other terminal run a second consumer, this time using a different value (b) for the consumer group ID.

``` 
mvn exec:java \
    -Dexec.mainClass="com.cloudera.training.kafka.solution.SimpleConsumer" \
    -Dexec.args="edge2ai-0.dim.local:9092 customers a"
```

You should find that each message goes to one consumer or the other; you should not see any given message going to both of them.


### 6. RObserve the offsets being read by consumers

Find the group.id for this consumer group
Use the `kafka-consumer-groups` command to monitor the group's status in a new tab:

```
kafka-consumer-groups \
  --bootstrap-server edge2ai-0.dim.local:9092 \
  --describe \
  --group your-consumer-group-id
````
The output should resemble the following:

```
TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG CONSUMER-ID                                     HOST            CLIENT-ID
newcustomers    0          22              22              0   consumer-1-53c2780b-faa9-4748-b739-dd54d4f0f9a0 /10.0.0.106     consumer-1
newcustomers    1          21              21              0   consumer-1-53c2780b-faa9-4748-b739-dd54d4f0f9a0 /10.0.0.106     consumer-1
newcustomers    2          20              21              1   consumer-1-db1cba97-c2d8-4bcc-a930-5b6f6f31e423 /10.0.0.106     consumer-1
```

