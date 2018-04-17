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
		//1.׼��һ��TopologyBuilder
		TopologyBuilder topologyBuilder = new TopologyBuilder();
		//����topology��spout��id���࣬���жȼ�spout��task����������exectorȥִ�� ��Щtask�ᱻ��Ⱥ���䵽��ͬ�ڵ�Ĳ�ͬworker��ȥִ��
		topologyBuilder.setSpout("mySpout", new MySpout(), 2);
		//����bolt��id���ࡢ���ж� �Լ�spout�ķַ�����
		//���԰Ѳ�����ͨ��args����������������д�������ļ��У����������޸Ķ�����ȥ�Ĵ��� 
		topologyBuilder.setBolt("myBolt1", new MySplitBolt(), 4).shuffleGrouping("mySpout");//Random.nextInt(num)
		topologyBuilder.setBolt("myBolt2", new MyCountBolt(), 4).fieldsGrouping("myBolt1", new Fields("word"));//�����ֶηַ����ݣ��ֶ���Ϊword hashcode(word)%tasknumnum/threadnum ��ͬ���ʻᷢ�͵�ͬһ��taskȥִ��
//		topologyBuilder.setBolt("myBolt2", new MyCountBolt(), 4).shuffleGrouping("myBolt1");//������������ô�ͻᰴ��//Random.nextInt(tasknum/threadnum) ÿ���̴߳���ĵ��ʶ��������
		 /*
		  * һ��Ҫָ��fieldsGrouping������ÿ�����ʹ̶���ĳ��worker�µ�ĳ��task������study���ʵ�ͳ�ƽ������worker-6702.log love���ʵ�ͳ�ƽ������worker-6703.log�� ����������  Ȼ����Խ���Щ�ļ�����ϲ���ָ��MyCountBolt���ж�Ϊ1����
		  * �����shuffleGrouping ��ôÿ��task����ĵ��ʲ��̶�����ôÿ��task��ͳ�ƽ�����ܶ�������ͬ�ĵ��� 
		  */
		
		//2.����һ��config����ָ��topology��Ҫ��worker����
		Config config=new Config();
//		config.setDebug(true);
		config.setNumWorkers(2);
		config.setNumAckers(1);//Ҫ����acker���ƣ�Ҫ����acker�߳�����������0
		
		
		//3.�ύ����  ---����ģʽ������ģʽ�ͼ�Ⱥģʽ
		//��Ⱥ�ύģʽ  Ҫ���ύ����Ⱥ�ϣ�Ҫ��StormSubmitter
//		StormSubmitter.submitTopology("mywordcount", config,topologyBuilder.createTopology());//ָ��topology������  config����  topology����
		
		//�ύ����������  Ҳ�����ڼ�Ⱥ��ʹ��LocalCluster�����������ύ����Ⱥ����web���濴�������ͻ����eclipse������һ��
		LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("mywordcount", config,topologyBuilder.createTopology());

//		Thread.sleep(5000); //�����һֱ���У���������˯5sȻ��ص�
//		localCluster.killTopology("mywordcount");
//		localCluster.shutdown();
		
	}
}
