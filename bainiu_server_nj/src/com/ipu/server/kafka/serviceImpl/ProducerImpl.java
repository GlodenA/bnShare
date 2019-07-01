package com.ipu.server.kafka.serviceImpl;


import java.util.List;

import org.springframework.stereotype.Service;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import com.ipu.server.kafka.exception.MsgIsNullException;
import com.ipu.server.kafka.service.ProducerInter;


@Service
public class ProducerImpl implements ProducerInter{

	@Override
	public void sendMsg(Object msg,Producer producer,String topic) throws Exception {
		if(msg == null)throw new MsgIsNullException("the kafka msg is null");
		KeyedMessage km = new KeyedMessage<String,Object>(topic, msg);
        producer.send(km);
		
	}

	@Override
	public void sendMsg(List<Object> msgs,Producer producer,String topic) throws Exception {
		if(msgs == null)throw new MsgIsNullException("the kafka msg list is null");
		for (Object msg : msgs) {
			this.sendMsg(msg, producer, topic);
		}
	}
	
}
