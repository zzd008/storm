package cn.jxust.bigdata.storm.kafka_strom;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;

/*
 * 从kafka中消费订单信息
 * 
 * flume采集数据到kafka中/订单系统生成mq到kafka
 * storm集成kafka，去kafka中消费数据
 */
public class KafkaAndStormTopologyMain {

	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		TopologyBuilder builder = new TopologyBuilder();
		
		//当storm从kafka中读取某个topic的消息时，需要知道这个topic有多少个分区，以及这些分区放在哪个kafka节点(broker)上，ZkHosts就是用于这个功能,通过zk来获取分区等信息
		BrokerHosts zkHosts = new ZkHosts("master:2181,slave1:2181,slave2:2181");//构造参数中默认是从/brokers下去获取主题及分区信息
//		BrokerHosts zkHosts = new ZkHosts("master:2181,slave1:2181,slave2:2181","/brokers");//构造参数中默认是从/brokers下去获取主题及分区信息
		
		//使用jar包中kafkaspout类  构造参数中需要一个SpoutConfig，spoutconfig中配置broker的地址，因为是消费者，即配置zk地址，  topic，  zk用于记录offset的目录  消费者标识id
		//会根据spout设置的并发度，负载均衡去消费kafka中的线程，最好是一个并发度消费一个分区
		//kafkaspout使用了ackfail机制，会将数据缓存，失败了会重发，成功后会会去zk的/myspout下更新offset值，标识成功消费，
		//所以要在bolt中调用一下ack方法，不然ackfail机制并未实现，zk上不会出现你指定的/myspout目录！！而且kafkaspout会一直重发你从kafka拿来的消息
		
		//!!!!要在集群中运行代码，zk上才有/myspout目录。。。。 本地运行zk上不会出现/myspout!! zk上不记录。那么每次都是从最新的开始消费
		builder.setSpout("order_kafkaspout", new KafkaSpout(new SpoutConfig(zkHosts, "orderMq", "/myspout", "kafkaspout")),1);
		builder.setBolt("ParserOrderBolt", new ParserOrderBolt(),1).shuffleGrouping("order_kafkaspout");
		
		Config config = new Config();
		config.setNumWorkers(1);
		
//		StormSubmitter.submitTopology("kafka_storm", config, builder.createTopology());
		
		LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("kafka_storm", config, builder.createTopology());
		
	}
}
