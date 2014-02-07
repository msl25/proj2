package ece842.core;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TimeStampedMessage extends Message implements Serializable {

	private TimeStamp timestamp;

	public TimeStampedMessage(String dest, String kind, String data) {
		super(dest, kind, data);
		this.timestamp = new TimeStamp();
	}
	
	public TimeStampedMessage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public TimeStamp getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(TimeStamp timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return String.format("%s %s %s %s %s %s %s\n", this.getSource(), this.getDest(), this.getSequenceNumber(),
				this.getKind(),this.getDupe(), this.getData().toString(), this.getTimestamp().toString()); 
	}
}