package ece842.services;

import ece842.core.TimeStamp;

public abstract class ClockService {
	protected String id;
	protected TimeStamp time;
		
	public abstract TimeStamp getNewTimeStamp();
	public abstract void updateClock(TimeStamp ts);
	public abstract TimeStamp getClock();
}