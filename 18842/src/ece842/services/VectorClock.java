package ece842.services;

import ece842.configs.Configuration;
import ece842.core.TimeStamp;

public class VectorClock extends ClockService{
	
	public VectorClock (Configuration globalConf, String id) {
		this.id = id;
		this.time = new TimeStamp();
		for (String peerName : globalConf.getPeers().keySet()) {
			this.time.timeStamp.put(peerName, 0);
		}
	}

	@Override
	public TimeStamp getNewTimeStamp() {
		this.time.incTimeStampValue(id);
		return this.time;
	}

	@Override
	public void updateClock(TimeStamp ts) {
		for (String id : this.time.timeStamp.keySet()) {
			this.time.timeStamp.put(id, Math.max(this.time.timeStamp.get(id), ts.timeStamp.get(id) + (this.id.equals(id) ? 1 : 0)));
		}
	}

	@Override
	public TimeStamp getClock() {
		return time;
	}
}
