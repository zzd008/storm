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
	
	//初始化方法  该方法每个线程只调用一次 由storm框架传入SpoutOutputCollector
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
//		System.out.println(Thread.currentThread().getId()+"   创建collector");//
		
		this.collector=collector;
	}

	//业务逻辑方法 被storm框架 while(true) 循环调用  传入参数为上一个bolt或者spout传过来的tuple
	public void execute(Tuple input) {
		String word=input.getString(0);
//		System.out.println(Thread.currentThread().getId()+"   "+word);//fieldsGrouping 如果指定mysplitspout按照字段分组，那么会根据hashcode(word)%tasknum计算 相同的单词分由同一个task即同一个线程去执行
		/*
		 * 如111 线程只处理am zzd
		 * 108  love
		 * 102 i
		 * 103 study
		 */
		//如果是随机分组shufflegrouping，那么就会按照//Random.nextInt(tasknum/threadnum) 每个线程处理的单词都是随机的  如106 am i zzd 106 love 
		
		//map中放一个值map就变了，它的hashcode也会随之改变
		//每个线程去执行task，都会new一个MyCountBolt对象，然后调用一次prepare方法（只调用一次），然后new一个map，然后不断循环调用execute来往map中统计单词次数
//		System.out.println(Thread.currentThread().getId()+"   "+map.hashCode()); 
		
		//*************多个线程(task)之间绝对不是共享同一个map的，如果是共享的，那么这个map会包含所有的word，而不是每个线程对应自己分区得到的word*******
		//如果要是共享变量的话，要么就是每个线程把map拿到自己的工作内存后一直没有刷新回主存，导致其他线程是不可见的，但是加上volatile也没用啊,不过不可能不刷回主存，假设失败。。。。。。所以map是每个线程独有的

		
		int num=input.getInteger(1);
//		int num=input.getIntegerByField("num");
		
		if(map.containsKey(word)){
			int count=map.get(word);
			map.put(word, count+num);
		}else{
			map.put(word, num);
		}
		//不往下发射了，打印到控制台
//		collector.ack(input);
		System.out.println(map);
		
		
		/*
		 * 这里调用Test中的静态变量a，因为静态变量在内存中只有一个，是多线程（多）共享的，所以是多个线程对它操作，结果是累加的
		 * 但是如果这里Test t=new Test()；t.a++; syso(t.geta()); 这样因为每个线程new了一个对象，是自己独有的。
		 * 所以，如果是多线程操作外部静态变量，是多线程/多task共享的，如果是bolt里new的对象，就像(static) Map<String,Integer> map=new HashMap<String, Integer>();那么是每个task独有的！！
		 * ！！！
		 * 总结：如果task被分在不同的物理机的worker上，因为是不同的jvm上，所以task之间是独立的，互不相关，即使是execute操作其他类的静态成员，也不会共享。代码会被打成jar分发到不同jvm去执行，自己执行自己的。
		 * 但是如果是两个task被分到了同一个物理机的jvm上，只有操作其他类的静态成员时，如Test类的a，这时a才是线程间共享的。  不然都是task独有的，每个task有自己的map 如wordcount
		 * 因为多线程共享变量是不安全的，所以岁共享变量进行操作要要加锁！！
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
