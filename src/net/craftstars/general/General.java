
package net.craftstars.general;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;

import me.taylorkelly.help.Help;
import net.craftstars.general.command.generalCommand;
import net.craftstars.general.command.kitCommand;
import net.craftstars.general.money.EconomyBase;
import net.craftstars.general.security.BasicPermissionsHandler;
import net.craftstars.general.security.PermissionsHandler;
import net.craftstars.general.util.Items;
import net.craftstars.general.util.PluginLogger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class General extends JavaPlugin {
    private class PluginListener extends ServerListener {
        @Override
        public void onPluginEnabled(PluginEvent event) {
            if(!gotHelp && event.getPlugin() instanceof Help)
                setupHelp();
            else if (!gotRequestedPermissions)
                setupPermissions(false);
            else if (!gotRequestedEconomy)
                setupPermissions(false);
        }
    }

    public static General plugin = null;

    public static final boolean DEBUG = true;
    public static final String codename = "Sibelius";

    public static final PluginLogger logger = PluginLogger.getLogger("General", DEBUG); // NOTE: Was
    // private. Should be changed back? [celticminstrel]

    public Configuration config; // NOTE: This was private. Should it be changed back? [celticminstrel]
    public PermissionsHandler permissions;
    public EconomyBase economy;
    private boolean gotRequestedPermissions, gotHelp, gotRequestedEconomy;
    private HashMap<String,String> playersAway = new HashMap<String,String>();
    
    public boolean isAway(Player who) {
        return playersAway.containsKey(who.getName());
    }
    
    public void goAway(Player who, String reason) {
        playersAway.put(who.getName(), reason);
    }
    
    public void unAway(Player who) {
        playersAway.remove(who.getName());
    }
    
    public String whyAway(Player who) {
        if(isAway(who)) return playersAway.get(who.getName());
        return "";
    }

    public General() {
        if(plugin != null) General.logger.warn("Seems to have loaded twice for some reason.");
        plugin = this;
        logger.info("Loaded.");
    }

    //@Override
    public void onEnable() {
        logger.setPluginVersion(this.getDescription().getVersion());
        
        this.config = this.getConfiguration();
        this.loadConfiguration();

        Items.setup();
        //getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, new PluginListener(), Priority.Monitor, this);
        setupPermissions(true);
        setupEconomy();
        if(config.getBoolean("show-motd", true))
            getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, new PlayerListener(){
                @Override
                public void onPlayerJoin(PlayerEvent event) {
                    generalCommand.showMotD(event.getPlayer());
                }
            }, Priority.Normal, this);

        logger.info("[Codename: " + General.codename + "] Plugin successfully loaded!");
        
        setupHelp();
    }

    private void setupHelp() {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("Help");
        if (test != null) {
            Help helpPlugin = ((Help) test);
            // TODO: Some of these help messages are too long; need to shorten them.
            // TODO: Some of the help should be moved to /<cmd> help; spawn and teleport, in particular.
            ////////////////////////////--------------------------------------------------
            helpPlugin.registerCommand("playerlist|online", "Lists online players.", plugin,
                    "general.playerlist");
            helpPlugin.registerCommand("playerinfo|who ([player])",
                    "Displays information about a player.", plugin, "general.who");
            helpPlugin.registerCommand("time ([world])", "Displays the current time in [world].",
                    plugin, "general.time");
            helpPlugin.registerCommand("time [time] ([world])", "Sets the current time.", plugin,
                    "general.time.set");
            helpPlugin.registerCommand("time help", "Shows syntax for setting the time.", plugin,
                    "general.time.set");
            helpPlugin.registerCommand("give|i(tem) [item](:[variant]) ([amount]) ([player])",
                    "Gives [player] [amount] of [item].", plugin, "general.give");
            helpPlugin.registerCommand("getpos ([player])",
                    "Get the current position of [player].", plugin, "general.getpos");
            helpPlugin.registerCommand("compass", "Show your direction.", plugin, "general.getpos");
            helpPlugin.registerCommand("where|pos|coords ([player])",
                    "Show the location of [player]; less detailed form of /getpos.", plugin,
                    "general.getpos");
            helpPlugin.registerCommand("tell|msg|pm|whisper [player] [message]",
                    "Whisper to a player.", plugin, "general.tell");
            helpPlugin.registerCommand("spawn ([player])",
                    "Teleports [player] to the spawn location.", plugin, "general.spawn");
            helpPlugin.registerCommand("spawn ([world]) show",
                    "Displays the current spawn location in [world].", plugin, "general.spawn");
            helpPlugin.registerCommand("spawn set ([player])",
                    "Sets the spawn location in [player]'s world to [player]'s location.", plugin,
                    "general.spawn.set");
            helpPlugin.registerCommand("spawn ([world]) set [x] [y] [z]", ".", plugin,
                    "general.spawn.set");
            helpPlugin.registerCommand("setspawn ([player])",
                    "Sets the spawn location in [player]'s world to [player]'s location.", plugin,
                    "general.spawn.set");
            helpPlugin.registerCommand("setspawn ([world]) [x] [y] [z]",
                    "Sets the spawn location of [world]", plugin, "general.spawn.set");
            helpPlugin.registerCommand("teleport|tp [player]",
                    "Teleport to the location of [player].", plugin, "general.teleport");
            helpPlugin.registerCommand("teleport|tp [player] [to-player]",
                    "Teleports [player] to the location of [to-player]", plugin,
                    "general.teleport.other");
            helpPlugin.registerCommand("teleport|tp [player1],[player2],... [to-player]",
                    "Teleports several players to the location of [to-player].", plugin,
                    "general.teleport.other.mass");
            helpPlugin.registerCommand("teleport|tp * [player]",
                    "Teleports everyone to the location of [player]", plugin,
                    "general.teleport.other.mass");
            helpPlugin.registerCommand("teleport|tp [x] [y] [z]",
                    "Teleport to the specified coordinates", plugin, "general.teleport.coords");
            helpPlugin.registerCommand("s(ummon)|tphere|teleporthere [player]",
                    "Teleports a player to your location.", plugin, "general.teleport.other");
            // helpPlugin.registerCommand("s(ummon)|tphere|teleporthere [player1],[player2],...",
            // "", plugin, "general.teleport.other.mass");
            // helpPlugin.registerCommand("s(ummon)|tphere|teleporthere *", "", plugin,
            // "general.teleport.other.mass");
            helpPlugin.registerCommand("clear ([player])", "Clear's [player]'s inventory.", plugin,
                    "general.clear");
            helpPlugin.registerCommand("take [item](:[variant]) ([amount]) ([player])",
                    "Deletes something from [player]'s inventory.", plugin, "general.take");
            helpPlugin.registerCommand("heal ([player]) ([amount])",
                    "Heals [player] by [amount] hearts (0-10). If [amount] is omitted, full heal.",
                    plugin, "general.heal");
            helpPlugin.registerCommand("general reload", "Reloads the configuration files.",
                    plugin, "OP", "general.admin");
            helpPlugin.registerCommand("general die", "Kills the plugin.", plugin, "OP",
                    "general.admin");
            helpPlugin.registerCommand("general motd", "Displays the message of the day.", plugin);
            // helpPlugin.registerCommand("mob(spawn)", "Spawns a mob.", plugin, "general.mobspawn");
            helpPlugin.registerCommand("help General", "Help for the General plugin.", plugin, true);
            logger.info("[Help " + helpPlugin.getDescription().getVersion() + "] support enabled.");
            gotHelp = true;
        } else {
            logger.warn("[Help] isn't detected. No /help support; instead use /general help");
            gotHelp = false;
        }
    }
    
    private void setupEconomy() {
        gotRequestedEconomy = true;
        String econType = "None";
        try {
            try {
                String s = config.getNode("economy").getString("system");
                if(s != null) econType = s;
            } catch(Exception ex) {
                econType = "None";
            }
            if(econType.equalsIgnoreCase("None")) return;
            Class<? extends EconomyBase> clazz = this.getClass().getClassLoader().loadClass(
                    "net.craftstars.general.money." + econType + "EconomyHandler")
                    .asSubclass(EconomyBase.class);
            economy = (EconomyBase) clazz.newInstance();
            if(!economy.wasLoaded()) {
                logger.info("[" + econType + "] not detected; economy support disabled.");
                gotRequestedEconomy = false;
            }
        } catch(Exception ex) {
            logger.error("There was a big problem loading economy system [" + econType
                    + "]! Please report this error!");
            ex.printStackTrace();
            gotRequestedEconomy = false;
        }
        logger.info(" Using [" + economy.getName() + " " + economy.getVersion() + "] for economy.");
    }

    private void setupPermissions(boolean firstTime) {
        if(permissions != null && !(permissions instanceof BasicPermissionsHandler)) return;
        String permType = "unknown";
        gotRequestedPermissions = true;
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
            if(firstTime && (permissions == null || !permissions.wasLoaded())) {
                logger.info("[" + permType + "] not detected; falling back to [Basic] permissions.");
                permissions = new BasicPermissionsHandler();
                gotRequestedPermissions = false;
            }
        } catch(Exception ex) {
            logger.error("There was a big problem loading permissions system [" + permType
                    + "]! Please report this error!");
            ex.printStackTrace();
            if(!firstTime)
                General.logger.error("Note: Using permissions [" + permissions.getName() + "]");
            gotRequestedPermissions = false;
        }
        logger.info(" Using [" + permissions.getName() + " " + permissions.getVersion() + "] for permissions.");
    }

    //@Override
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
                General.logger.info("Configuration file does not exist. Attempting to create default one...");
                InputStream defaultConfig = this.getClass().getResourceAsStream(File.separator + "config.yml");
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
            General.logger.warn("Could not read and/or write config.yml! Continuing with default values!", ex);
        }
        kitCommand.loadKits();
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
            logger.error("There was a big problem executing command [" + command.getName()
                    + "]! Please report this error!");
            String cmdStr = "Full command string: [" + commandLabel;
            for(String x : args) cmdStr += " " + x;
            cmdStr += ']';
            logger.error(cmdStr);
            ex.printStackTrace();
        }

        return false;
    }
}
