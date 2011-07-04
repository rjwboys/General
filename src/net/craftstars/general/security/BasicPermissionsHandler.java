
package net.craftstars.general.security;

import java.util.List;

import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.security.PermissionsHandler;
import net.craftstars.general.util.Messaging;

public class BasicPermissionsHandler implements PermissionsHandler {
	
	public boolean hasPermission(Player who, String what) {
		try {
			List<String> permsList = General.plugin.config.getStringList("permissions.ops-only", null);
			if(permsList.contains(what)) return who.isOp();
			while(what.matches(".*\\..+$")) {
				if(permsList.contains(what + ".*")) return who.isOp();
				what = what.substring(0, what.lastIndexOf('.'));
			}
			return true;
		} catch(NullPointerException ex) {
			Messaging.send(who, "&cError checking permissions.");
			return false;
		}
	}
	
	@Override
	public boolean wasLoaded() {
		return true;
	}
	
	@Override
	public boolean inGroup(Player who, String which) {
		if(which == ".isop") return who.isOp();
		return false;
	}
	
	@Override
	public String getVersion() {
		return "(isOp)";
	}
	
	@Override
	public String getName() {
		return "Basic";
	}
	
}
