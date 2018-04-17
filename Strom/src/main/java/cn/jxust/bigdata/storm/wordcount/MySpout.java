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
 * 自定义spout spout的顶级接口为IRichSpout
 */
public class MySpout  extends BaseRichSpout{

	SpoutOutputCollector collector;//输出收集器，用于收集并发送tuple
	
	Map<String,Values> buffer=new HashMap<String,Values>();//用于存放发射的tuple，做是否成功发射处理
	
	//初始化方法  该方法每个线程只调用一次 由storm框架传入SpoutOutputCollector
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector=collector;//初始化collector
		 
		
	}

	//storm框架会while(true) 一直调用该方法来发射tuple tuple就相当于scala中的元组，不同元素的集合 tuple对象封装了一个list
	public void nextTuple() {
//		collector.emit(new Values("i am zzd love study"));//发射tuple，参数是list Values继承了ArrayList
//		collector.emit(new Values("i am zzd love study","aaavalue"));//values中可以放很多object，它会遍历object，把每一个字符串都加到集合中
		
		Values values=new Values("i am zzd love study");
		String messageID=UUID.randomUUID().toString().replaceAll("-", "");
		buffer.put(messageID, values);//缓存到hashmap中
		collector.emit(values,messageID);//1，需要ackfail时，请为每个tuple生成一个messageid，这个messageid是用来标识你关心的tuple 如果不指定messageid则不开始ack机制  关于ack机制见文档
	}

	//声明输出的tuple名  （数据源可以发射多个消息流stream，可以声明不同类型的数据流类型）
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("love")); //Fields是一个list
//		declarer.declare(new Fields("love","aaa"));//这里的字段名与tuple中的元素相对应，love对应"i am zzd love study"  aaa对应"aaavalue"
	}

	/*
	 * 当这个tuple被完全处理后（接收这个tuple的bolt 调用collector.ack（tuple）后），storm会调用ack方法，否则调用fail方法。至于消息是否重发，由你自己决定处理
	 */
	@Override
	public void ack(Object msgId) {
		System.out.println("tuple已经成功发送"+msgId);//这个msgId是发射时为tuple指定的messageID
		//成功发送就移除掉这个tuple
		buffer.remove(msgId);
	}
	
	@Override
	public void fail(Object msgId) {
		//发送失败重新发送
		System.out.println("发送失败！");
		Values values = buffer.get(msgId);//先根据tuple的uuid获取到values
		collector.emit(values,msgId);//重新发送
		
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
