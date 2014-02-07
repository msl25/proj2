package ece842.services;

import ece842.core.TimeStamp;

public class LogicalClock extends ClockService{
	
	public LogicalClock () {
		id = "logical";
		this.time = new TimeStamp();
		this.time.timeStamp.put(id, 0);
	}

	@Override
	public TimeStamp getNewTimeStamp() {
		this.time.incTimeStampValue(id);
		return this.time;
	}
	
	@Override
	public void updateClock(TimeStamp ts) {
		for (String id : this.time.timeStamp.keySet()) {
			this.time.timeStamp.put(id, Math.max(this.time.timeStamp.get(id), ts.timeStamp.get(id)+1));
		}
	}

	@Override
	public TimeStamp getClock() {
		return time;
	}
}
