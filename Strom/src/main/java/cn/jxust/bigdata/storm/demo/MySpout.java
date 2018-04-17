package cn.jxust.bigdata.storm.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

public class MySpout  extends BaseRichSpout{

	SpoutOutputCollector collector;
	
	
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector=collector;//≥ı ºªØcollector
	}

	public void nextTuple() {
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream("C:/Users/zzd/Desktop/cite75_99.txt")));
			String str="";
			while((str=br.readLine())!=null){
				String[] split = str.split(",");
//				System.out.println(str);
				collector.emit(new Values(split[0],split[1]));
				Thread.sleep(1);
			}
			
			BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:/Users/zzd/Desktop/aaaaaaaaaaa1.txt")));
			bw.write("ok-----------------------------------");
			bw.flush();
			Thread.sleep(1000000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("one","two")); 
	}
	
}
