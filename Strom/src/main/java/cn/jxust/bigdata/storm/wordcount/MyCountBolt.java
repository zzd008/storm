package cn.jxust.bigdata.storm.wordcount;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class MyCountBolt extends BaseRichBolt {
	OutputCollector collector;
	Map<String,Integer> map=new HashMap<String, Integer>();
	
	//��ʼ������  �÷���ÿ���߳�ֻ����һ�� ��storm��ܴ���SpoutOutputCollector
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
//		System.out.println(Thread.currentThread().getId()+"   ����collector");//
		
		this.collector=collector;
	}

	//ҵ���߼����� ��storm��� while(true) ѭ������  �������Ϊ��һ��bolt����spout��������tuple
	public void execute(Tuple input) {
		String word=input.getString(0);
//		System.out.println(Thread.currentThread().getId()+"   "+word);//fieldsGrouping ���ָ��mysplitspout�����ֶη��飬��ô�����hashcode(word)%tasknum���� ��ͬ�ĵ��ʷ���ͬһ��task��ͬһ���߳�ȥִ��
		/*
		 * ��111 �߳�ֻ����am zzd
		 * 108  love
		 * 102 i
		 * 103 study
		 */
		//������������shufflegrouping����ô�ͻᰴ��//Random.nextInt(tasknum/threadnum) ÿ���̴߳���ĵ��ʶ��������  ��106 am i zzd 106 love 
		
		//map�з�һ��ֵmap�ͱ��ˣ�����hashcodeҲ����֮�ı�
		//ÿ���߳�ȥִ��task������newһ��MyCountBolt����Ȼ�����һ��prepare������ֻ����һ�Σ���Ȼ��newһ��map��Ȼ�󲻶�ѭ������execute����map��ͳ�Ƶ��ʴ���
//		System.out.println(Thread.currentThread().getId()+"   "+map.hashCode()); 
		
		//*************����߳�(task)֮����Բ��ǹ���ͬһ��map�ģ�����ǹ���ģ���ô���map��������е�word��������ÿ���̶߳�Ӧ�Լ������õ���word*******
		//���Ҫ�ǹ�������Ļ���Ҫô����ÿ���̰߳�map�õ��Լ��Ĺ����ڴ��һֱû��ˢ�»����棬���������߳��ǲ��ɼ��ģ����Ǽ���volatileҲû�ð�,���������ܲ�ˢ�����棬����ʧ�ܡ���������������map��ÿ���̶߳��е�

		
		int num=input.getInteger(1);
//		int num=input.getIntegerByField("num");
		
		if(map.containsKey(word)){
			int count=map.get(word);
			map.put(word, count+num);
		}else{
			map.put(word, num);
		}
		//�����·����ˣ���ӡ������̨
//		collector.ack(input);
		System.out.println(map);
		
		
		/*
		 * �������Test�еľ�̬����a����Ϊ��̬�������ڴ���ֻ��һ�����Ƕ��̣߳��ࣩ����ģ������Ƕ���̶߳���������������ۼӵ�
		 * �����������Test t=new Test()��t.a++; syso(t.geta()); ������Ϊÿ���߳�new��һ���������Լ����еġ�
		 * ���ԣ�����Ƕ��̲߳����ⲿ��̬�������Ƕ��߳�/��task����ģ������bolt��new�Ķ��󣬾���(static) Map<String,Integer> map=new HashMap<String, Integer>();��ô��ÿ��task���еģ���
		 * ������
		 * �ܽ᣺���task�����ڲ�ͬ���������worker�ϣ���Ϊ�ǲ�ͬ��jvm�ϣ�����task֮���Ƕ����ģ�������أ���ʹ��execute����������ľ�̬��Ա��Ҳ���Ṳ������ᱻ���jar�ַ�����ͬjvmȥִ�У��Լ�ִ���Լ��ġ�
		 * �������������task���ֵ���ͬһ���������jvm�ϣ�ֻ�в���������ľ�̬��Աʱ����Test���a����ʱa�����̼߳乲��ġ�  ��Ȼ����task���еģ�ÿ��task���Լ���map ��wordcount
		 * ��Ϊ���̹߳�������ǲ���ȫ�ģ������깲��������в���ҪҪ��������
		 */
		/*Test.a+=1; 
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getId()+"   "+Test.a);*/
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}


}
