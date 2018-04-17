package cn.jxust.bigdata.storm.wordcount;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
/*
 * �Զ���spout spout�Ķ����ӿ�ΪIRichSpout
 */
public class MySpout  extends BaseRichSpout{

	SpoutOutputCollector collector;//����ռ����������ռ�������tuple
	
	Map<String,Values> buffer=new HashMap<String,Values>();//���ڴ�ŷ����tuple�����Ƿ�ɹ����䴦��
	
	//��ʼ������  �÷���ÿ���߳�ֻ����һ�� ��storm��ܴ���SpoutOutputCollector
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector=collector;//��ʼ��collector
		 
		
	}

	//storm��ܻ�while(true) һֱ���ø÷���������tuple tuple���൱��scala�е�Ԫ�飬��ͬԪ�صļ��� tuple�����װ��һ��list
	public void nextTuple() {
//		collector.emit(new Values("i am zzd love study"));//����tuple��������list Values�̳���ArrayList
//		collector.emit(new Values("i am zzd love study","aaavalue"));//values�п��Էźܶ�object���������object����ÿһ���ַ������ӵ�������
		
		Values values=new Values("i am zzd love study");
		String messageID=UUID.randomUUID().toString().replaceAll("-", "");
		buffer.put(messageID, values);//���浽hashmap��
		collector.emit(values,messageID);//1����Ҫackfailʱ����Ϊÿ��tuple����һ��messageid�����messageid��������ʶ����ĵ�tuple �����ָ��messageid�򲻿�ʼack����  ����ack���Ƽ��ĵ�
	}

	//���������tuple��  ������Դ���Է�������Ϣ��stream������������ͬ���͵����������ͣ�
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("love")); //Fields��һ��list
//		declarer.declare(new Fields("love","aaa"));//������ֶ�����tuple�е�Ԫ�����Ӧ��love��Ӧ"i am zzd love study"  aaa��Ӧ"aaavalue"
	}

	/*
	 * �����tuple����ȫ����󣨽������tuple��bolt ����collector.ack��tuple���󣩣�storm�����ack�������������fail������������Ϣ�Ƿ��ط��������Լ���������
	 */
	@Override
	public void ack(Object msgId) {
		System.out.println("tuple�Ѿ��ɹ�����"+msgId);//���msgId�Ƿ���ʱΪtupleָ����messageID
		//�ɹ����;��Ƴ������tuple
		buffer.remove(msgId);
	}
	
	@Override
	public void fail(Object msgId) {
		//����ʧ�����·���
		System.out.println("����ʧ�ܣ�");
		Values values = buffer.get(msgId);//�ȸ���tuple��uuid��ȡ��values
		collector.emit(values,msgId);//���·���
		
	}
	
	@Test
	public void test(){
		System.out.println("i".hashCode()%4);
		System.out.println("am".hashCode()%4);
		System.out.println("zzd".hashCode()%4);
		System.out.println("love".hashCode()%4);
		System.out.println("study".hashCode()%4);
	}
	
}
