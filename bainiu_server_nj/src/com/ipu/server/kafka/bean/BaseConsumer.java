package com.ipu.server.kafka.bean;

public class BaseConsumer {
	private  String brokerList = null;
    private  String clientName;
	public String getBrokerList() {
		return brokerList;
	}
	public void setBrokerList(String brokerList) {
		this.brokerList = brokerList;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
    
}
