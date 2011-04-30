
package net.craftstars.general.teleport;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;

import org.bukkit.entity.Player;

public enum TargetType {
	SELF("yourself"), OTHER("others"), MOB("mobs");
	private String msg;
	
	private TargetType(String message) {
		msg = message;
	}
	
	public boolean hasPermission(Player who) {
		if(who.isOp()) return true;
		if(General.plugin.permissions.hasPermission(who, getPermission())) return true;
		Messaging.send(who, "&cYou don't have permission to teleport " + msg + ".");
		return false;
	}
	
	public String getPermission() {
		return "general.teleport." + this.toString().toLowerCase();
	}
}
