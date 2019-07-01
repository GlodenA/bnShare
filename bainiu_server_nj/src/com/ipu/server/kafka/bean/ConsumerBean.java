package com.ipu.server.kafka.bean;

import java.util.List;

import com.ailk.common.data.IDataset;



public class ConsumerBean extends BaseConsumer{
	
	private String topicName;
    private int partitionId;
    private Long offset;

	public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }
    

    public ConsumerBean() {
		super();
	}

	public ConsumerBean(String topicName, int partitionId, Long offset) {
		super();
		this.topicName = topicName;
		this.partitionId = partitionId;
		this.offset = offset;
	}

	@Override
    public String toString() {
        return "OffsetInfo{" +
                "topicName='" + topicName + '\'' +
                ", partitionId=" + partitionId +
                ", offset=" + offset +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsumerBean)) return false;

        ConsumerBean that = (ConsumerBean) o;

        if (partitionId != that.partitionId) return false;
        return !(topicName != null ? !topicName.equals(that.topicName) : that.topicName != null);

    }

    @Override
    public int hashCode() {
        int result = topicName != null ? topicName.hashCode() : 0;
        result = 31 * result + partitionId;
        return result;
    }
    
}
