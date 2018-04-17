package cn.jxust.bigdata.storm.wordcount;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class WordCountTopologyMain {
	
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, InterruptedException {
		//1.准备一个TopologyBuilder
		TopologyBuilder topologyBuilder = new TopologyBuilder();
		//设置topology的spout的id，类，并行度即spout的task数量，几个exector去执行 这些task会被集群分配到不同节点的不同worker下去执行
		topologyBuilder.setSpout("mySpout", new MySpout(), 2);
		//设置bolt的id、类、并行度 以及spout的分发策略
		//可以把并发度通过args参数传进来或者是写到配置文件中，这样方便修改而不用去改代码 
		topologyBuilder.setBolt("myBolt1", new MySplitBolt(), 4).shuffleGrouping("mySpout");//Random.nextInt(num)
		topologyBuilder.setBolt("myBolt2", new MyCountBolt(), 4).fieldsGrouping("myBolt1", new Fields("word"));//按照字段分发数据，字段名为word hashcode(word)%tasknumnum/threadnum 相同单词会发送到同一个task去执行
//		topologyBuilder.setBolt("myBolt2", new MyCountBolt(), 4).shuffleGrouping("myBolt1");//如果是随机，那么就会按照//Random.nextInt(tasknum/threadnum) 每个线程处理的单词都是随机的
		 /*
		  * 一定要指定fieldsGrouping，这样每个单词固定被某个worker下的某个task处理，如study单词的统计结果都在worker-6702.log love单词的统计结果都在worker-6703.log下 如果是有序的  然后可以将这些文件结果合并，指定MyCountBolt并行度为1即可
		  * 如果是shuffleGrouping 那么每个task处理的单词不固定，那么每个task的统计结果可能都包含不同的单词 
		  */
		
		//2.创建一个config，来指定topology需要的worker数量
		Config config=new Config();
//		config.setDebug(true);
		config.setNumWorkers(2);
		config.setNumAckers(1);//要开启acker机制，要设置acker线程数量数大于0
		
		
		//3.提交任务  ---两种模式：本地模式和集群模式
		//集群提交模式  要想提交到集群上，要用StormSubmitter
//		StormSubmitter.submitTopology("mywordcount", config,topologyBuilder.createTopology());//指定topology任务名  config对象  topology对象
		
		//提交到本地运行  也可以在集群上使用LocalCluster，这样不会提交到集群，在web界面看不到，就会和在eclipse上运行一样
		LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("mywordcount", config,topologyBuilder.createTopology());

//		Thread.sleep(5000); //程序会一直运行，这里让它睡5s然后关掉
//		localCluster.killTopology("mywordcount");
//		localCluster.shutdown();
		
	}
}
