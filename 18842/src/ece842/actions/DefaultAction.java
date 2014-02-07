package ece842.actions;

import java.util.ArrayList;
import java.util.List;

import ece842.core.Message;
import ece842.core.MessagePasser;

public class DefaultAction implements Action {

	@Override
	public void executeSend(Message message, MessagePasser messagePasser) {
		messagePasser.sendMessage(message);
		sendDelayedMessages(messagePasser);
	}

	public void sendDelayedMessages(MessagePasser messagePasser){
		if(!messagePasser.getDelayedSendMessages().isEmpty()){
			for(Message delayedMessage: messagePasser.getDelayedSendMessages()){
				messagePasser.sendMessage(delayedMessage);
			}
			messagePasser.clearDelayedSendMessages();
		}
	}
	
	@Override
	public List<Message> executeReceive(Message message, MessagePasser messagePasser) {
		List<Message> messages = new ArrayList<Message>();
		messages.add(message);
		receiveDelayedMessages(messages, messagePasser);
		return messages;
	}
	
	public void receiveDelayedMessages(List<Message> messages, MessagePasser messagePasser){
		if(!messagePasser.getDelayedReceiveMessages().isEmpty()){
			for(Message delayedMessage: messagePasser.getDelayedReceiveMessages()){
				messages.add(delayedMessage);
			}
			messagePasser.clearDelayedReceiveMessages();
		}
	}
}
