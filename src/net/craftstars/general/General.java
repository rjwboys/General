
package net.craftstars.general;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import net.craftstars.general.items.Items;
import net.craftstars.general.items.Kits;
import net.craftstars.general.money.EconomyBase;
import net.craftstars.general.security.BasicPermissionsHandler;
import net.craftstars.general.security.PermissionsHandler;
import net.craftstars.general.util.CommandHandler;
import net.craftstars.general.util.HelpHandler;
import net.craftstars.general.util.LanguageText;
import net.craftstars.general.util.MessageOfTheDay;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.PluginLogger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import static org.bukkit.event.Event.Type.*;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class General extends JavaPlugin {
	public static General plugin = null;
	
	public static final boolean DEBUG = true;
	public static final String codename = "Schoenberg";
	
	public static final PluginLogger logger = PluginLogger.getLogger("General", DEBUG);
	
	public Configuration config;
	public PermissionsHandler permissions;
	public EconomyBase economy;
	@SuppressWarnings("unused")
	private boolean gotRequestedPermissions, gotRequestedEconomy;
	
	private HashMap<String, String> playersAway = new HashMap<String, String>();
	private HashMap<String,String> lastMessager = new HashMap<String,String>();
	private String tagFormat;
	
	public boolean isAway(Player who) {
		return playersAway.containsKey(who.getName());
	}
	
	public void goAway(Player who, String reason) {
		playersAway.put(who.getName(), reason);
		if(Option.AWAY_SLEEP.get())
			who.setSleepingIgnored(true);
	}
	
	public void unAway(Player who) {
		playersAway.remove(who.getName());
		if(Option.AWAY_SLEEP.get())
			who.setSleepingIgnored(false);
	}
	
	public String whyAway(Player who) {
		if(isAway(who)) return playersAway.get(who.getName());
		return "";
	}
	
	public void hasMessaged(String from, String to) {
		lastMessager.put(from, to);
	}
	
	public String lastMessaged(String to) {
		return lastMessager.get(to);
	}
	
	PlayerListener pl = new PlayerListener() {
		@Override
		public void onPlayerJoin(PlayerJoinEvent event) {
			if(!Option.SHOW_MOTD.get()) return;
			MessageOfTheDay.showMotD(event.getPlayer());
		}
		
		@Override
		public void onPlayerChat(PlayerChatEvent event) {
			String tag = event.getMessage().split("\\s+")[0];
			for(String who : playersAway.keySet()) {
				if(tag.equalsIgnoreCase(tagFormat.replace("name", who))) {
					Messaging.send(event.getPlayer(), LanguageText.AWAY_BRIEF.value("name", who,
						"reason", playersAway.get(who)));
					break;
				}
			}
		}
		
		@Override
		public void onPlayerLogin(PlayerLoginEvent event) {
			lastMessager.remove(event.getPlayer().getName());
		}
	};
	
	@Override
	public void onEnable() {
		boolean alreadyLoaded = false;
		if(plugin != null) {
			General.logger.warn(LanguageText.LOG_TWICE.value());
			alreadyLoaded = true;
		}
		plugin = this;
		logger.setPluginVersion(this.getDescription().getVersion());
		loadAllConfigs();
		logger.info("[Codename: " + General.codename + "] " + LanguageText.LOG_SUCCESS.value());
		if(alreadyLoaded) return;
		CommandHandler.setup(config);
		HelpHandler.setup();
		registerEvents();
	}

	public void loadAllConfigs() {
		// The load order here is very delicate, as LanguageText is used nearly everywhere
		// and Messaging.load uses Option.
		this.config = this.getConfiguration();
		this.loadConfiguration();
		Option.setConfiguration(config);
		Messaging.load();
		
		Items.setup();
		setupPermissions(true);
		Kits.loadKits();
		setupEconomy();
	}

	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(PLAYER_JOIN, pl, Priority.Monitor, this);
		pm.registerEvent(PLAYER_CHAT, pl, Priority.Monitor, this);
		pm.registerEvent(PLAYER_LOGIN, pl, Priority.Monitor, this);
	}
	
	private void setupEconomy() {
		gotRequestedEconomy = true;
		String econType = "None";
		try {
			econType = Option.ECONOMY_SYSTEM.get();
			if(econType.equalsIgnoreCase("None")) {
				logger.info(LanguageText.LOG_NO_ECONOMY.value());
				return;
			}
			Class<? extends EconomyBase> clazz =
					this.getClass()
							.getClassLoader()
							.loadClass("net.craftstars.general.money." + econType + "EconomyHandler")
							.asSubclass(EconomyBase.class);
			economy = clazz.newInstance();
			if(!economy.wasLoaded()) {
				logger.info(LanguageText.LOG_BAD_ECONOMY.value("econ", econType));
				gotRequestedEconomy = false;
			}
		} catch(Exception ex) {
			logger.error(LanguageText.LOG_ECONOMY_ERROR.value("econ", econType));
			ex.printStackTrace();
			gotRequestedEconomy = false;
		}
		if(economy != null)
			logger.info(LanguageText.LOG_ECONOMY_SUCCESS.value("econ", economy.getName(),
				"version", economy.getVersion()));
		else logger.info(LanguageText.LOG_ECONOMY_FAIL.value());
	}
	
	private void setupPermissions(boolean firstTime) {
		if(permissions != null && ! (permissions instanceof BasicPermissionsHandler)) return;
		String permType = "unknown";
		gotRequestedPermissions = true;
		try {
			permType = Option.PERMISSIONS_SYSTEM.get();
			Class<? extends PermissionsHandler> clazz = this.getClass().getClassLoader()
							.loadClass("net.craftstars.general.security." + permType + "PermissionsHandler")
							.asSubclass(PermissionsHandler.class);
			permissions = clazz.newInstance();
			if(firstTime && (permissions == null || !permissions.wasLoaded())) {
				logger.info(LanguageText.LOG_PERMISSIONS_MISSING.value("system", permType));
				permissions = new BasicPermissionsHandler();
				gotRequestedPermissions = false;
			}
		} catch(Exception ex) {
			logger.error(LanguageText.LOG_PERMISSIONS_FAIL.value("system", permType));
			ex.printStackTrace();
			if(!firstTime) General.logger.error(LanguageText.LOG_PERMISSIONS_NOTE.value("system", permissions.getName()));
			gotRequestedPermissions = false;
		}
		logger.info(LanguageText.LOG_PERMISSIONS_RESULT.value("system", permissions.getName(),
			"version", permissions.getVersion()));
	}
	
	@Override
	public void onDisable() {
		Items.save();
		Messaging.save();
		if(Option.AUTO_SAVE.get()) {
			Kits.save();
			config.save();
		}
		General.logger.info(LanguageText.LOG_DISABLED.value());
	}
	
	private void loadConfiguration() {
		try {
			File dataFolder = this.getDataFolder();
			if(!dataFolder.exists()) dataFolder.mkdirs();
			File configFile = new File(dataFolder, "config.yml");
			
			if(!configFile.exists()) {
				createDefaultConfig(configFile);
				General.logger.info(LanguageText.LOG_CONFIG_SUCCESS.value());
			}
		} catch(Exception ex) {
			General.logger.warn(LanguageText.LOG_CONFIG_ERROR.value("file", "config.yml"), ex);
		}
		this.config.load();
	}

	public void createDefaultConfig(File configFile) throws IOException {
		General.logger.info(LanguageText.LOG_CONFIG_DEFAULT.value("file", configFile.getName()));
		InputStream defaultConfig = this.getClass().getResourceAsStream(File.separator + configFile.getName());
		FileWriter out = new FileWriter(configFile);
		Scanner lines = new Scanner(defaultConfig);
		while(lines.hasNextLine())
			out.write(lines.nextLine());
		out.flush();
		out.close();
		defaultConfig.close();
	}
}
