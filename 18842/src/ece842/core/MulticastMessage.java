package ece842.core;

import java.io.Serializable;

public class MulticastMessage extends TimeStampedMessage implements Serializable {
	
	private TimeStamp timeStamp;
	private String GroupName;
	
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(TimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getGroupName() {
		return GroupName;
	}
	public void setGroupName(String groupName) {
		GroupName = groupName;
	}
	
	
}
