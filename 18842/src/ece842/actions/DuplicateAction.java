package ece842.actions;

import java.util.ArrayList;
import java.util.List;

import ece842.core.Message;
import ece842.core.MessagePasser;

public class DuplicateAction extends DefaultAction {

	@Override
	public void executeSend(Message message, MessagePasser messagePasser) {
		super.executeSend(message, messagePasser);
		message.setDupe(true);
		super.executeSend(message, messagePasser);
	}
	
	@Override
	public List<Message> executeReceive(Message message, MessagePasser messagePasser) {
		List<Message> messages = new ArrayList<Message>();
		messages.add(message);
		messages.add(message);
		receiveDelayedMessages(messages, messagePasser);
		return messages;
	}
}
