
package net.craftstars.general.security;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.bukkit.migration.PermissionsResolverManager;
import com.sk89q.bukkit.migration.PermissionsResolverServerListener;
import net.craftstars.general.General;
import net.craftstars.general.security.PermissionsHandler;

public class WorldEditPermissionsHandler implements PermissionsHandler {
    private PermissionsResolverManager perms;
    private PermissionsResolverServerListener permsListener;
    private final String version;
    private boolean wasLoaded = false;
    
    public WorldEditPermissionsHandler() {
        Plugin test = General.plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        if(test != null) {
            this.wasLoaded = true;
            this.version = test.getDescription().getVersion();
        } else this.version = "0.0";
    }

    private void loadPermissions() {
        perms = new PermissionsResolverManager(General.plugin.getConfiguration(), General.plugin.getServer(),
                General.plugin.getDescription().getName(), General.logger.getInternal());
        permsListener = new PermissionsResolverServerListener(perms); 
        permsListener.register(General.plugin);
        perms.load();
    }

    @Override
    public boolean hasPermission(Player who, String what) {
        if(this.perms == null) loadPermissions();
        return perms.hasPermission(who.getName(), what);
    }

    @Override
    public boolean inGroup(Player who, String which) {
        if(which == ".isop") return who.isOp();
        if(this.perms == null) loadPermissions();
        return perms.inGroup(who.getName(), which);
    }

    @Override
    public boolean wasLoaded() {
        return wasLoaded;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getName() {
        return "WorldEdit";
    }
}
