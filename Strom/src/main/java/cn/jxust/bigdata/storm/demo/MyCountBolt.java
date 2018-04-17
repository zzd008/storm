package cn.jxust.bigdata.storm.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class MyCountBolt extends BaseRichBolt {
	OutputCollector collector;
	Map<String,List<String>> map;
	
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector=collector;
		map=new HashMap<String,List<String>>();
	}

	public void execute(Tuple input) {
		String one=input.getString(0);
		String two=input.getString(1);
//		System.out.println(one+" "+two);
		List<String> list=new ArrayList<>();
		
		if(map.containsKey(one)){
			list=map.get(one);
			list.add(two);
			map.put(one, list);
		}else{
			list.add(two);
			map.put(one, list);
		}
		System.out.println(Thread.currentThread().getName()+"  "+map.size());
		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}


}
