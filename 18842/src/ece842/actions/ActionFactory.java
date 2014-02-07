package ece842.actions;

import java.util.HashMap;
import java.util.Map;

public class ActionFactory {

	
	private static final Map<String, Action> actionMap = new HashMap<String, Action>();
    static {
        actionMap.put("drop", new DropAction());
        actionMap.put("duplicate", new DuplicateAction());
        actionMap.put("delay", new DelayAction());
        actionMap.put("deafult", new DefaultAction());
    }
    
    public static Action getActionExecutor(String actionName){
    	if(actionMap.containsKey(actionName)){
    		return actionMap.get(actionName);
    	}
    	return new DefaultAction();
    }
}
