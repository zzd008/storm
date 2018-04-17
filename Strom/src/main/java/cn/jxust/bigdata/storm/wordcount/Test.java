package cn.jxust.bigdata.storm.wordcount;

public class Test {
	public static int a=0;//静态变量，多线程共享的

	public static int getA() {
		return a;
	}

}
