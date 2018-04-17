package cn.jxust.bigdata.storm.demo;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class WordCountTopologyMain {
	
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, InterruptedException {
		TopologyBuilder topologyBuilder = new TopologyBuilder();
		topologyBuilder.setSpout("mySpout", new MySpout(), 1);
		topologyBuilder.setBolt("myBolt1", new MyCountBolt(),1 ).fieldsGrouping("mySpout", new Fields("one"));
		Config config=new Config();
		config.setNumWorkers(1);
		
		LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("aa", config,topologyBuilder.createTopology());
		
	}
}
