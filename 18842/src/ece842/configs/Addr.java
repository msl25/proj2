package ece842.configs;

public class Addr {
	private String IP;
	private int port;
	
	public Addr(Object IP, Object port) {
		this.IP = (String)IP;
		this.port = ((Integer)port).intValue();
	}
	public String getIP() {
		return IP;
	}
	public int getPort() {
		return port;
	}
}