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
 * ��kafka�����Ѷ�����Ϣ
 * 
 * flume�ɼ����ݵ�kafka��/����ϵͳ����mq��kafka
 * storm����kafka��ȥkafka����������
 */
public class KafkaAndStormTopologyMain {

	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		TopologyBuilder builder = new TopologyBuilder();
		
		//��storm��kafka�ж�ȡĳ��topic����Ϣʱ����Ҫ֪�����topic�ж��ٸ��������Լ���Щ���������ĸ�kafka�ڵ�(broker)�ϣ�ZkHosts���������������,ͨ��zk����ȡ��������Ϣ
		BrokerHosts zkHosts = new ZkHosts("master:2181,slave1:2181,slave2:2181");//���������Ĭ���Ǵ�/brokers��ȥ��ȡ���⼰������Ϣ
//		BrokerHosts zkHosts = new ZkHosts("master:2181,slave1:2181,slave2:2181","/brokers");//���������Ĭ���Ǵ�/brokers��ȥ��ȡ���⼰������Ϣ
		
		//ʹ��jar����kafkaspout��  �����������Ҫһ��SpoutConfig��spoutconfig������broker�ĵ�ַ����Ϊ�������ߣ�������zk��ַ��  topic��  zk���ڼ�¼offset��Ŀ¼  �����߱�ʶid
		//�����spout���õĲ����ȣ����ؾ���ȥ����kafka�е��̣߳������һ������������һ������
		//kafkaspoutʹ����ackfail���ƣ��Ὣ���ݻ��棬ʧ���˻��ط����ɹ�����ȥzk��/myspout�¸���offsetֵ����ʶ�ɹ����ѣ�
		//����Ҫ��bolt�е���һ��ack��������Ȼackfail���Ʋ�δʵ�֣�zk�ϲ��������ָ����/myspoutĿ¼��������kafkaspout��һֱ�ط����kafka��������Ϣ
		
		//!!!!Ҫ�ڼ�Ⱥ�����д��룬zk�ϲ���/myspoutĿ¼�������� ��������zk�ϲ������/myspout!! zk�ϲ���¼����ôÿ�ζ��Ǵ����µĿ�ʼ����
		builder.setSpout("order_kafkaspout", new KafkaSpout(new SpoutConfig(zkHosts, "orderMq", "/myspout", "kafkaspout")),1);
		builder.setBolt("ParserOrderBolt", new ParserOrderBolt(),1).shuffleGrouping("order_kafkaspout");
		
		Config config = new Config();
		config.setNumWorkers(1);
		
//		StormSubmitter.submitTopology("kafka_storm", config, builder.createTopology());
		
		LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("kafka_storm", config, builder.createTopology());
		
	}
}
