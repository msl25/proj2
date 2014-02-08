package ece842.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import ece842.configs.Configuration;
import ece842.configs.Group;
import ece842.services.ClockService;
import ece842.services.LogicalClock;
import ece842.services.MulticastService;
import ece842.services.VectorClock;

public class Application {
	public static String id;
	public static Configuration globalConf;
	public static MessagePasser messagePasser;
	public static ClockService localClock;
	public static ClockService multicastClock;
	public static Map<String, String> groupName = new HashMap<String, String>();
	public static MulticastService multicastSvc;
	
	public static void main(String[] args) throws IOException {
		id = args[1];
		globalConf = new Configuration(args[0]);
		if (globalConf.getClockType().equals("logical"))
			localClock = new LogicalClock();
		else
			localClock = new VectorClock(globalConf, id);

		try {
			messagePasser = new MessagePasser(globalConf, id, localClock);
		} catch (IOException e) {

			System.out.println("Error initialing MessagerPasser: " + e.getMessage());
			e.printStackTrace();		

			System.exit(1);
		}
		
//		Map<String,Group> groups = globalConf.getGroups();
//		for(String s : groups.keySet()) {
//			System.out.println(groups.get(s));
//		}

		Scanner scanIn = new Scanner(System.in);
		System.out
				.println("\nOptions available\n a)Send Message\n b)Check received Messages\n c)Display Clock\n d)Dummy Event\n e)Exit\n Please enter your choice");
		String task = scanIn.nextLine();
		while (true) {
			if (task.equalsIgnoreCase("a")) {
				handleSendRequest(scanIn, messagePasser);
			} else if (task.equalsIgnoreCase("b")) {
				handleReceiveRequest(messagePasser);
			} else if (task.equalsIgnoreCase("c")) {
				System.out.println(localClock.getClock().timeStamp.toString());
			} else if (task.equalsIgnoreCase("d")) {
				localClock.getNewTimeStamp();
				System.out.println("\nLogging? (y/n)");
				String choice = scanIn.nextLine();
				if (choice.equalsIgnoreCase("y")) {
					Message msg = new TimeStampedMessage("logger", "log",
							id.concat(" Self-Event"));
					msg.setSource(id);
					msg.setTimestamp(localClock.getClock());
					messagePasser.sendMessage(msg);
				}
			} else if (task.equalsIgnoreCase("e")) {
				messagePasser.exit();
				System.out.println("\nApplication Closed");
				break;
			} else {
				System.out.println("\nIncorrect Option selected.");
			}
			System.out
					.println("\nOptions available\n a)Send Message\n b)Check received Messages\n c)Display Clock\n d)Dummy Event\n e)Exit\n Please eneter your choice");
			task = scanIn.nextLine();
		}
	}

	private static void handleSendRequest(Scanner scanIn,
			MessagePasser messagePasser) {
		System.out.println("\nEnter Destination:");
		String dest = scanIn.nextLine();
		System.out.println("\nEnter kind of message:");
		String kind = scanIn.nextLine();
		System.out.println("\nEnter Data:");
		String data = scanIn.nextLine();
		
		System.out.println("\nLogging? (y/n)");
		String choice = scanIn.nextLine();

		if (groupName.containsKey(dest)) {
			Message msg = new Message(dest, kind, data);
			handleMulticast(messagePasser, msg);

		} else {
			Message message = new TimeStampedMessage(dest, kind, data);
			try {
				messagePasser.send(message);
				/* Sending the same message to logger */
				if (choice.equalsIgnoreCase("y")) {

					Message msg = new TimeStampedMessage("logger", "log",
							message.toString());
					msg.setSource(id);
					localClock.getNewTimeStamp();
					msg.setTimestamp(localClock.getClock());
					messagePasser.sendMessage(msg);
				}

			} catch (IOException e) {
				System.out
						.println("Error while sending the message, error was:"
								+ e.getMessage());
				e.printStackTrace();
			}
		}
		System.out.println("\nMessage sent!");
	}

	private static void handleReceiveRequest(MessagePasser messagePasser) {
		System.out.println("\nLogging? (y/n)");
		@SuppressWarnings("resource")
		Scanner scanIn = new Scanner(System.in);
		String choice = scanIn.nextLine();

		System.out.println("\nWaiting for message...");
		Message message = null;
		try {
			message = messagePasser.receive();
			if (message != null) {
				System.out
						.println(String
								.format("Message received from %s. with seqNo:%s Request Type:%s, Duplicate Flag is %s, Data is as follows:%s\n",
										message.getSource(),
										message.getSequenceNumber(),
										message.getKind(), message.getDupe(),
										message.getData()));

				if (choice.equalsIgnoreCase("y")) {
					Message msg = new TimeStampedMessage("logger", "log",
							message.toString());
					msg.setSource(id);
					localClock.getNewTimeStamp(); // XXX
					msg.setTimestamp(localClock.getClock());
					messagePasser.sendMessage(msg);
				}
			} else {
				System.out.println("No Message");
			}
		} catch (IOException e) {
			System.out.println("Error while sending the message, error was:"
					+ e.getMessage());
			e.printStackTrace();
		}

	}

	private static void handleMulticast(MessagePasser messagepasser, Message msg) {
		
		multicastSvc = new MulticastService();
		multicastClock = new VectorClock(globalConf, id);

		try {
			messagePasser = new MessagePasser(globalConf, id, multicastClock);
			multicastSvc.multicastSend(messagepasser, msg);
		} catch (IOException e) {
			System.out.println("Error initialing MessagerPasser for multicast: "
					+ e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}