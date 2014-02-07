package ece842.configs;

import java.util.Map;

import ece842.core.Message;

public class Rule {

	private String action;
	private String source;
	private int sequenceNumber = -1;
	private String dest;
	private String kind;
	private String dupe;

	private static final String ACTION = "action";
	private static final String SOURCE = "src";
	private static final String DEST = "dest";
	private static final String KIND = "kind";
	private static final String SEQ_NO = "seqNum";
	private static final String DUPLICATE = "duplicate";

	public String getAction() {
		return action;
	}

	public Rule(Map<String, Object> parameters) {
		// Assuming every rule will have a action
		action = (String) parameters.get(ACTION);
		if (parameters.containsKey(DEST)) {
			dest = (String) parameters.get(DEST);
		}
		if (parameters.containsKey(SOURCE)) {
			source = (String) parameters.get(SOURCE);
		}
		if (parameters.containsKey(KIND)) {
			kind = (String) parameters.get(KIND);
		}

		if (parameters.containsKey(SEQ_NO)) {
			sequenceNumber = ((Integer) parameters.get(SEQ_NO)).intValue();
		}
		
		if (parameters.containsKey(DUPLICATE)) {
			dupe = parameters.get(DUPLICATE).toString();
		}
	}

	public boolean isSatisfied(Message message) {
		if ((source != null && !source.equalsIgnoreCase(message.getSource()))) {
			return false;
		}
		if (dest != null && !dest.equalsIgnoreCase(message.getDest())) {
			return false;
		}
		if (kind != null && !kind.equalsIgnoreCase(message.getKind())) {
			return false;
		}
		if (sequenceNumber != -1
				&& sequenceNumber != message.getSequenceNumber()) {
			return false;
		}
		if (dupe != null && !dupe.equalsIgnoreCase(message.getDupe().toString())) {
			return false;
		}
		return true;
	}
}
