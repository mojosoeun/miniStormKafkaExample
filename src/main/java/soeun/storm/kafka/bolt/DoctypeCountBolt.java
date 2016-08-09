package enow.storm.kafka.bolt;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class DoctypeCountBolt extends BaseBasicBolt {
	Map<String,Integer> docMap = new HashMap<String,Integer>();

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

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("docmap"));
	}
}
