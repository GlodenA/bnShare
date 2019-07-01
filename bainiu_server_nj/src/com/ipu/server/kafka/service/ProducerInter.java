package com.ipu.server.kafka.service;

import java.util.List;



import kafka.javaapi.producer.Producer;



public interface ProducerInter {

	//发送消息:单条
	void sendMsg(Object msg,Producer producer,String topic) throws Exception;
	
	//发送消息:多条分装到一个list
	void sendMsg(List<Object> msgs,Producer producer,String topic) throws Exception;
}
