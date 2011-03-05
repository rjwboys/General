
package net.craftstars.general.security;

import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.security.PermissionsHandler;

public class BasicPermissionsHandler implements PermissionsHandler {

    public boolean hasPermission(Player who, String what) {
        try {
            if(General.plugin.config.getNode("permissions").getList("ops-only").contains(what)) return who
                    .isOp();
            return true;
        } catch(NullPointerException ex) {
            return false;
        }
    }

    public boolean wasLoaded() {
        return true;
    }

    public boolean inGroup(Player who, String which) {
        if(which == ".isop") return who.isOp();
        return false;
    }

}
