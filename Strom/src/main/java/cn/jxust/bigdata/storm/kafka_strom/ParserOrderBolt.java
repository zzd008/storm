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
 * 将spout消费到的json串解析成javabean，并进行业务逻辑处理
 *
 */
public class ParserOrderBolt extends BaseRichBolt {
	private OutputCollector collector;
	private JedisPool pool;//redis连接池

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector=collector;//初始化输出收集器
		
		//连接池配置
        JedisPoolConfig config = new JedisPoolConfig();
        
        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(10);
        
        //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取
        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setMaxTotal(1000 * 100);//最大连接数
        
        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(5000);//当连接池中没有连接时，超时时间
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        
        /**
         *如果你遇到 java.net.SocketTimeoutException: Read timed out exception的异常信息
         *请尝试在构造JedisPool的时候设置自己的超时值. JedisPool默认的超时时间是2秒(单位毫秒)
         */
        pool = new JedisPool(config, "localhost", 6379);//初始化创建连接池
	}

	public void execute(Tuple input) {
//		String str = input.getString(0);
		//kafkaspout中声明发射的tuple中放的是byte数组，所以不能用getstring()接收，用getvalue获取值，然后转成byte[]，再转成string
		
		//获取spout发过来的tuple
		String json=new String(((byte[])input.getValue(0)));
		//将jason解析成javabean对象
		OrderInfo orderInfo = new Gson().fromJson(json, OrderInfo.class);
		
		//整个网站，各个业务线，各个品类，各个店铺，各个品牌，每个商品
		Jedis jedis=pool.getResource();//获取一个redis连接
		
		//获取整个网站的金额统计指标
//		String totalAmount = jedis.get("totalAmount");
		//金额累加     redis中不存在该key时会自动创建
		jedis.incrBy("totalAmount", orderInfo.getProductPrice());//redis对数值类型的key进行累加
		
		
		//获取商品所属的业务线指标
		String bid = getBusinessById(orderInfo.getProductId());//获取业务线
		String bAmount=jedis.get(bid+"Amount");//获取业务线金额
		//金额累加
		jedis.incrBy(bid+"Amount", orderInfo.getProductPrice());
//		jedis.incrBy(bAmount, orderInfo.getProductPrice());//报错？？！
		
		//求某一业务线的排行榜sortset
		
		pool.returnResource(jedis);//释放连接 不然资源不够用
//		jedis.close();
		
		
		collector.ack(input);//发送确认信息
		
	}
	
	//根据商品的id来获取它属于哪个业务线  要从外部数据源获取字典信息，字典信息可以放在mysql中也可以放在redis中
	private String getBusinessById(String productId) {//也可以写在静态代码块中
		//这里从redis中获取
		return "3c";
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}

}
