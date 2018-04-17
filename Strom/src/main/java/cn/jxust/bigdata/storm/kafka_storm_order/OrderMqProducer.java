package cn.jxust.bigdata.storm.kafka_storm_order;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 *ģ�ⶩ��ϵͳ����������kafka 
 *
 */
public class OrderMqProducer {
	public static void main(String[] args) {
		String TOPIC = "orderMq";//����
		
		//����
        Properties p = new Properties();
        p.put("serializer.class", "kafka.serializer.StringEncoder");
        p.put("metadata.broker.list", "master:9092,slave1:9092,slave2:9092");
        p.put("request.required.acks", "1");
        p.put("partitioner.class", "kafka.producer.DefaultPartitioner");
        
        //���������߶���
        Producer<String, String> producer = new Producer<String, String>(new ProducerConfig(p));
        
        //������Ϣ
        for (int messageNo = 1; messageNo < 10000; messageNo++) {
            producer.send(new KeyedMessage<String, String>(TOPIC, messageNo + "",new OrderInfo().random() ));//json�ַ���
            try {
                Thread.sleep(200);//˯һ�ᣬ��Ȼredis����������̫�٣��ٶȸ�����storm
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
}
