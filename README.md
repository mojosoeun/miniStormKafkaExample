miniStormKafkaExample
=====================

Hello Apache Storm Beginners! This Repo is for you! Follow the README.md instructions and learn Apache Storm quickly!

## Tutorial
- http://mojosoeun.tistory.com/119

Environment setup with [Docker](https://www.docker.io/)
------------------------------

If you are using a Mac follow the instructions [here](https://docs.docker.com/installation/mac/) to setup a docker environment.

- Install [docker-compose](http://docs.docker.com/compose/install/)

- Install [storm](https://storm.incubator.apache.org/downloads.html) (so you can upload your topology to the test cluster) but if you just would like to use remote cluster, you don't have to

- Build for running a Storm cluster
    - ```mvn clean package```
- Start the test environment
    - ```docker-compose -p storm up```
- Start a kafka shell and From within the shell, create a topic
    - ```start-kafka-shell.sh <Docker Ip> <Zookeeper>```
    - ```$KAFKA_HOME/bin/kafka-topics.sh --create --topic storm-sentence --partitions 2 --zookeeper $ZK --replication-factor 1```
- Or just add ```environment: KAFKA_CREATE_TOPICS: "test:3:1"``` in kafka of docker-compose(I already added)
- And when the storm ui is available create another window then Start the topology builder
    - ```docker-compose -p storm -f submitter.yml build```
    - ```docker-compose -p storm -f submitter.yml up```

For more details and troubleshooting see: <br/> [https://github.com/enow/kafka-docker](https://github.com/enow/kafka-docker) </br>
and </br> [https://github.com/enow/storm-docker](https://github.com/enow/storm-docker)

The Storm UI will be available under: ```http://<dockerIp>:8080/```

The Logviewer will be available under: ```http://<dockerIp>:8000/``` <br/>e.g. ```http://<dockerIp>:8000/log?file=supervisor.log```

## Producing data

To feed the topologies with data, start the StormProducer (built in local mode)

- ```java -cp target/enow-storm-1.0.jar com.enow.storm.tools.StormProducer <dockerIp>:<kafkaPort>```

Alternatively use the kafka console producer from within the kafka shell (see above)

- ```$KAFKA_HOME/bin/kafka-console-producer.sh --topic=storm-sentence --broker-list=<dockerIp>:<kafkaPort>```

## Consuming data

To run a DRPC query, start the DrpcClient (built in local mode)

- ```java -cp target/enow-storm-1.0.jar com.enow.storm.tools.DrpcClient <dockerIp> 3772```

## Apache Kafka, Zookeeper and Storm Installation
Below two Repositories and Tutorials help you install powerful open source distributed realtime computation system.

- [https://github.com/Writtic/docker-storm](https://github.com/Writtic/docker-storm)
- [https://github.com/Writtic/docker-kafka](https://github.com/Writtic/docker-kafka)

## To do list

In the latest version, the class packages have been changed from ```backtype.storm``` to ```org.apache.storm``` and Storm can not properly support ```Apache-Kafka-0.10.0.0``` or __higher version of Kafka__.

These are the new features you should expect in the coming
months:

* [x] Support ```org.apache.storm``` class packages instead of ```backtype.storm```
* [x] Support easy installation of ```Apache Kafka```<sub>0.9.0.1</sub> ```Apache Zookeeper```<sub>3.4.8</sub> and ```Apache Storm```<sub>1.0.2</sub> via ```Docker```
* [x] Support ```Storm-Kafka-Client```<sub>0.10.0.0</sub> or ```Storm-Kafka```<sub>0.9.0.1</sub> for Kafka Integration.
* [ ] Correct Bolt errors
* [ ] Support ```Trident Topology``` for powerful batch processing.
* [ ] Add more Integrations.


# Build And Run Bundled Examples  
To be able to run the examples you must first build the java code in the package `storm-kafka-client`,
and then generate an uber jar with all the dependencies.

## Use the Maven Shade Plugin to Build the Uber Jar

Add the following to `REPO_HOME/storm/external/storm-kafka-client/pom.xml`
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>2.4.1</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>org.apache.storm.kafka.spout.test.KafkaSpoutTopologyMain</mainClass>
                    </transformer>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```

create the uber jar by running the commmand:

`mvn package -f REPO_HOME/storm/external/storm-kafka-client/pom.xml`

This will create the uber jar file with the name and location matching the following pattern:

`REPO_HOME/storm/external/storm-kafka-client/target/storm-kafka-client-1.0.x.jar`

### Run Storm Topology

Copy the file `REPO_HOME/storm/external/storm-kafka-client/target/storm-kafka-client-1.0.x.jar` to `STORM_HOME/extlib`

Using the Kafka command line tools create three topics [test, test1, test2] and use the Kafka console producer to populate the topics with some data

Execute the command `STORM_HOME/bin/storm jar REPO_HOME/storm/external/storm/target/storm-kafka-client-1.0.x.jar org.apache.storm.kafka.spout.test.KafkaSpoutTopologyMain`

With the debug level logs enabled it is possible to see the messages of each topic being redirected to the appropriate Bolt as defined
by the streams defined and choice of shuffle grouping.   

## Using storm-kafka-client with different versions of kafka

Storm-kafka-client's Kafka dependency is defined as `provided` scope in maven, meaning it will not be pulled in
as a transitive dependency. This allows you to use a version of Kafka dependency compatible with your kafka cluster.

When building a project with storm-kafka-client, you must explicitly add the Kafka clients dependency. For example, to
use Kafka-clients 0.10.0.0, you would use the following dependency in your `pom.xml`:

```xml
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>0.10.0.0</version>
        </dependency>
```

You can also override the kafka clients version while building from maven, with parameter `storm.kafka.client.version`
e.g. `mvn clean install -Dstorm.kafka.client.version=0.10.0.0`

When selecting a kafka client version, you should ensure -
 1. kafka api is compatible. storm-kafka-client module only supports **0.10 or newer** kafka client API. For older versions,
 you can use storm-kafka module (https://github.com/apache/storm/tree/master/external/storm-kafka).  
 2. The kafka client selected by you should be wire compatible with the broker. e.g. 0.9.x client will not work with
 0.8.x broker.
