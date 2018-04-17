package cn.jxust.bigdata.storm.wordcount;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/*
 *�Զ���bolt ��ԭʼ�Ķ����ӿ�ΪIRichBolt
 */
//BaseRichBolt ��Ҫ�ֶ���ack������BaseBasicBolt��storm����Զ���ack����
public class MySplitBolt extends BaseRichBolt{
	OutputCollector collector;//����ռ����������ռ�������tuple
	
	//��ʼ������  �÷���ÿ���߳�ֻ����һ�� ��storm��ܴ���SpoutOutputCollector
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector=collector;//��ʼ���ռ���
	}

	//ҵ���߼����� ��storm��� while(true) ѭ������  �������Ϊ��һ��bolt����spout��������tuple
	public void execute(Tuple input) {
		String line=input.getString(0);//tupleΪһ��list�������ȡtuple�еĵ�һ��ֵ:(String) values.get(i);
		String[] words = line.split(" ");
		for(String word:words){
			collector.emit(new Values(word,1));//values��һ��list
//			int i=1/0;
			collector.ack(input);//����storm���Լ������tuple������ɣ���һ��������spout�Ǳ߾Ϳ���ȷ��tuple�Ѿ��ɹ�����
		}
		
		//ǰһ��spoutָ���������ֶ���love��aaa �ֱ��Ӧspout�з����tuple�еĵ�һ��Ԫ��"i am zzd love study"��"aaavalue" �����ȡaaa��fields�е��±꣬Ȼ���������±�1��Ȼ���ȡtuple�еĵ�1��Ԫ�أ��±��0��ʼ��
//		String line1=input.getStringByField("aaa");
	}

	//���������tuple�ֶ�
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word","num"));//�ֱ�����tuple�е�word��1��Ӧ
	}

	
}
