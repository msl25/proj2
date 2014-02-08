package ece842.services;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import ece842.configs.Group;
import ece842.core.Message;
import ece842.core.MessagePasser;
import ece842.core.MulticastMessage;
import ece842.core.TimeStamp;
import ece842.core.TimeStampedMessage;

public class MulticastService {

	public void multicastSend(MessagePasser messagepasser, Message message,
			Group myGroup) {
		/*
		 * for(String s : groups.keySet()) {
		 * 
		 * System.out.println(groups.get(s)); }
		 */

		Collection<String> sendFrom = myGroup.getMembers();
		Collection<String> sendTo = myGroup.getMembers();

		MulticastMessage multicastMsg = new MulticastMessage(myGroup
				.getGroupClock().getNewTimeStamp(), myGroup.getName());

		for (String s : sendFrom) {

			for (String s1 : sendTo) {
				Message msg = new TimeStampedMessage(s1, message.getKind(),
						message.getData().toString());
				msg.setSource(s);
				msg.setMulticastMsg(multicastMsg);
				try {
					messagepasser.send(msg);
					System.out.println("Sent from " + msg.getSource() + "--> "
							+ msg.getDest());
				} catch (IOException e) {
					System.out.println("Couldn't send msg to " + s1 + "from"
							+ s);
				}
			}
		}

	}

}
