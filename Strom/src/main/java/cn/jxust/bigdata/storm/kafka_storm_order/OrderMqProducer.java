package cn.jxust.bigdata.storm.kafka_storm_order;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 *模拟订单系统生产订单到kafka 
 *
 */
public class OrderMqProducer {
	public static void main(String[] args) {
		String TOPIC = "orderMq";//主题
		
		//配置
        Properties p = new Properties();
        p.put("serializer.class", "kafka.serializer.StringEncoder");
        p.put("metadata.broker.list", "master:9092,slave1:9092,slave2:9092");
        p.put("request.required.acks", "1");
        p.put("partitioner.class", "kafka.producer.DefaultPartitioner");
        
        //构建生产者对象
        Producer<String, String> producer = new Producer<String, String>(new ProducerConfig(p));
        
        //生产信息
        for (int messageNo = 1; messageNo < 10000; messageNo++) {
            producer.send(new KeyedMessage<String, String>(TOPIC, messageNo + "",new OrderInfo().random() ));//json字符串
            try {
                Thread.sleep(200);//睡一会，不然redis连接数设置太少，速度跟不上storm
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
}
