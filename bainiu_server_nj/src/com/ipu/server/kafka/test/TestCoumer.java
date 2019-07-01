package com.ipu.server.kafka.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.ipu.server.kafka.bean.ConsumerBean;
import com.ipu.server.kafka.serviceImpl.ConsumerImpl;

public class TestCoumer {

	public static void main(String[] args) {
		try {
			Properties prop = new Properties();  
			InputStream in = new FileInputStream(new File("F:/mywork/bainiu_server_nj/etc/kafka-consumer-broker.properties"));
			prop.load(in);
			ConsumerBean consumerBean = new ConsumerBean();
	    	consumerBean.setClientName(prop.getProperty("client.name"));
	    	consumerBean.setBrokerList(prop.getProperty("broker.list"));
	    	consumerBean.setOffset(1600l);
	    	consumerBean.setPartitionId(0);
	    	consumerBean.setTopicName("test1");
	    	
	    	ConsumerImpl c = new ConsumerImpl();
	    	List list = c.consumer(consumerBean);
	    	for (Object object : list) {
				System.out.println(object.toString());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
