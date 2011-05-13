
package net.craftstars.general.teleport;

import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.entity.Player;

public enum TargetType {
	SELF("yourself"), OTHER("others"), MOB("mobs");
	private String msg;
	
	private TargetType(String message) {
		msg = message;
	}
	
	public boolean hasPermission(Player who) {
		if(who.isOp()) return true;
		if(Toolbox.hasPermission(who, getPermission())) return true;
		Messaging.lacksPermission(who, "teleport " + msg);
		return false;
	}
	
	public String getPermission() {
		return "general.teleport." + this.toString().toLowerCase();
	}
}
