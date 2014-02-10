package ece842.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings("serial")
public class TimeStamp implements Serializable, Comparable<TimeStamp> {

	public Map<String, Integer> timeStamp;

	public TimeStamp() {
		this.timeStamp = new HashMap<String, Integer>();
	}

	public void incTimeStampValue(String id) {
		this.timeStamp.put(id, this.timeStamp.get(id) + 1);
	}
	
	public void setTimeStampValue(String id, int value) {
		this.timeStamp.put(id, value);
	}
	
	public int getTimeStampValue(String id) {
		return this.timeStamp.get(id);
	}

	@Override
	public int compareTo(TimeStamp ts) {
		return 0;
	}
	
	@Override
	public String toString() {
		String s = "";
		SortedSet<String> keys = new TreeSet<String>(this.timeStamp.keySet());
		for (String key : keys) {
		   s= String.format("%s%s:%d ", s, key, this.timeStamp.get(key));
		}
		return s;
	}
	
	public TimeStamp clone() {
		TimeStamp ts = new TimeStamp();
		ts.timeStamp = new HashMap<String,Integer>(this.timeStamp);
		return ts;
	}
}