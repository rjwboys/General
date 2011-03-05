
package net.craftstars.general.security;

import org.bukkit.entity.Player;

public interface PermissionsHandler {
    public boolean hasPermission(Player who, String what);
    // TODO: Perhaps add one or two other functions, like "inGroup(Player who,String group)" or
    // "givePermission(Player,String)"
    
    /** @return Whether the plugin was successfully loaded. */
    public boolean wasLoaded();
    
    public boolean inGroup(Player who, String which);
}
