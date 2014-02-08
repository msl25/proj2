package ece842.configs;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Group {
	private String name;
	private Set<String> members;
	
	public Group(String n) {
		this.name = n;
		this.members = new HashSet<String>();
	}
	
	public void addMember(String member) {
		this.members.add(member);
	}
	
	public boolean isMember(String member) {
		return this.members.contains(member);
	}
	
	public Collection<String> getMembers() {
		return this.members;
	}
	
	@Override
	public String toString() {
		String result = "Group [" + this.name + ": ";
		boolean first = true;
		for(String m : this.members) {
			if(first) {
				first = false;
			} else {
				result += ", ";
			}
			result += m;
		}
		result = result + "]";
		return result;
	}
}
