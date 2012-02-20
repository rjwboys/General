
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
import net.craftstars.general.util.CommandManager;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.PermissionManager;
import net.craftstars.general.util.PluginLogger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class General extends JavaPlugin {
	public static General plugin = null;
	public static final boolean DEBUG = true;
	public static final String codename = "Dowland";
	public static PluginLogger logger;
	private FileConfiguration config;
	public File configFile;
	public static PlayerManager players = new PlayerManager();
	
	@Override
	public void onEnable() {
		boolean alreadyLoaded = false;
		if(plugin != null) {
			General.logger.warn(LanguageText.LOG_TWICE.value());
			alreadyLoaded = true;
		}
		plugin = this;
		logger = PluginLogger.getLogger(this, codename, DEBUG);
		loadAllConfigs();
		logger.info("[Codename: " + General.codename + "] " + LanguageText.LOG_SUCCESS.value());
		if(alreadyLoaded) {
			PermissionManager.refreshItemGroups();
			return;
		}
		PermissionManager.setup();
		CommandManager.setup(config);
		HelpHandler.setup();
		getServer().getPluginManager().registerEvents(players, this);
	}

	public void loadAllConfigs() {
		// The load order here is very delicate, as LanguageText is used nearly everywhere
		// and Messaging.load uses Option.
		File dataFolder = this.getDataFolder();
		if(!dataFolder.exists()) dataFolder.mkdirs();
		LanguageText.setLanguage("en", getDataFolder(), "messages_en.yml"); // Make sure we have a default language!
		config = getConfig();
		loadConfiguration();
		Option.setConfiguration(config);
		Messaging.load();
		
		Items.setup();
		MobType.setup();
		Kits.load();
		EconomyManager.setup();
	}

	@Override
	public void onDisable() {
		Items.save();
		Messaging.save();
		if(Option.AUTO_SAVE.get()) {
			Kits.save();
			try {
				config.save(configFile);
			} catch(IOException e) { // TODO: LanguageText
				logger.warn("Error saving config.yml: " + e.getMessage());
			}
		}
		General.logger.info(LanguageText.LOG_DISABLED.value());
	}
	
	private void loadConfiguration() {
		configFile = new File(getDataFolder(), "config.yml");
		try {
			if(!configFile.exists()) {
				createDefaultConfig(configFile);
				General.logger.info(LanguageText.LOG_CONFIG_SUCCESS.value());
			}
			config.load(configFile);
		} catch(Exception ex) {
			General.logger.warn(LanguageText.LOG_CONFIG_ERROR.value("file", "config.yml"), ex);
		}
	}

	public static void createDefaultConfig(File configFile) throws IOException {
		General.logger.info(LanguageText.LOG_CONFIG_DEFAULT.value("file", configFile.getName()));
		InputStream defaultConfig = General.plugin.getResource(configFile.getName());
		FileWriter out = new FileWriter(configFile);
		Scanner lines = new Scanner(defaultConfig);
		while(lines.hasNextLine())
			out.write(lines.nextLine() + "\n");
		out.flush();
		out.close();
		defaultConfig.close();
	}
}
