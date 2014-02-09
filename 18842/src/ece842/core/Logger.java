package ece842.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import ece842.configs.Configuration;
import ece842.services.ClockService;
import ece842.services.LogicalClock;
import ece842.services.VectorClock;

public class Logger {
	public static String id = "logger";
	public static Configuration globalConf;
	public static MessagePasser messagePasser;
	public static ClockService localClock;

	public static List <TimeStampedMessage> msgList = new ArrayList<TimeStampedMessage> ();

	public static void main(String[] args) throws IOException {
		globalConf = new Configuration(args[0]);

		if (globalConf.getClockType().equals("logical"))
			localClock = new LogicalClock ();
		else
			localClock = new VectorClock (globalConf, id);

		try {
			messagePasser = new MessagePasser(globalConf, id,localClock);
		} catch (IOException e) {
			System.out.println("Could not initialise MessagerPasser, error was: "
					+ e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		Scanner scanIn = new Scanner(System.in);
		System.out
		.println("\nOptions available\n a)Display logs\n b)Exit\n Please select a or b ");
		String task = scanIn.nextLine();
		while (true) {
			if (task.equalsIgnoreCase("a")) {
				handleReceiveRequest(messagePasser);
			} else if (task.equalsIgnoreCase("quit")) {
				messagePasser.exit();
				System.out.println("\nApplication Closed");
				break;
			} else {
				System.out.println("\nIncorrect Option selected.");
			}
			System.out
			.println("\nOptions available\n a)Display logs\n b)Exit\n Please select a or b ");
			task = scanIn.nextLine();
		}
		scanIn.close();
	}

	private static void handleReceiveRequest(MessagePasser messagePasser) {
		System.out.println("\nWaiting for message...");
		Message message = null;
		while (true){
			message = messagePasser.receive();
			if (message != null) {
				msgList.add((TimeStampedMessage) message);
			} else {
//					System.out.println("No Message");
				break;
			}
		}
		if (globalConf.getClockType() == "logical") {
			Collections.sort(msgList, new TimeStampedMessageComparator());
			for (TimeStampedMessage msg : msgList) {
				System.out.println(String.format("[%s] %s\n", msg.getTimestamp().toString(), msg.getSource()));
			}
		} else {
			if (msgList.size() == 1) {
				System.out.println(String.format("[%s] %s", msgList.get(0).getTimestamp().toString(), 
						msgList.get(0).getSource()));
				return;
			}
			TimeStampedMessageComparator cmp = new TimeStampedMessageComparator();
			for (int i = 0; i < msgList.size(); i++) {
				TimeStampedMessage tsm1 = msgList.get(i);
				for (int j = i + 1; j < msgList.size(); j++) {
					TimeStampedMessage tsm2 = msgList.get(j);
					if (tsm1 != tsm2) {
						int ret;
						String relationship = "\n||\n";
						ret = cmp.compare(tsm1, tsm2);
						if (ret > 0)
							relationship = "\n<-\n";
						else if (ret < 0)
							relationship = "\n->\n";
						System.out.println(String.format("[%s] %s", tsm1.getTimestamp().toString(), 
								tsm1.getSource()).concat(relationship).concat(String.format("[%s] %s", 
										tsm2.getTimestamp().toString(), tsm2.getSource())));
					}
				}
			}
		}
	}
}