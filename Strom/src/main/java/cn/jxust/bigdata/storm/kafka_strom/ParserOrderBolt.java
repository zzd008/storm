package cn.jxust.bigdata.storm.kafka_strom;

import java.util.Map;
import java.util.stream.Collector;

import com.google.gson.Gson;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import cn.jxust.bigdata.storm.kafka_storm_order.OrderInfo;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/**
 * ��spout���ѵ���json��������javabean��������ҵ���߼�����
 *
 */
public class ParserOrderBolt extends BaseRichBolt {
	private OutputCollector collector;
	private JedisPool pool;//redis���ӳ�

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector=collector;//��ʼ������ռ���
		
		//���ӳ�����
        JedisPoolConfig config = new JedisPoolConfig();
        
        //����һ��pool����ж��ٸ�״̬Ϊidle(���е�)��jedisʵ����
        config.setMaxIdle(10);
        
        //����һ��pool�ɷ�����ٸ�jedisʵ����ͨ��pool.getResource()����ȡ
        //�����ֵΪ-1�����ʾ�����ƣ����pool�Ѿ�������maxActive��jedisʵ�������ʱpool��״̬Ϊexhausted(�ľ�)��
        //��borrowһ��jedisʵ��ʱ���Ƿ���ǰ����validate���������Ϊtrue����õ���jedisʵ�����ǿ��õģ�
        config.setMaxTotal(1000 * 100);//���������
        
        //��ʾ��borrow(����)һ��jedisʵ��ʱ�����ĵȴ�ʱ�䣬��������ȴ�ʱ�䣬��ֱ���׳�JedisConnectionException��
        config.setMaxWaitMillis(5000);//�����ӳ���û������ʱ����ʱʱ��
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        
        /**
         *��������� java.net.SocketTimeoutException: Read timed out exception���쳣��Ϣ
         *�볢���ڹ���JedisPool��ʱ�������Լ��ĳ�ʱֵ. JedisPoolĬ�ϵĳ�ʱʱ����2��(��λ����)
         */
        pool = new JedisPool(config, "localhost", 6379);//��ʼ���������ӳ�
	}

	public void execute(Tuple input) {
//		String str = input.getString(0);
		//kafkaspout�����������tuple�зŵ���byte���飬���Բ�����getstring()���գ���getvalue��ȡֵ��Ȼ��ת��byte[]����ת��string
		
		//��ȡspout��������tuple
		String json=new String(((byte[])input.getValue(0)));
		//��jason������javabean����
		OrderInfo orderInfo = new Gson().fromJson(json, OrderInfo.class);
		
		//������վ������ҵ���ߣ�����Ʒ�࣬�������̣�����Ʒ�ƣ�ÿ����Ʒ
		Jedis jedis=pool.getResource();//��ȡһ��redis����
		
		//��ȡ������վ�Ľ��ͳ��ָ��
//		String totalAmount = jedis.get("totalAmount");
		//����ۼ�     redis�в����ڸ�keyʱ���Զ�����
		jedis.incrBy("totalAmount", orderInfo.getProductPrice());//redis����ֵ���͵�key�����ۼ�
		
		
		//��ȡ��Ʒ������ҵ����ָ��
		String bid = getBusinessById(orderInfo.getProductId());//��ȡҵ����
		String bAmount=jedis.get(bid+"Amount");//��ȡҵ���߽��
		//����ۼ�
		jedis.incrBy(bid+"Amount", orderInfo.getProductPrice());
//		jedis.incrBy(bAmount, orderInfo.getProductPrice());//��������
		
		//��ĳһҵ���ߵ����а�sortset
		
		pool.returnResource(jedis);//�ͷ����� ��Ȼ��Դ������
//		jedis.close();
		
		
		collector.ack(input);//����ȷ����Ϣ
		
	}
	
	//������Ʒ��id����ȡ�������ĸ�ҵ����  Ҫ���ⲿ����Դ��ȡ�ֵ���Ϣ���ֵ���Ϣ���Է���mysql��Ҳ���Է���redis��
	private String getBusinessById(String productId) {//Ҳ����д�ھ�̬�������
		//�����redis�л�ȡ
		return "3c";
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}

}
