
package net.craftstars.general;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

import me.taylorkelly.help.Help;
import net.craftstars.general.security.PermissionsHandler;
import net.craftstars.general.util.Items;
import net.craftstars.general.util.PluginLogger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class General extends JavaPlugin {
    public static General plugin = null;

    public static final boolean DEBUG = true;
    public static final String codename = "Wagner";

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
        
        setupHelp();
    }

    private void setupHelp() {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("Help");
        if (test != null) {
            Help helpPlugin = ((Help) test);
            // Registers a main command. But all users will be able to see it
            //helpPlugin.registerCommand("home help", "Help for all MyHome commands", plugin, true);
            // Registers a secondary command to our plugin (MyHome), but the user has to have the permission to see it
            //helpPlugin.registerCommand("home", "Go home young chap!", plugin, "myhome.home.basic.home");
            // TODO: Some of these help messages are too long; need to shorten them.
            // TODO: Some of the help should be moved to /<cmd> help; spawn and teleport, in particular.
            helpPlugin.registerCommand("playerlist|online", "Lists online players.", plugin, "general.playerlist");
            helpPlugin.registerCommand("playerinfo|who ([player])", "Displays information about a player.", plugin, "general.who");
            helpPlugin.registerCommand("time ([world])", "Displays the current time in [world].", plugin, "general.time");
            helpPlugin.registerCommand("time [time] ([world])", "Sets the current time.", plugin, "general.time.set");
            helpPlugin.registerCommand("time help", "Shows syntax for setting the time.", plugin, "general.time.set");
            helpPlugin.registerCommand("give|i(tem) ([player]) [item](:[variant]) ([amount])", "Gives [player] [amount] of [item].", plugin, "general.give");
            helpPlugin.registerCommand("getpos ([player])", "Get the current position of [player].", plugin, "general.getpos");
            helpPlugin.registerCommand("compass", "Show your direction.", plugin, "general.getpos");
            helpPlugin.registerCommand("where|pos|coords ([player])", "Show the location of [player]; less detailed form of /getpos.", plugin, "general.getpos");
            helpPlugin.registerCommand("tell|msg|pm|whisper [player] [message]", "Whisper to a player.", plugin, "general.tell");
            helpPlugin.registerCommand("spawn ([player])", "Teleports [player] to the spawn location.", plugin, "general.spawn");
            helpPlugin.registerCommand("spawn ([world]) show", "Displays the current spawn location in [world].", plugin, "general.spawn");
            helpPlugin.registerCommand("spawn set ([player])", "Sets the spawn location in [player]'s world to [player]'s location.", plugin, "general.spawn.set");
            helpPlugin.registerCommand("spawn ([world]) set [x] [y] [z]", ".", plugin, "general.spawn.set");
            helpPlugin.registerCommand("setspawn ([player])", "Sets the spawn location in [player]'s world to [player]'s location.", plugin, "general.spawn.set");
            helpPlugin.registerCommand("setspawn ([world]) [x] [y] [z]", "Sets the spawn location of [world]", plugin, "general.spawn.set");
            helpPlugin.registerCommand("teleport|tp [player]", "Teleport to the location of [player].", plugin, "general.teleport");
            helpPlugin.registerCommand("teleport|tp [player] [to-player]", "Teleports [player] to the location of [to-player]", plugin, "general.teleport.other");
            helpPlugin.registerCommand("teleport|tp [player1],[player2],... [to-player]", "Teleports several players to the location of [to-player].", plugin, "general.teleport.other.mass");
            helpPlugin.registerCommand("teleport|tp * [player]", "Teleports everyone to the location of [player]", plugin, "general.teleport.other.mass");
            helpPlugin.registerCommand("teleport|tp [x] [y] [z]", "Teleport to the specified coordinates", plugin, "general.teleport.coords");
            helpPlugin.registerCommand("s(ummon)|tphere|teleporthere [player]", "Teleports a player to your location.", plugin, "general.teleport.other");
            //helpPlugin.registerCommand("s(ummon)|tphere|teleporthere [player1],[player2],...", "", plugin, "general.teleport.other.mass");
            //helpPlugin.registerCommand("s(ummon)|tphere|teleporthere *", "", plugin, "general.teleport.other.mass");
            helpPlugin.registerCommand("clear ([player])", "Clear's [player]'s inventory.", plugin, "general.clear");
            helpPlugin.registerCommand("take ([player]) [item](:[variant]) ([amount])", "Deletes something from [player]'s inventory.", plugin, "general.take");
            helpPlugin.registerCommand("heal ([player]) ([amount])", "Heals [player] by [amount] hearts (0-10). If [amount] is omitted, full heal.", plugin, "general.heal");
            helpPlugin.registerCommand("general reload", "Reloads the configuration files.", plugin, "OP", "general.admin");
            helpPlugin.registerCommand("general die", "Kills the plugin.", plugin, "OP", "general.admin");
            helpPlugin.registerCommand("general help", "A brief help summary.", plugin);
            helpPlugin.registerCommand("general motd", "Displays the message of the day.", plugin);
            //helpPlugin.registerCommand("mspawn", "Spawns a mob.", plugin, "general.mspawn");
            helpPlugin.registerCommand("help General", "Help for the General plugin.", plugin, true);
            logger.info("[Help "+helpPlugin.getDescription().getVersion()+"] support enabled.");
        } else {
            logger.warn("[Help] isn't detected. No /help support; instead use /general help");
        }
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
            Class<? extends CommandBase> clazz = this.getClass().getClassLoader().loadClass(
                    "net.craftstars.general.command." + command.getName() + "Command").asSubclass(
                    CommandBase.class);
            CommandBase commandInstance = (CommandBase) clazz.newInstance();
            return commandInstance.runCommand(this, sender, command, commandLabel, args);
        } catch(Exception ex) {
            General.logger.error("There was a big problem executing command [" + command.getName()
                    + "]! Please report this error!");
            ex.printStackTrace();
        }

        return false;
    }
}
