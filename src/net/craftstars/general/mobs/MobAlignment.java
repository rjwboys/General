package net.craftstars.general.mobs;

import org.bukkit.entity.Player;

import net.craftstars.general.General;

public enum MobAlignment {
    FRIENDLY, NEUTRAL, ENEMY;
    // TODO: Wolves and spiders complicate this a bit.
    
    public boolean hasPermission(Player who) {
        String x = this.toString().toLowerCase();
        return General.plugin.permissions.hasPermission(who, "general.mobspawn." + x);
    }
}
