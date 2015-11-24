package soeun.storm.kafka.bolt;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class DoctypeCountBolt extends BaseBasicBolt {
	Map<String,Integer> docMap = new HashMap<String,Integer>();
	
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String doctype = input.getStringByField("subdoctype");
		
		Integer count = docMap.get(doctype);
		if(count == null)
			count = 0;
		
		count++;
		
		docMap.put(doctype, count);
		System.out.println(docMap);
		collector.emit(new Values(docMap));
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("docmap"));
	}
}
