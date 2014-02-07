package ece842.actions;

import java.util.Collections;
import java.util.List;

import ece842.core.Message;
import ece842.core.MessagePasser;

public class DropAction implements Action {

	@Override
	public void executeSend(Message message, MessagePasser messagePasser) {
		// Do nothing since packet needs to be dropped

	}

	@Override
	public List<Message> executeReceive(Message message,
			MessagePasser messagePasser) {
		return Collections.emptyList();
	}

}
