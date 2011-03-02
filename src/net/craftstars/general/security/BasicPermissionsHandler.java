package net.craftstars.general.security;

import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.security.PermissionsHandler;

public class BasicPermissionsHandler implements
		PermissionsHandler {

	public boolean hasPermission(Player who, String what) {
		if(General.plugin.config.getList("permissions.ops-only").contains(what))
			return who.isOp();
		return true;
	}

}
