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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import ece842.actions.ActionFactory;
import ece842.configs.Configuration;
import ece842.configs.Group;
import ece842.configs.Rule;
import ece842.services.ClockService;

public class MessagePasser {

	LinkedBlockingQueue<Message> temp = new LinkedBlockingQueue<Message>();
	
	private class RecvThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					receiveThread();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	private String id;
	private DatagramSocket socket;
	private Configuration configuration;
	private AtomicInteger seqNo = new AtomicInteger(0);
	// private List<Message> receivedMessages = new ArrayList<Message>();
	private List<Message> delayedSendMessages = new ArrayList<Message>();
	private List<Message> delayedReceiveMessages = new ArrayList<Message>();
	private List<Message> messagesReadyToBeDelivered = new ArrayList<Message>();
	private ClockService localClock = null;
	private Map<String, Map<String, Integer>> AckMap = new HashMap<String, Map<String, Integer>>();

	public MessagePasser(Configuration conf, String id, ClockService localClock)
			throws IOException {
		this.id = id;
		this.configuration = conf;
		int port = this.configuration.getPeers().get(id).getPort();
		this.localClock = localClock;
		this.socket = new DatagramSocket(port);
		this.socket.setSoTimeout(1000);
		// Start receive thread
		new Thread(new RecvThread()).start();
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
	}

	public void clearDelayedReceiveMessages() {
		delayedReceiveMessages.clear();
	}

	public void send(Message message) throws IOException {

		this.configuration.updateRules();
		TimeStamp timestamp = this.localClock.getNewTimeStamp();

		message.setTimestamp(timestamp);
		message.setSource(id); // XXX
		message.setSequenceNumber(seqNo.getAndIncrement());

		for (Rule rule : configuration.getSendRules()) {
			if (rule.isSatisfied(message)) {
				ActionFactory.getActionExecutor(rule.getAction()).executeSend(
						message, this);
				break;
			}
		}
	}

	Message receive() {
		if (!messagesReadyToBeDelivered.isEmpty()) {
			Message message = messagesReadyToBeDelivered.get(0);
			messagesReadyToBeDelivered.remove(0);
			this.localClock.getNewTimeStamp();
			this.localClock.updateClock(message.getTimestamp());
			return message;
		} else
			return null;
	}

	void receiveThread() throws IOException, InterruptedException {
		Message rxMessage = (TimeStampedMessage) receiveMessage();
		if (rxMessage == null) {
			return;
		}

		this.configuration.updateRules();
		List<Message> messages = new ArrayList<Message>();
		for (Rule rule : configuration.getReceiveRules()) {
			if (rule.isSatisfied(rxMessage)) {
				messages.addAll(ActionFactory.getActionExecutor(
						rule.getAction()).executeReceive(rxMessage, this));
				break;
			}
		}

		for (Message m : messages) {
			if (m.getMulticastMsg() != null)
				receiveMulticastMsg(m);
			else
				this.messagesReadyToBeDelivered.add(m);
		}
	}

	void exit() {
		socket.close();
	}

	public void sendMessage(Message message) {
		String destIP = configuration.getPeers().get(message.getDest()).getIP();
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

	public String getLocalId() {
		return this.id;
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

	public void receiveMulticastMsg(Message rxMsg) {

		boolean isPresent = false;
		boolean isReliable = true;
		MulticastMessage innerMsg = rxMsg.getMulticastMsg();
		TimeStamp multicastTimeStamp = rxMsg.getMulticastMsg().getTimeStamp();
		String groupName = rxMsg.getMulticastMsg().getGroupName();
		Group group = configuration.getGroups().get(groupName);
		String originalSender = innerMsg.getOriginalSender();

		Map<String, Integer> nodeMap = new HashMap<String, Integer>();

		// Check whether nodeMap is already created
		if (AckMap.containsKey(multicastTimeStamp.toString()))
			nodeMap = AckMap.get(multicastTimeStamp.toString());
		else {
			nodeMap = new HashMap<String, Integer>();
			// Multicast the same msg
			if (!rxMsg.getSource().equalsIgnoreCase(rxMsg.getDest())) {
				Collection<String> sendTo = group.getMembers();
				for (String s : sendTo) {
					Message msg = new TimeStampedMessage(s, rxMsg.getKind(),
							rxMsg.getData().toString());
					msg.setMulticastMsg(rxMsg.getMulticastMsg());
					try {
						send(msg);
					} catch (IOException e) {
						System.out.println("Multicast msg from " + id + "to "
								+ s + "failed..!");
					}
				}
			}
		}

		// check if originator is given process, if not TODO
		// Check whether holdQueue has this msg already
		for (Integer value : nodeMap.values()) {
			if (value == 1) {
				isPresent = true;
				break;
			}
		}

		nodeMap.put(rxMsg.getSource(), 1);
		AckMap.put(multicastTimeStamp.toString(), nodeMap);

		// Insert into holdQueue
		if (!isPresent) {
			group.insertHoldQueue(rxMsg);
		}

		// Add to receive buffer
		Collection<String> members = group.getMembers();
		for (String m : members) {
			if (!nodeMap.containsKey(m) || nodeMap.get(m) != 1) {
				isReliable = false;
				break;
			}
		}
		if (isReliable) {
			// remove from holdQueue
			PriorityBlockingQueue<Message> holdQueue = group.getHoldQueue();

			while (true) {
				Message first = holdQueue.peek();
				if (first == null) {
					break;
				}

				MulticastMessage mcast = first.getMulticastMsg();
				TimeStamp mcastTS = mcast.getTimeStamp();
				TimeStamp groupTS = group.getGroupClock().getTimeStamp();
				boolean removed = false;
				int ts1 = mcastTS.getTimeStampValue(originalSender);
				int ts2 = groupTS.getTimeStampValue(originalSender);

				if (ts1 == ts2 + 1) {
					boolean allLt = true;
					for (String member : group.getMembers()) {
						if (member.compareTo(originalSender) != 0) {
							if (mcastTS.getTimeStampValue(member) > groupTS
									.getTimeStampValue(member)) {
								allLt = false;
								break;
							}
						}
					}
					if (allLt) {
						try {
							Message msg = holdQueue.take();
							group.getGroupClock().incrementTimestamp(
									originalSender);

							this.messagesReadyToBeDelivered.add(msg);
							removed = true;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				if (!removed) {
					break;
				}
			}
		}
	}
}
