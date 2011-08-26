
package net.craftstars.general;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import net.craftstars.general.items.Items;
import net.craftstars.general.items.Kits;
import net.craftstars.general.mobs.MobType;
import net.craftstars.general.util.CommandHandler;
import net.craftstars.general.util.HelpHandler;
import net.craftstars.general.util.LanguageText;
import net.craftstars.general.util.MessageOfTheDay;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.PermissionsHandler;
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

import com.fernferret.allpay.AllPay;
import com.fernferret.allpay.GenericBank;

public class General extends JavaPlugin {
	public static General plugin = null;
	
	public static final boolean DEBUG = true;
	public static final String codename = "Webern";
	
	public static final PluginLogger logger = PluginLogger.getLogger("General", DEBUG);
	
	public Configuration config;
	public PermissionsHandler permissions;
	public GenericBank economy;
	private AllPay allpay;
	
	private HashMap<String, String> playersAway = new HashMap<String, String>();
	private HashMap<String,String> lastMessager = new HashMap<String, String>();
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
		MobType.setup();
		permissions = new PermissionsHandler();
		Kits.loadKits();
		allpay = new AllPay(this, "General [" + codename + "] ");
		economy = allpay.getEconPlugin();
	}

	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(PLAYER_JOIN, pl, Priority.Monitor, this);
		pm.registerEvent(PLAYER_CHAT, pl, Priority.Monitor, this);
		pm.registerEvent(PLAYER_LOGIN, pl, Priority.Monitor, this);
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
			out.write(lines.nextLine() + "\n");
		out.flush();
		out.close();
		defaultConfig.close();
	}
}
