package net.craftstars.general.items;

import org.bukkit.entity.Player;

public class GotKit {
	private String who;
	private String which;
	private int id;
	
	@SuppressWarnings("hiding")
	public GotKit(Player who, Kit which) {
		this.who = who.getName();
		this.which = which.getName();
		this.id = who.getEntityId();
	}
	
	@Override
	public int hashCode() {
		int items = which.hashCode();
		// return (id << 16) | (item & 0xFFFF);
		return id ^ items;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof GotKit) {
			GotKit other = (GotKit) obj;
			if(this.which.equals(other.which) && this.who.equals(other.who)) return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "(" + who + "[" + id + "], " + which + ")";
	}
}