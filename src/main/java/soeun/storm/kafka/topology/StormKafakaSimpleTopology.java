package soeun.storm.kafka.topology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import soeun.storm.kafka.bolt.ClassifyKeyBolt;
import soeun.storm.kafka.bolt.CutLogBolt;
import soeun.storm.kafka.bolt.DoctypeCountBolt;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class StormKafakaSimpleTopology {
   
    public static void main(String[] args) throws Exception {

        String zkUrl = "zookeeper url:2181";        // zookeeper url 
        String brokerUrl = "localhost:9092";

        if (args.length > 2 || (args.length == 1 && args[0].matches("^-h|--help$"))) {
            System.out.println("Usage: TridentKafkaWordCount [kafka zookeeper url] [kafka broker url]");
            System.out.println("   E.g TridentKafkaWordCount [" + zkUrl + "]" + " [" + brokerUrl + "]");
            System.exit(1);
        } else if (args.length == 1) {
            zkUrl = args[0];
        } else if (args.length == 2) {
            zkUrl = args[0];
            brokerUrl = args[1];
        }

        System.out.println("Using Kafka zookeeper url: " + zkUrl + " broker url: " + brokerUrl);

        ZkHosts hosts = new ZkHosts(zkUrl);
        SpoutConfig spoutConfig = new SpoutConfig(hosts, "onlytest", "/onlytest", UUID.randomUUID().toString());
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
        TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout", kafkaSpout, 1);
		builder.setBolt("cutbolt", new CutLogBolt(), 8).shuffleGrouping("spout");
		builder.setBolt("classifybolt", new ClassifyKeyBolt(), 8).fieldsGrouping("cutbolt",new Fields("key","doctype"));
		builder.setBolt("docbolt", new DoctypeCountBolt(), 8).fieldsGrouping("classifybolt",new Fields("subdoctype"));
		
		Config conf = new Config();
		conf.setDebug(true);
		List<String> nimbus_seeds = new ArrayList<String>();
		nimbus_seeds.add("nimbus url");

		if (args != null && args.length > 0) {
			conf.setNumWorkers(3);

			StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
		}
		else {

			//=============================
			//	local mode
			//=============================
//			LocalCluster cluster = new LocalCluster();
//			cluster.submitTopology("log-stat", conf, builder.createTopology());
//			Thread.sleep(10000);
//			cluster.shutdown();
			
			//=============================
			//	cluster mode
			//=============================
			conf.put(Config.NIMBUS_HOST, "nimbus url");
			conf.put(Config.STORM_LOCAL_DIR,"your storm local dir");
			conf.put(Config.NIMBUS_THRIFT_PORT,6627);
			conf.put(Config.STORM_ZOOKEEPER_PORT,2181);
			conf.put(Config.STORM_ZOOKEEPER_SERVERS,Arrays.asList(new String[] {"zookeeper url"}));
//			conf.setNumWorkers(20);
//			conf.setMaxSpoutPending(5000);
			StormSubmitter.submitTopology("onlytest", conf, builder.createTopology());

		}
	}
 
}
