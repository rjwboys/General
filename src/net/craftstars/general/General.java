
package net.craftstars.general;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;

import me.taylorkelly.help.Help;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.items.Items;
import net.craftstars.general.items.Kits;
import net.craftstars.general.money.EconomyBase;
import net.craftstars.general.security.BasicPermissionsHandler;
import net.craftstars.general.security.PermissionsHandler;
import net.craftstars.general.util.HelpHandler;
import net.craftstars.general.util.MessageOfTheDay;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.PluginLogger;
import net.craftstars.general.util.Time;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class General extends JavaPlugin {
    private class PluginListener extends ServerListener {
        @Override
        public void onPluginEnable(PluginEnableEvent event) {
            if(!HelpHandler.gotHelp && event.getPlugin() instanceof Help)
                HelpHandler.setup();
            else if (!gotRequestedPermissions)
                setupPermissions(false);
            else if (!gotRequestedEconomy)
                setupPermissions(false);
        }
    }

    public static General plugin = null;

    public static final boolean DEBUG = true;
    public static final String codename = "Dvorak";

    public static final PluginLogger logger = PluginLogger.getLogger("General", DEBUG); // NOTE: Was
    // private. Should be changed back? [celticminstrel]

    public Configuration config; // NOTE: This was private. Should it be changed back? [celticminstrel]
    public PermissionsHandler permissions;
    public EconomyBase economy;
    private boolean gotRequestedPermissions, gotRequestedEconomy;

    private HashMap<String,String> playersAway = new HashMap<String,String>();
    private String tagFormat;
    
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
    }
    
    @Override
    public void onLoad() {
        logger.info("Loaded.");
    }
    
    PlayerListener pl = new PlayerListener(){
        @Override
        public void onPlayerJoin(PlayerJoinEvent event) {
            MessageOfTheDay.showMotD(event.getPlayer());
        }
        
        @Override
        public void onPlayerChat(PlayerChatEvent event) {
            String tag = event.getMessage().split("\\s+")[0];
            for(String who : playersAway.keySet()) {
                if(tag.equalsIgnoreCase(tagFormat.replace("name", who))) {
                    Messaging.send(event.getPlayer(), "&c" + who + " is away: " + playersAway.get(who));
                    break;
                }
            }
        }
    };

    @Override
    public void onEnable() {
        logger.setPluginVersion(this.getDescription().getVersion());
        
        this.config = this.getConfiguration();
        this.loadConfiguration();
        
        Time.setup();
        Items.setup();
        Kits.loadKits();
        //getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, new PluginListener(), Priority.Monitor, this);
        setupPermissions(true);
        setupEconomy();
        if(config.getBoolean("show-motd", true))
            getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, pl, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, pl, Priority.Monitor, this);
        tagFormat = config.getString("tag-fmt", "name:");
        
        logger.info("[Codename: " + General.codename + "] Plugin successfully loaded!");
        
        HelpHandler.setup();
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

    @Override
    public void onDisable() {
        Items.save();
        // config.save();
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
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel,
            String[] args) {
        String cmdStr = commandLabel;
        for(String x : args) cmdStr += " " + x;
        try {
            boolean result;
            if(args.length > 0 && args[0].equalsIgnoreCase("help") && HelpHandler.hasEntry(command.getName()))
                result = HelpHandler.displayEntry(sender, command.getName());
            else {
                Class<? extends CommandBase> clazz = this.getClass().getClassLoader().loadClass(
                        "net.craftstars.general.command." + command.getName() + "Command").asSubclass(
                        CommandBase.class);
                CommandBase commandInstance = (CommandBase) clazz.newInstance();
                result = commandInstance.runCommand(this, sender, command, commandLabel, args);
            }
            if(config.getBoolean("log-commands", false)) {
                String name = "CONSOLE";
                if(sender instanceof Player) name = ((Player) sender).getName();
                logger.info(name + " used command: " + cmdStr);
            }
            return result;
        } catch(Exception ex) {
            logger.error("There was a big problem executing command [" + command.getName()
                    + "]! Please report this error!");
            String err = "Full command string: [" + cmdStr + "]";
            logger.error(err);
            ex.printStackTrace();
            return false;
        }
    }
}
