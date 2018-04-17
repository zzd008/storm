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
 *自定义bolt 最原始的顶级接口为IRichBolt
 */
//BaseRichBolt 需要手动调ack方法，BaseBasicBolt由storm框架自动调ack方法
public class MySplitBolt extends BaseRichBolt{
	OutputCollector collector;//输出收集器，用于收集并发送tuple
	
	//初始化方法  该方法每个线程只调用一次 由storm框架传入SpoutOutputCollector
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector=collector;//初始化收集器
	}

	//业务逻辑方法 被storm框架 while(true) 循环调用  传入参数为上一个bolt或者spout传过来的tuple
	public void execute(Tuple input) {
		String line=input.getString(0);//tuple为一个list，这里获取tuple中的第一个值:(String) values.get(i);
		String[] words = line.split(" ");
		for(String word:words){
			collector.emit(new Values(word,1));//values是一个list
//			int i=1/0;
			collector.ack(input);//告诉storm，自己对这个tuple处理完成，给一个反馈，spout那边就可以确认tuple已经成功发送
		}
		
		//前一个spout指定了两个字段名love和aaa 分别对应spout中发射的tuple中的第一个元素"i am zzd love study"和"aaavalue" 这里获取aaa在fields中的下标，然后根据这个下标1，然后获取tuple中的第1个元素（下标从0开始）
//		String line1=input.getStringByField("aaa");
	}

	//声明输出的tuple字段
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word","num"));//分别于与tuple中的word，1对应
	}

	
}
