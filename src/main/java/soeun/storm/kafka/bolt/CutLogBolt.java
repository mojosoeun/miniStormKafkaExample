package soeun.storm.kafka.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class CutLogBolt extends BaseBasicBolt{

	public void execute(Tuple input, BasicOutputCollector collector) {
		String[] splitArray = input.getString(0).split(";");
		String key = "";
		String doctype = "";
		for(int i = 0; i < splitArray.length; i++){
			if(splitArray[i].contains("key"))
				key  = splitArray[i];
			if(splitArray[i].contains("doctype"))
				doctype = splitArray[i];
		}
		collector.emit(new Values(key,doctype));
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("key","doctype"));
	}

}
