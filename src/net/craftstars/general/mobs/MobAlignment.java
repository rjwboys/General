
package net.craftstars.general.mobs;

import org.bukkit.entity.Player;

import net.craftstars.general.util.Toolbox;

public enum MobAlignment {
	FRIENDLY, NEUTRAL, ENEMY;
	// TODO: Wolves and spiders complicate this a bit.
	
	public boolean hasPermission(Player who) {
		String x = this.toString().toLowerCase();
		return Toolbox.hasPermission(who, "general.mobspawn." + x);
	}
}
