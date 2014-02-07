package ece842.actions;

import java.util.List;

import ece842.core.Message;
import ece842.core.MessagePasser;

public interface Action {

	public void executeSend(Message message, MessagePasser messagePasser);
	public List<Message> executeReceive(Message message, MessagePasser messagePasser);
}
