package ece842.core;

import java.util.Comparator;

public class TimeStampedMessageComparator implements Comparator<TimeStampedMessage> {
    @Override
	public int compare(TimeStampedMessage tsm1, TimeStampedMessage tsm2) {
    	if (tsm1.getTimestamp().timeStamp.containsKey("logical")) {
    		return tsm1.getTimestamp().timeStamp.get("logical") - tsm2.getTimestamp().timeStamp.get("logical");
    	} else {
    		boolean happenBefore = true;
    		boolean happenAfter = true;
    		
    		// happenBefore
    		for (String id : tsm1.getTimestamp().timeStamp.keySet()) {
    			if (tsm1.getTimestamp().timeStamp.get(id) > tsm2.getTimestamp().timeStamp.get(id)) {
    				happenBefore = false;
    			}
    			if (tsm1.getTimestamp().timeStamp.get(id) < tsm2.getTimestamp().timeStamp.get(id)) {
    				happenAfter = false;
    			}
    		}
    		if (happenBefore == true)
    			return -1;
    		else if (happenAfter == true)
    			return 1;
    		
    		return 0;
    	}
    	
    }
}