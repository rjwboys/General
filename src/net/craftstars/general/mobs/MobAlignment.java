
package net.craftstars.general.mobs;

import org.bukkit.command.CommandSender;

import net.craftstars.general.util.Toolbox;

public enum MobAlignment {
	FRIENDLY, NEUTRAL, ENEMY;
	
	public boolean hasPermission(CommandSender sender) {
		String x = this.toString().toLowerCase();
		return Toolbox.hasPermission(sender, "general.mobspawn." + x);
	}
}
