package ece842.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ece842.actions.ActionFactory;
import ece842.configs.Configuration;
import ece842.configs.Rule;
import ece842.services.ClockService;

public class MessagePasser {
	private String id;
	private DatagramSocket socket;
	private Configuration configuration;
	private AtomicInteger seqNo = new AtomicInteger(0);
	private List<Message> receivedMessages = new ArrayList<Message>();
	private List<Message> delayedSendMessages = new ArrayList<Message>();
	private List<Message> delayedReceiveMessages = new ArrayList<Message>();
	private List<Message> messagesReadyToBeDelivered = new ArrayList<Message>();
	private ClockService localClock = null;
	
	public MessagePasser(Configuration conf, String id, ClockService localClock)
			throws IOException {
		this.id = id;
		this.configuration = conf;
		int port = this.configuration.getPeers().get(id).getPort();
		this.localClock = localClock;
		this.socket = new DatagramSocket(port);
		this.socket.setSoTimeout(1000);
	}

	public void addDelayedSendMessages(Message delayedMessage) {
		this.delayedSendMessages.add(delayedMessage);
	}

	public void addDelayedReceiveMessages(Message delayedMessage) {
		this.delayedReceiveMessages.add(delayedMessage);
	}

	public List<Message> getDelayedSendMessages() {
		return delayedSendMessages;
	}

	public List<Message> getDelayedReceiveMessages() {
		return delayedReceiveMessages;
	}

	public void clearDelayedSendMessages() {
		delayedSendMessages.clear();
		;
	}

	public void clearDelayedReceiveMessages() {
		delayedReceiveMessages.clear();
	}

	void send(Message message) throws IOException {
				
		this.configuration.updateRules();		
		TimeStamp timestamp = this.localClock.getNewTimeStamp();
		
		message.setTimestamp(timestamp);
		message.setSource(id);
		message.setSequenceNumber(seqNo.getAndIncrement());

		for (Rule rule : configuration.getSendRules()) {
			if (rule.isSatisfied(message)) {
				ActionFactory.getActionExecutor(rule.getAction()).executeSend(
						message, this);
				break;
			}
		}
	}

	Message receive() throws IOException {
		
		if (messagesReadyToBeDelivered.isEmpty()) {
			Message rxMessage = null;
			do {
				rxMessage = (TimeStampedMessage)receiveMessage();
				
				if (rxMessage != null) {
					receivedMessages.add(rxMessage);
				}
			} while (rxMessage != null);
			if (!receivedMessages.isEmpty()) {
				for (Message receivedMessage : receivedMessages) {
					this.configuration.updateRules();
					for (Rule rule : configuration.getReceiveRules()) {
						if (rule.isSatisfied(receivedMessage)) {
							messagesReadyToBeDelivered.addAll(ActionFactory
									.getActionExecutor(rule.getAction())
									.executeReceive(receivedMessage, this));
							break;
						}
					}
				}
				receivedMessages.clear();
				if (!messagesReadyToBeDelivered.isEmpty()) {
					Message message = messagesReadyToBeDelivered.get(0);
					messagesReadyToBeDelivered.remove(0);
					this.localClock.getNewTimeStamp();
					this.localClock.updateClock(message.getTimestamp());
					return message;
				}
			}
			return null;
		}
		Message message = messagesReadyToBeDelivered.get(0);
		messagesReadyToBeDelivered.remove(0);
		this.localClock.getNewTimeStamp();
		this.localClock.updateClock(message.getTimestamp());
		return message;

	}

	void exit() {
		socket.close();
	}

	public void sendMessage(Message message) {
		String destIP = configuration.getPeers().get(message.getDest())
				.getIP();
		int destPort = configuration.getPeers().get(message.getDest())
				.getPort();

		try {
			InetAddress address = InetAddress.getByName(destIP);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
			ObjectOutputStream os = new ObjectOutputStream(
					new BufferedOutputStream(byteStream));
			os.flush();
			os.writeObject(message);
			os.flush();

			byte[] sendBuf = byteStream.toByteArray();
			DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length,
					address, destPort);
			socket.send(packet);
			os.close();
		} catch (UnknownHostException e) {
			System.err.println("Exception:  " + e);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Message receiveMessage() {
		try {
			byte[] recvBuf = new byte[5000];
			DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
			socket.receive(packet);

			ByteArrayInputStream byteStream = new ByteArrayInputStream(recvBuf);
			ObjectInputStream is = new ObjectInputStream(
					new BufferedInputStream(byteStream));
			Message message = (Message) is.readObject();
			is.close();
			
			return message;
		} catch (InterruptedIOException e) {
			// System.out.println("No messages");
		} catch (IOException e) {
			System.err.println("Exception:  " + e);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
//	
//	public void displayLogs() {
//		this.clockType.display(messagesReadyToBeDelivered);
//	}
}
