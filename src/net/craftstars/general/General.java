
package net.craftstars.general;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

import net.craftstars.general.command.GeneralCommand;
import net.craftstars.general.security.PermissionsHandler;
import net.craftstars.general.util.Items;
import net.craftstars.general.util.PluginLogger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class General extends JavaPlugin {
    public static General plugin = null;

    public static final boolean DEBUG = true;
    public static final String codename = "Bach";

    public static final PluginLogger logger = PluginLogger.getLogger("General", DEBUG); // NOTE: Was
    // private.
    // Should be
    // changed
    // back?
    // [celticminstrel]

    public Configuration config; // NOTE: This was private. Should it be changed back?
    // [celticminstrel]
    public PermissionsHandler permissions;

    public General() {
        if(plugin != null) General.logger.warn("Seems to have loaded twice for some reason.");
        plugin = this;
        General.logger.info("Loaded.");
    }

    @Override
    public void onEnable() {
        General.logger.setPluginVersion(this.getDescription().getVersion());

        this.config = this.getConfiguration();
        this.loadConfiguration();

        Items.setup();

        String permType = setupPermissions();

        General.logger.info("[Codename: " + General.codename
                + "] Plugin successfully loaded! Using [" + permType + "] permissions.");
    }

    private String setupPermissions() {
        String permType = "unknown";
        try {
            try {
                permType = config.getNode("permissions").getString("system");
                permType.isEmpty(); // To trigger NPE if applicable. <_< Why? Avoiding duplication.
                // [celticminstrel]
            } catch(Exception ex) {
                permType = "Basic";
            }
            Class<? extends PermissionsHandler> clazz = this.getClass().getClassLoader().loadClass(
                    "net.craftstars.general.security." + permType + "PermissionsHandler")
                    .asSubclass(PermissionsHandler.class);
            permissions = (PermissionsHandler) clazz.newInstance();
            if(permissions == null || !permissions.wasLoaded()) {
                General.logger.info("[" + permType
                        + "] permissions not detected; falling back to [Basic] permissions.");
                clazz = this.getClass().getClassLoader().loadClass(
                        "net.craftstars.general.security.BasicPermissionsHandler").asSubclass(
                        PermissionsHandler.class);
                permissions = (PermissionsHandler) clazz.newInstance();
            }
        } catch(Exception ex) {
            General.logger.error("There was a big problem loading permissions system [" + permType
                    + "]! Please report this error!");
            ex.printStackTrace();
        }
        if(permissions != null) permType = permType + " " + permissions.getVersion();
        return permType;
    }

    @Override
    public void onDisable() {
        General.logger.info("Plugin disabled!");
    }

    private void loadConfiguration() {
        this.config.load();

        try {
            File dataFolder = this.getDataFolder();
            if(!dataFolder.exists()) dataFolder.mkdirs();
            File configFile = new File(dataFolder, "config.yml");

            if(!configFile.exists()) {
                General.logger
                        .info("Configuration file does not exist. Attempting to create default one...");
                InputStream defaultConfig = this.getClass().getResourceAsStream(
                        File.separator + "config.yml");
                FileWriter out = new FileWriter(configFile);
                for(int i = 0; (i = defaultConfig.read()) > 0;)
                    out.write(i);
                out.flush();
                out.close();
                defaultConfig.close();
                this.config.load();
                General.logger.info("Default configuration created successfully! You can now "
                        + "stop the server and edit plugins/General/config.yml.");
            }
        } catch(Exception ex) {
            General.logger.warn(
                    "Could not read and/or write config.yml! Continuing with default values!", ex);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel,
            String[] args) {
        try {
            Class<? extends GeneralCommand> clazz = this.getClass().getClassLoader().loadClass(
                    "net.craftstars.general.command." + command.getName() + "Command").asSubclass(
                    GeneralCommand.class);
            GeneralCommand commandInstance = (GeneralCommand) clazz.newInstance();
            return commandInstance.runCommand(this, sender, command, commandLabel, args);
        } catch(Exception ex) {
            General.logger.error("There was a big problem executing command [" + command.getName()
                    + "]! Please report this error!");
            ex.printStackTrace();
        }

        return false;
    }
}
