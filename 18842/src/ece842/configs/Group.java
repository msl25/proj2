package ece842.configs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ece842.core.Message;
import ece842.core.TimeStamp;
import ece842.services.ClockService;
import ece842.services.MulticastService;

public class Group {
	private String name;
	private Set<String> members;
	private ClockService groupClock;
	private MulticastService multicastsvc;
	private Map<TimeStamp, Message> holdQueue; 
	
	
	public Group(String name) {
		this.name = name;
		this.members = new HashSet<String>();
		this.groupClock = null;
		this.multicastsvc = new MulticastService();
		this.holdQueue = new HashMap<TimeStamp, Message>(); 
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void addMember(String member) {
		this.members.add(member);
	}
	
	public boolean isMember(String member) {
		return this.members.contains(member);
	}
	
	public Collection<String> getMembers() {
		return this.members;
	}
	
	public void insertHoldQueue(Message msg) {
		this.holdQueue.put(msg.getMulticastMsg().getTimeStamp(), msg);
	}
	
	public Message removeHoldQueue(TimeStamp ts) {
		return this.holdQueue.remove(ts);
	}
	@Override
	public String toString() {
		String result = "Group [" + this.name + ": ";
		boolean first = true;
		for(String m : this.members) {
			if(first) {
				first = false;
			} else {
				result += ", ";
			}
			result += m;
		}
		result = result + "]";
		return result;
	}

	public ClockService getGroupClock() {
		return groupClock;
	}

	public void setGroupClock(ClockService groupClock) {
		this.groupClock = groupClock;
	}

	public MulticastService getMulticastsvc() {
		return multicastsvc;
	}

	public void setMulticastsvc(MulticastService multicastsvc) {
		this.multicastsvc = multicastsvc;
	}
	public Map<TimeStamp, Message> getHoldQueue() {
		return holdQueue;
	}
	public void setHoldQueue(Map<TimeStamp, Message> holdQueue) {
		this.holdQueue = holdQueue;
	}
}
