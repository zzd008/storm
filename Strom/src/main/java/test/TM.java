package test;

import org.apache.storm.http.conn.scheme.Scheme;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.MultiScheme;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

/*
 * 驱动类
 */
public class TM {
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		TopologyBuilder builder = new TopologyBuilder();
		

		SpoutConfig spoutConfig=new SpoutConfig(new ZkHosts("master:2181,slave1:2181,slave2:2181"), "logmonitor", "/kafka", "k1");
		builder.setSpout("logmonitor_kfkspout", new KafkaSpout(spoutConfig),3);
		builder.setBolt("t_bolt", new TBolt(),4).shuffleGrouping("logmonitor_kfkspout");//kfkspout不能指定按字段分组
		
		Config config = new Config();
		config.setNumWorkers(3);
		
		if(args!=null&&args.length>0){
			StormSubmitter.submitTopologyWithProgressBar(args[0], config, builder.createTopology());//集群运行 传入的是topology的名字
		}else{
			
			/*LocalCluster localCluster = new LocalCluster();
			localCluster.submitTopology("l1", config, builder.createTopology());//本地运行
*/			
			StormSubmitter.submitTopologyWithProgressBar("tm1", config, builder.createTopology());
		}
		
		
	}
}
