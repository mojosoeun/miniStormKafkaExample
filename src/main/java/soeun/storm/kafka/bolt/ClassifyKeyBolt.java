package soeun.storm.kafka.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class ClassifyKeyBolt extends BaseBasicBolt{

	public void execute(Tuple input, BasicOutputCollector collector) {
		String[] splitdoctype = input.getStringByField("doctype").split(":");
		String[] splitkey = input.getStringByField("key").split(":");
		if(splitkey.length == 2 && splitdoctype.length == 2){
			String doctype  = splitdoctype[1].trim();
			String key  = splitkey[1].trim();
//			System.err.println(key + ":" + doctype);
			collector.emit(new Values(key + ":" + doctype));

		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("subdoctype"));
	}
}
