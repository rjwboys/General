
package net.craftstars.general;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import net.craftstars.general.items.Items;
import net.craftstars.general.items.Kits;
import net.craftstars.general.mobs.MobType;
import net.craftstars.general.text.HelpHandler;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.CommandHandler;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.PermissionsHandler;
import net.craftstars.general.util.PluginLogger;

import org.bukkit.event.Event.Priority;
import static org.bukkit.event.Event.Type.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.fernferret.allpay.AllPay;
import com.fernferret.allpay.GenericBank;
import com.fernferret.allpay.ItemBank;

public class General extends JavaPlugin {
	public static General plugin = null;
	public static final boolean DEBUG = true;
	public static final String codename = "Webern";
	public static final PluginLogger logger = PluginLogger.getLogger("General", DEBUG);
	private Configuration config;
	public static GenericBank economy;
	public static PlayerManager players = new PlayerManager();
	private AllPay allpay;
	
	@Override
	public void onEnable() {
		boolean alreadyLoaded = false;
		if(plugin != null) {
			General.logger.warn(LanguageText.LOG_TWICE.value());
			alreadyLoaded = true;
		}
		logger.setPluginVersion(this.getDescription().getVersion());
		loadAllConfigs();
		logger.info("[Codename: " + General.codename + "] " + LanguageText.LOG_SUCCESS.value());
		plugin = this;
		if(alreadyLoaded) return;
		CommandHandler.setup(config);
		HelpHandler.setup();
		registerEvents();
	}

	public void loadAllConfigs() {
		// The load order here is very delicate, as LanguageText is used nearly everywhere
		// and Messaging.load uses Option.
		LanguageText.setLanguage("en", getDataFolder(), "messages_en.yml"); // Make sure we have a default language!
		config = getConfiguration();
		loadConfiguration();
		Option.setConfiguration(config);
		Messaging.load();
		
		Items.setup();
		MobType.setup();
		Kits.load();
		if(plugin == null) PermissionsHandler.setup();
		else PermissionsHandler.refreshItemGroups();
		allpay = new AllPay(this, "General [" + codename + "] ");
		economy = allpay.loadEconPlugin();
		if(Option.NO_ECONOMY.get()) logger.info(LanguageText.LOG_NO_ECONOMY.value());
		else if(economy instanceof ItemBank && Option.ECONOMY_ITEM.get() <= 0)
			logger.warn(LanguageText.LOG_MISSING_ECONOMY.value());
	}

	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(PLAYER_JOIN, players, Priority.Monitor, this);
		pm.registerEvent(PLAYER_CHAT, players, Priority.Monitor, this);
		pm.registerEvent(PLAYER_LOGIN, players, Priority.Monitor, this);
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
		config.load();
	}

	public static void createDefaultConfig(File configFile) throws IOException {
		General.logger.info(LanguageText.LOG_CONFIG_DEFAULT.value("file", configFile.getName()));
		InputStream defaultConfig = General.class.getResourceAsStream(File.separator + configFile.getName());
		FileWriter out = new FileWriter(configFile);
		Scanner lines = new Scanner(defaultConfig);
		while(lines.hasNextLine())
			out.write(lines.nextLine() + "\n");
		out.flush();
		out.close();
		defaultConfig.close();
	}
}
