package enow.storm.kafka.bolt;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

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
		System.out.println(key);
		System.out.println(doctype);
		collector.emit(new Values(key,doctype));
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
//		declarer.declare(new Fields("key","doctype"));
	}
}
