package com.ipu.server.kafka.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

public class ProducerCreater {
	private static Producer producer=null;
	public static String topic = null;
	
	private static ProducerConfig config = null;
    public static ProducerConfig getConfig() {
        return config;
    }
    
    public static Producer getProducer(){
    	return producer;
    }
	 // 读取配置文件
    static {
        InputStream in = null;
        Properties prop = new Properties();
        try {
            in = ProducerCreater.class.getResourceAsStream("kafka-producer.properties");
            prop.load(in);
            config = new ProducerConfig(prop);
            topic = prop.getProperty("topic"); 
            producer = new Producer(config);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
