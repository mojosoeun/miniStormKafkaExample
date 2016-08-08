package soeun.kafka.producer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import backtype.storm.utils.Time;

//import org.apache.kafka.clients.KeyedMessage;
//import kafka.javaapi.producer.Producer;
import org.apache.kafka.clients.producer.*;

public class TestProducer {

	private static final int SLEEP = 500;
	public KafkaProducer<String,String> producer;

	public void setConfig(){
		Properties properties = new Properties();
        properties.put("bootstrap.servers","localhost:9092");
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<>(properties);
	}

	public static void main(String[] args) throws InterruptedException{
        TestProducer testProducer = new TestProducer();
        testProducer.setConfig();
        testProducer.run();
//        producer.close();
    }
	private void run() throws InterruptedException {
		OnlyLogListenter onlyLogListenter = new OnlyLogListenter(producer);
        Tailer tailer = Tailer.create(new File("your file"), onlyLogListenter,SLEEP);
        while(true){
        	Thread.sleep(SLEEP);
        }
    }


	public class OnlyLogListenter extends TailerListenerAdapter{
		KafkaProducer<String,String> producer;
		public OnlyLogListenter(KafkaProducer<String,String> producer){
			this.producer = producer;
		}
		@Override
		public void handle(String line){
			 System.err.println(line);
			 ProducerRecord<String, String> message =new ProducerRecord<String, String>("onlytest","Hello World!");
		     producer.send(message);
		}
	}
}
