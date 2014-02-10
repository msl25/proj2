package ece842.core;

public class MessageFlooder implements Runnable {
	private final int ITERS = 15;
	private final int SLEEP = 2000;
	private MessagePasser mp;
	private Message msg;
	
	public MessageFlooder(MessagePasser mp, Message msg) {
		this.mp = mp;
		this.msg = msg;
	}

	@Override
	public void run() {
		for(int ii = 0; ii < ITERS; ii++) {
			try {
				this.mp.send(this.msg);
				Thread.sleep(SLEEP);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
