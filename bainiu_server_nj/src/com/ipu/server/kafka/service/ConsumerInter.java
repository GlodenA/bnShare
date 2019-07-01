package com.ipu.server.kafka.service;

import java.util.List;

import com.ipu.server.kafka.bean.ConsumerBean;

public interface ConsumerInter {
	//消费消息
	List<String> consumer(ConsumerBean consumerBean) throws Exception;
}
