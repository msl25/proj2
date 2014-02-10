package ece842.services;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import ece842.configs.Group;
import ece842.core.Message;
import ece842.core.MessageFlooder;
import ece842.core.MessagePasser;
import ece842.core.MulticastMessage;
import ece842.core.TimeStamp;
import ece842.core.TimeStampedMessage;

public class MulticastService {

	public void multicastSend(MessagePasser messagepasser, Message message,
			Group myGroup) {

		Collection<String> sendTo = myGroup.getMembers();
		String sender = messagepasser.getLocalId();
		myGroup.getGroupClock().incrementSendCount();
		TimeStamp ts = myGroup.getGroupClock().getTimeStamp().clone();
		ts.setTimeStampValue(sender, myGroup.getGroupClock().getSendCount());

		MulticastMessage multicastMsg = new MulticastMessage(ts,
				myGroup.getName(), sender);

		for (String s : sendTo) {
			Message msg = new TimeStampedMessage(s, message.getKind(), message
					.getData().toString());
			msg.setMulticastMsg(multicastMsg);
			try {
				messagepasser.send(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
