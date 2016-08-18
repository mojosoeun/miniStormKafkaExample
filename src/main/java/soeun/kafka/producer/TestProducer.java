package soeun.kafka.producer;
/**
 * Created by writtic on 2016. 8. 15..
 */

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class TestProducer {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        ProducerRecord<String, String> message = new ProducerRecord<String, String>("test", "Hello, World!");
        producer.send(message);
        producer.close();
    }
}