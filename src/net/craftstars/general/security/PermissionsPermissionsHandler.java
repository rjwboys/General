
package net.craftstars.general.security;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import net.craftstars.general.General;
import net.craftstars.general.security.PermissionsHandler;

public class PermissionsPermissionsHandler implements PermissionsHandler {
    private PermissionHandler permissions = null;
    private boolean wasLoaded = false;
    private final String version;

    public PermissionsPermissionsHandler() {
        Plugin test = General.plugin.getServer().getPluginManager().getPlugin("Permissions");

        if(test != null) {
            if(this.permissions == null) {
                General.plugin.getServer().getPluginManager().enablePlugin(test);
                this.permissions = ((Permissions) test).getHandler();
                this.wasLoaded = true;
            }
            this.version = test.getDescription().getVersion();
        } else this.version = "0.0";
    }

    public boolean hasPermission(Player who, String what) {
        return this.permissions.has(who, what);
    }

    public boolean wasLoaded() {
        return wasLoaded;
    }

    public boolean inGroup(Player who, String which) {
        if(which == ".isop") return who.isOp();
        return this.permissions.inGroup(who.getWorld().getName(), who.getName(), which);
    }

    public String getVersion() {
        return version;
    }
}
