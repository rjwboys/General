package net.craftstars.general.security;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import net.craftstars.general.General;
import net.craftstars.general.security.PermissionsHandler;

public class PermissionsPermissionsHandler implements PermissionsHandler {
    private PermissionHandler permissions = null;
    private boolean wasLoaded = false;
    
    public PermissionsPermissionsHandler() {
        Plugin test = General.plugin.getServer().getPluginManager().getPlugin("Permissions");
        PluginDescriptionFile pdfFile = General.plugin.getDescription();
            
        if (this.permissions == null) {
            if (test!= null) {
                General.plugin.getServer().getPluginManager().enablePlugin(test);
                this.permissions = ((Permissions) test).getHandler();
                this.wasLoaded = true;
            }
            else {
                General.logger.info("Permissions not detected; falling back to [Basic] permissions.");
            }
        }
    }
    
    public boolean hasPermission(Player who, String what) {
        return this.permissions.has(who, what);
    }

    public boolean wasLoaded() {
        return wasLoaded;
    }

}
