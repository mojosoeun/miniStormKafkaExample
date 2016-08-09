package enow.storm.kafka.bolt;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class ClassifyKeyBolt extends BaseBasicBolt{

	public void execute(Tuple input, BasicOutputCollector collector) {
		String[] splitdoctype = input.getStringByField("doctype").split(":");
		String[] splitkey = input.getStringByField("key").split(":");
		if(splitkey.length == 2 && splitdoctype.length == 2){
			String doctype  = splitdoctype[1].trim();
			String key  = splitkey[1].trim();
//			System.err.println(key + ":" + doctype); haha
			collector.emit(new Values(key + ":" + doctype));

		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("subdoctype"));
	}
}
