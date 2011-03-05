
package net.craftstars.general.security;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.bukkit.migration.PermissionsResolverManager;
import com.sk89q.bukkit.migration.PermissionsResolverServerListener;

import net.craftstars.general.General;
import net.craftstars.general.security.PermissionsHandler;

public class WorldEditPermissionsHandler implements PermissionsHandler {
    private PermissionsResolverManager perms;
    private final String version;
    private boolean wasLoaded = false;
    
    public WorldEditPermissionsHandler() {
        Plugin test = General.plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        if(test == null) this.version = "0.0";
        else {
            perms = new PermissionsResolverManager(General.plugin.getConfiguration(), General.plugin.getServer(),
                    General.plugin.getDescription().getName(), General.logger.getInternal());
            (new PermissionsResolverServerListener(perms)).register(General.plugin);
            this.wasLoaded = true;
            this.version = test.getDescription().getVersion();
        }
    }
    
    public boolean hasPermission(Player who, String what) {
        return perms.hasPermission(who.getName(), what);
    }

    public boolean inGroup(Player who, String which) {
        if(which == ".isop") return who.isOp();
        return perms.inGroup(who.getName(), which);
    }

    public boolean wasLoaded() {
        return wasLoaded;
    }
    
    public String getVersion() {
        return version;
    }
}
