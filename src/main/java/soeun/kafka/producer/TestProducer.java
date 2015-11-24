package soeun.kafka.producer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import backtype.storm.utils.Time;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class TestProducer {
	
	private static final int SLEEP = 500;
	public kafka.javaapi.producer.Producer<String,String> producer;
	
	public void setConfig(){
		Properties properties = new Properties();
        properties.put("metadata.broker.list","localhost:9092");
        properties.put("serializer.class","kafka.serializer.StringEncoder");
        ProducerConfig producerConfig = new ProducerConfig(properties);
        producer = new kafka.javaapi.producer.Producer<String, String>(producerConfig);
	}
	
	public static void main(String[] args) throws InterruptedException{
		       
        TestProducer testProducer = new TestProducer();
        testProducer.setConfig();
        testProducer.run();
//        producer.close();
    }
	private void run() throws InterruptedException {
		OnlyLogListenter onlyLogListenter = new OnlyLogListenter(producer);
        Tailer tailer = Tailer.create(new File("/home/soeun/workspace/onlysamplelog.log"), onlyLogListenter,SLEEP);
        while(true){
        	Thread.sleep(SLEEP);
        }
    }

	public class OnlyLogListenter extends TailerListenerAdapter{
		kafka.javaapi.producer.Producer<String,String> producer;
		public OnlyLogListenter(kafka.javaapi.producer.Producer<String,String> producer){
			this.producer = producer;
		}
		@Override
		public void handle(String line){
			 System.err.println(line);
			 KeyedMessage<String, String> message =new KeyedMessage<String, String>("onlytest",line);
		     producer.send(message);
		}
	}
}
