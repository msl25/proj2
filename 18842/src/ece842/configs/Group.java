package ece842.configs;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import ece842.core.Message;
import ece842.core.MulticastMessage;
import ece842.core.TimeStamp;
import ece842.services.GroupClock;
import ece842.services.MulticastService;

public class Group {
	private String name;
	private Set<String> members;
	private GroupClock groupClock;
	private MulticastService multicastsvc;
	private PriorityBlockingQueue<Message> holdQueue;

	public Group(String name) {
		this.name = name;
		this.members = new HashSet<String>();
		this.groupClock = null;
		this.multicastsvc = new MulticastService();
		this.holdQueue = new PriorityBlockingQueue<Message>(100,
				new MulticastMessageComparator());
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
		this.holdQueue.add(msg);
	}

	@Override
	public String toString() {
		String result = "Group [" + this.name + ": ";
		boolean first = true;
		for (String m : this.members) {
			if (first) {
				first = false;
			} else {
				result += ", ";
			}
			result += m;
		}
		result = result + "]";
		return result;
	}
	
	public PriorityBlockingQueue<Message> getHoldQueue() {
		return this.holdQueue;
	}

	public GroupClock getGroupClock() {
		return groupClock;
	}

	public void setGroupClock(GroupClock groupClock) {
		this.groupClock = groupClock;
	}

	public MulticastService getMulticastsvc() {
		return multicastsvc;
	}

	public void setMulticastsvc(MulticastService multicastsvc) {
		this.multicastsvc = multicastsvc;
	}
	
	private class MulticastMessageComparator implements Comparator<Message> {

		@Override
		public int compare(Message o1, Message o2) {
			MulticastMessage m1 = o1.getMulticastMsg();
			MulticastMessage m2 = o2.getMulticastMsg();
			
			if(m1 == null || m2 == null) {
				throw new NullPointerException();
			}
			
			TimeStamp ts1 = m1.getTimeStamp();
			TimeStamp ts2 = m2.getTimeStamp();
    		boolean happenBefore = true;
    		boolean happenAfter = true;
    		
    		// happenBefore
    		for (String id : ts1.timeStamp.keySet()) {
    			if (ts1.timeStamp.get(id) > ts2.timeStamp.get(id)) {
    				happenBefore = false;
    			}
    			if (ts1.timeStamp.get(id) < ts2.timeStamp.get(id)) {
    				happenAfter = false;
    			}
    		}
    		if (happenBefore == true)
    			return -1;
    		else if (happenAfter == true)
    			return 1;
    		
    		return 0;
		}
	}
}
