package com.ipu.server.kafka.dao;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ipu.server.dao.SmartBaseDao;
import com.ipu.server.kafka.bean.ConsumerBean;
import com.ipu.server.kafka.bean.ConsumerCreater;
import com.ipu.server.kafka.exception.OffsetException;

public class KafkaDao extends SmartBaseDao{
	
	private static transient Logger log = Logger.getLogger(KafkaDao.class);
	

	public KafkaDao(String connName) throws Exception {
		super(connName);		
	}

	public void saveOffert(ConsumerBean consumerBean) throws Exception{		
		String topic = consumerBean.getTopicName();
		int partition = consumerBean.getPartitionId();
		long startOffset = consumerBean.getOffset();
		StringBuffer sql = new StringBuffer();
		sql.append("insert into tf_f_kafka_offset (topicName,partitionId,offsett,creatTime,stats) VALUES (:TOPICNAME,:PARTITIONID,:OFFSETT,NOW(),0)");
		IData param = new DataMap();
		param.put("topicName", topic);
		param.put("partitionId", partition);
		param.put("offsett", startOffset);
		this.executeUpdate(sql.toString(), param);
		
	}
	
	@SuppressWarnings("deprecation")
	public void delOffert() throws Exception{
	
		StringBuffer sql = new StringBuffer();
		sql.append("update tf_f_kafka_offset set stats = 1 where stats != 1");
		this.executeUpdate(sql.toString());
		
	}
	
	public ConsumerBean getConsumerBean() throws Exception{
		ConsumerBean consumerBean = new ConsumerBean();
    	consumerBean.setClientName(ConsumerCreater.getClientName());
    	consumerBean.setBrokerList(ConsumerCreater.getBrokerList());
		StringBuffer sql = new StringBuffer();
    	sql.append("select * from tf_f_kafka_offset where stats != 1");
    	IDataset set = this.queryList(sql.toString());
    	if(set.size() != 1){
    		throw new OffsetException("the offset must only one");
    	}
    	IData idata = set.first();
    	consumerBean.setTopicName(idata.getString("topicName"));
    	consumerBean.setPartitionId(idata.getInt("partitionId"));
    	consumerBean.setOffset(idata.getLong("offsett"));
    	return consumerBean;
	}
	
}
