package com.ipu.server.kafka.serviceImpl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.Broker;
import kafka.common.TopicAndPartition;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.TopicMetadataResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.Message;
import kafka.message.MessageAndOffset;

import com.ipu.server.kafka.bean.ConsumerBean;
import com.ipu.server.kafka.dao.KafkaDao;
import com.ipu.server.kafka.service.ConsumerInter;
@Service
public class ConsumerImpl implements ConsumerInter {
	private static transient Logger log = Logger.getLogger(ConsumerImpl.class);
	@Resource
	private KafkaDao kafkaDao;
	
	@Override
	public synchronized List<String> consumer(ConsumerBean consumerBean) throws Exception {
		List<String> msgList = new ArrayList<String>();
		String brokerList = consumerBean.getBrokerList();
		String clientName = consumerBean.getClientName();
		String topic = consumerBean.getTopicName();
		int partition = consumerBean.getPartitionId();
		long startOffset = consumerBean.getOffset();
		// 找到leader
        Broker leaderBroker = findLeader(brokerList, topic, partition);
        //从leader消费
        SimpleConsumer simpleConsumer = new SimpleConsumer(leaderBroker.host(), leaderBroker.port(), 60*1000,64 * 1024, clientName);
        ConsumerBean off = new ConsumerBean();
        off.setTopicName(topic);
        off.setPartitionId(partition);
        //找到最后的offset
        long lastOffset = getLastOffset(simpleConsumer, topic, partition, clientName);
        
        while (startOffset < lastOffset) {
            long offset = startOffset;
            // 添加fetch指定目标topic，分区，起始offset及fetchSize(字节)，可以添加多个fetch
            FetchRequest req = new FetchRequestBuilder().addFetch(topic, partition, startOffset, 20480).build();
            // 拉取消息
            FetchResponse fetchResponse = simpleConsumer.fetch(req);

            ByteBufferMessageSet messageSet = fetchResponse.messageSet(topic, partition);
            String msgStr = "";
            Message msg;
            ArrayList<Object[]> splitMsgList =  new ArrayList<Object[]>();
            for (MessageAndOffset messageAndOffset : messageSet) {
//            	long a = messageAndOffset.offset();
//            	System.out.println(a);
                msg = messageAndOffset.message();
                ByteBuffer payload = msg.payload();
                byte[] bytes = new byte[payload.limit()];
                
                payload.get(bytes);
                msgStr = new String(bytes);
                offset = messageAndOffset.offset();
               
                msgList.add(msgStr);
                startOffset = offset + 1;
                off.setOffset(startOffset);          
            }
        }
        kafkaDao.delOffert();
        kafkaDao.saveOffert(off);
        log.debug("saveOffert:" + startOffset);
		return msgList;
	}
	
	/**
     * 找到制定分区的leader broker
     *
     * @param brokerHosts broker地址，格式为：“host1:port1,host2:port2,host3:port3”
     * @param topic       topic
     * @param partition   分区
     * @return
     */
    public Broker findLeader(String brokerHosts, String topic, int partition) {
        PartitionMetadata metadata=findPartitionMetadata(brokerHosts, topic, partition);
        Broker leader = metadata.leader();
        //System.out.println(metadata.replicas());//获取所有副本
        return leader;
    }
    /**
     * 找到指定分区的元数据
     *
     * @param brokerHosts broker地址，格式为：“host1:port1,host2:port2,host3:port3”
     * @param topic       topic
     * @param partition   分区
     * @return 元数据
     */
    private PartitionMetadata findPartitionMetadata(String brokerHosts, String topic, int partition) {
        PartitionMetadata returnMetaData = null;
        SimpleConsumer consumer = null;
        for (String brokerHost : brokerHosts.split(",")) {
            String[] splits = brokerHost.split(":");
            consumer = new SimpleConsumer(splits[0], Integer.valueOf(splits[1]), 100000, 64 * 1024, "leaderLookup");
            List<String> topics = Collections.singletonList(topic);
            TopicMetadataRequest request = new TopicMetadataRequest(topics);
            TopicMetadataResponse response = consumer.send(request);
            List<TopicMetadata> topicMetadataList = response.topicsMetadata();
            for (TopicMetadata topicMetadata : topicMetadataList) {
                for (PartitionMetadata partitionMetadata : topicMetadata.partitionsMetadata()) {
                    if (partitionMetadata.partitionId() == partition) {
                        returnMetaData = partitionMetadata;
                    }
                }
            }
            if (consumer != null)
                consumer.close();
        }
        return returnMetaData;
    }

    public long getLastOffset(SimpleConsumer consumer, String topic,
			int partition, String clientName) {
        long whichTime = kafka.api.OffsetRequest.LatestTime();//从最后开始取

		TopicAndPartition topicAndPartition = new TopicAndPartition(topic,
				partition);
		Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
		requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(
				whichTime, 3));
		kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(
				requestInfo, kafka.api.OffsetRequest.CurrentVersion(),
				clientName);
		OffsetResponse response = consumer.getOffsetsBefore(request);

		if (response.hasError()) {
			System.out
					.println("Error fetching data Offset Data the Broker. Reason: "
							+ response.errorCode(topic, partition));
			return 0;
		}
		long[] offsets = response.offsets(topic, partition);
		return offsets[0];
	}
}
