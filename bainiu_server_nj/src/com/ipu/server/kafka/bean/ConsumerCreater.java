package com.ipu.server.kafka.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.ailk.common.data.IDataset;



public class ConsumerCreater {

	private static String brokerList = null;
    private static String clientName;
    static {
        InputStream in = null;
        Properties prop = new Properties();
        try {
            in = ConsumerCreater.class.getResourceAsStream("kafka-consumer-broker.properties");
            prop.load(in);
            clientName = prop.getProperty("client.name");
            brokerList = prop.getProperty("broker.list");
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
	

	public static String getBrokerList() {
		return brokerList;
	}

	public static void setBrokerList(String brokerList) {
		ConsumerCreater.brokerList = brokerList;
	}

	public static String getClientName() {
		return clientName;
	}

	public static void setClientName(String clientName) {
		ConsumerCreater.clientName = clientName;
	}
}
