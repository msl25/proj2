package ece842.actions;

import java.util.Collections;
import java.util.List;

import ece842.core.Message;
import ece842.core.MessagePasser;

public class DelayAction extends DefaultAction{

	@Override
	public void executeSend(Message message, MessagePasser messagePasser) {
		messagePasser.addDelayedSendMessages(message);
	}
	
	@Override
	public List<Message> executeReceive(Message message, MessagePasser messagePasser) {
		messagePasser.addDelayedReceiveMessages(message);
		return Collections.emptyList();
	}
}
