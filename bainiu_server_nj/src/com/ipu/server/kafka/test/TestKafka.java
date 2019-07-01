package com.ipu.server.kafka.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import com.ipu.server.kafka.bean.ProducerCreater;
import com.ipu.server.kafka.dao.KafkaDao;
import com.ipu.server.kafka.service.ProducerInter;
import com.ipu.server.kafka.serviceImpl.ProducerImpl;

public class TestKafka {
	public static void main(String[] args) {
		aa();		
	}
	
	public static void aa(){
		try {
			Properties prop = new Properties();  
	        InputStream in = new FileInputStream(new File("F:/mywork/bainiu_server_nj/etc/kafka-producer.properties"));
	        prop.load(in);
	        ProducerConfig config = new ProducerConfig(prop);
			Producer p = new Producer(config);
			ProducerInter pi = new ProducerImpl();
			for (int i = 0; i < 100; i++) {
//				KeyedMessage km = new KeyedMessage<String,Object>("testTopic", "测试"+String.valueOf(i));
				pi.sendMsg("kafka"+String.valueOf(i),p,"test1" );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
