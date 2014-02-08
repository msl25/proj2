package ece842.core;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Serializable {
	private String source;
	private int sequenceNumber;
	private Boolean dupe = false;
	private String dest;
	private String kind;
	private Object data;

	public Message () {
		
	}
	
	public Message(String dest, String kind, Object data) {
		this.dest = dest;
		this.kind = kind;
		this.data = data;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public void setDupe(Boolean dupe) {
		this.dupe = dupe;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getSource() {
		return source;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public Boolean getDupe() {
		return dupe;
	}

	public String getDest() {
		return dest;
	}

	public String getKind() {
		return kind;
	}

	public Object getData() {
		return data;
	}

	public TimeStamp getTimestamp() {
		return null;
	}

	public void setTimestamp(TimeStamp ts ) {
	}
	
	public MulticastMessage getMulticastMsg() {
		return null;
	}

	public void setMulticastMsg(MulticastMessage multicastMsg) {
		//
	}
	@Override
	public String toString() {
		return String.format("%s %s %s %s %s %s\n", this.getSource(), this.getDest(), this.getSequenceNumber(), this.getKind(),this.getDupe(), this.getData().toString()); 
	}
}
