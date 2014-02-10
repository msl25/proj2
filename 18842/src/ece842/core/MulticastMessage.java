package ece842.core;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MulticastMessage implements Serializable {
	
	private TimeStamp timeStamp;
	private String GroupName;
	private String origSender;
	
	public MulticastMessage(TimeStamp ts, String grpName, String oSender) {
		this.timeStamp = ts;
		this.GroupName = grpName;
		this.origSender = oSender;
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
	public String getOriginalSender() {
		return this.origSender;
	}
	public void setGroupName(String groupName) {
		GroupName = groupName;
	}
}
