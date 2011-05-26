
package net.craftstars.general.teleport;

import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.entity.Player;

public enum DestinationType {
	WORLD("other worlds"), PLAYER("other players"), COORDS("specific coordinates"), HOME("player homes", true),
	SPAWN("spawn", true), TARGET("targeted block", true), COMPASS("compass target", true);
	private String msg;
	private boolean spec;
	
	private DestinationType(String message) {
		this.msg = message;
		this.spec = false;
	}
	
	private DestinationType(String message, boolean special) {
		this.msg = message;
		this.spec = special;
	}
	
	public boolean hasPermission(Player who, String action, String base) {
		if(who.isOp()) return true;
		if(Toolbox.hasPermission(who, getPermission(base))) return true;
		Messaging.lacksPermission(who, getAction(action, false));
		return false;
	}
	
	public boolean hasOtherPermission(Player who, String action, String base) {
		if(who.isOp()) return true;
		if(Toolbox.hasPermission(who, getPermission(base + ".other"))) return true;
		Messaging.lacksPermission(who, getAction(action, true));
		return false;
	}
	
	public boolean isSpecial() {
		return spec;
	}
	
	public String getPermission(String base) {
		return base + ".to." + this.toString().toLowerCase();
	}
	
	public String getAction(String base,boolean other) {
		return base + " to " + (other ? "others' " : "") + this.msg;
	}
}