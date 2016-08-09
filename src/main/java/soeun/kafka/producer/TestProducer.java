package enow.storm.kafka.topology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import enow.storm.kafka.bolt.ClassifyKeyBolt;
import enow.storm.kafka.bolt.CutLogBolt;
import enow.storm.kafka.bolt.DoctypeCountBolt;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class StormKafakaSimpleTopology {

	public static void main(String[] args) throws Exception {

		String zkUrl = "localhost:2181"; // zookeeper url
		String brokerUrl = "localhost:9092";
		String inputJar = "/usr/local/Cellar/storm/1.0.1/libexec/lib/storm-core-1.0.1.jar";
		if (args.length > 2 || (args.length == 1 && args[0].matches("^-h|--help$"))) {
			System.out.println("Usage: ENOW [kafka zookeeper url] [kafka broker url]");
			System.out.println("   E.g ENOW [" + zkUrl + "]" + " [" + brokerUrl + "]");
			System.exit(1);
		} else if (args.length == 1) {
			zkUrl = args[0];
		} else if (args.length == 2) {
			zkUrl = args[0];
			brokerUrl = args[1];
		} else if (args.length == 3) {
			zkUrl = args[0];
			brokerUrl = args[1];
			inputJar = args[2];
		}

		System.out.println("Using Kafka zookeeper url: " + zkUrl + " broker url: " + brokerUrl);

		ZkHosts hosts = new ZkHosts(zkUrl);
		SpoutConfig spoutConfig = new SpoutConfig(hosts, "onlytest", "/onlytest", UUID.randomUUID().toString());
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout", kafkaSpout, 1);
		builder.setBolt("cutbolt", new CutLogBolt(), 1).shuffleGrouping("spout");
		builder.setBolt("classifybolt", new ClassifyKeyBolt(), 8).fieldsGrouping("cutbolt",
				new Fields("key", "doctype"));
		builder.setBolt("docbolt", new DoctypeCountBolt(), 8).fieldsGrouping("classifybolt", new Fields("subdoctype"));

		Config conf = new Config();
		conf.setDebug(true);
		List<String> nimbus_seeds = new ArrayList<String>();
		nimbus_seeds.add("localhost"); // nimbus url

		if (args != null && args.length > 0) {
			conf.setNumWorkers(3);

			StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
		} else {

			// =============================
			// local mode
			// =============================
			// LocalCluster cluster = new LocalCluster();
			// cluster.submitTopology("log-stat", conf,
			// builder.createTopology());
			// Thread.sleep(10000);
			// cluster.shutdown();

			// =============================
			// cluster mode
			// =============================
			//conf.put(Config.NIMBUS_HOST, "localhost");
			conf.put(Config.STORM_LOCAL_DIR, "/usr/local/Cellar/storm/1.0.1");
			conf.put(Config.NIMBUS_THRIFT_PORT, 6627);
			conf.put(Config.STORM_ZOOKEEPER_PORT, 2181);
			conf.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(new String[] { "localhost" })); // zookeeper
																									// url
			// conf.setNumWorkers(20);
			// conf.setMaxSpoutPending(5000);
			// storm.jar를 종종 인식을 못하여 해당 Property를 추가해주어야한다.
			System.setProperty("storm.jar", inputJar);
			StormSubmitter.submitTopology("onlytest", conf, builder.createTopology());
		}
	}
}
