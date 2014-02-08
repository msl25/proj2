package ece842.core;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MulticastMessage implements Serializable {
	
	private TimeStamp timeStamp;
	private String GroupName;
	
	public MulticastMessage(TimeStamp ts, String grpName) {
		this.timeStamp = ts;
		this.GroupName = grpName;
	}
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
