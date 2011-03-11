
package net.craftstars.general.security;

import org.bukkit.entity.Player;

public interface PermissionsHandler {
    boolean hasPermission(Player who, String what);
    // TODO: Perhaps add one or two other functions, like "inGroup(Player who,String group)" or
    // "givePermission(Player,String)"
    
    /** @return Whether the plugin was successfully loaded. */
    boolean wasLoaded();
    
    boolean inGroup(Player who, String which);

    String getVersion();
    
    String getName();
}
