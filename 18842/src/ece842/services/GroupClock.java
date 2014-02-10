package ece842.services;

import ece842.configs.Configuration;
import ece842.configs.Group;
import ece842.core.TimeStamp;

public class GroupClock {

	private TimeStamp time;
	private int sendCount;
	
	public GroupClock(Configuration globalConf, String groupName) {
		this.time = new TimeStamp();
		this.sendCount = 0;
		Group g = globalConf.getGroups().get(groupName);
		for (String member : g.getMembers()) {
			this.time.timeStamp.put(member, 0);
		}
	}

	public TimeStamp incrementTimestamp(String id) {
		this.time.incTimeStampValue(id);
		return this.time;
	}
	
	public void incrementSendCount() {
		this.sendCount++;
	}
	
	public int getSendCount() {
		return this.sendCount;
	}
	
	public TimeStamp getTimeStamp() {
		return this.time;
	}
}
