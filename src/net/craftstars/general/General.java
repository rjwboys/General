package net.craftstars.general;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.craftstars.general.command.GeneralCommand;
import net.craftstars.general.util.PluginLogger;
import net.craftstars.general.util.PropertyFile;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class General extends JavaPlugin
{
	public static General plugin;
	
	private static final boolean DEBUG = true;
	
	protected static final PluginLogger logger = PluginLogger.getLogger("General", DEBUG);
	
	private Configuration config;
	
	public static HashMap<String, String> items;
	public static PropertyFile itemsp;
	
	public General()
	{
		plugin = this;
	}
	
	public void onEnable()
	{
		General.logger.setPluginVersion(this.getDescription().getVersion());
		
		this.config = this.getConfiguration();
		this.loadConfiguration();
		
		General.itemsp = new PropertyFile("items.db");
		this.setupItems();
		
		General.logger.info("Plugin successfully loaded!");
	}
	
	public void onDisable()
	{
		General.logger.info("Plugin disabled!");
	}
	
	private void loadConfiguration()
	{
		this.config.load();
		
		try
		{
			File dataFolder = this.getDataFolder();
			
			if (!dataFolder.exists()) dataFolder.mkdirs();
			
			File configFile = new File(dataFolder, "config.yml");
			
			if (!configFile.exists())
			{
				General.logger.info("Configuration file does not exist. Attempting to create default one...");
				InputStream defaultConfig = this.getClass().getResourceAsStream(File.separator+"config.yml");
				FileWriter out = new FileWriter(configFile);
				for (int i = 0; (i = defaultConfig.read()) > 0;) out.write(i);
				out.flush();
				out.close();
				defaultConfig.close();
				this.config.load();
				General.logger.info("Default configuration created successfully! You can now stop the server and edit plugin/General/config.yml.");
			}
		}
		catch (Exception ex)
		{
			General.logger.warn("Could not read and/or write config.yml! Continuing with default values!", ex);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		try
		{
			Class<? extends GeneralCommand> clazz = this.getClass().getClassLoader().loadClass("net.craftstars.general.command."+command.getName()+"Command").asSubclass(GeneralCommand.class);
			GeneralCommand commandInstance = (GeneralCommand) clazz.newInstance();
			
			return commandInstance.runCommand(this, sender, command, commandLabel, args);
		}
		catch (Exception ex)
		{
			General.logger.error("There was a big problem executing command ["+command.getName()+"]! Please report this error!");
		}
		
		return false;
	}
	
	/**
     * Setup Items
     */
    public void setupItems() {
    	
		@SuppressWarnings("rawtypes")
		Map mappedItems = null;
		items = new HashMap<String, String>();
	
		try {
			mappedItems = itemsp.returnMap();
		} catch (Exception ex) {
			General.logger.warn("Could not open items.db!");
		}
	
		if(mappedItems != null) {
			for (Object item : mappedItems.keySet()) {
				String left = (String)item;
				String right = (String) mappedItems.get(item);
				String id = left.trim();
				String itemName;
				//log.info("Found " + left + "=" + right + " in items.db");
				if(id.matches("[0-9]+") || id.matches("[0-9]+,[0-9]+")) {
					//log.info("matches");
					if(right.contains(",")) {
						String[] synonyms = right.split(",");
						itemName = synonyms[0].replaceAll("\\s","");
						items.put(id, itemName);
						//log.info("Added " + id + "=" + itemName);
						for(int i = 1; i < synonyms.length; i++) {
							itemName = synonyms[i].replaceAll("\\s","");
							items.put(itemName, id);
							//log.info("Added " + itemName + "=" + id);
						}
					} else {
						itemName = right.replaceAll("\\s","");
						items.put(id, itemName);
						//log.info("Added " + id + "=" + itemName);
					}
				} else {
					itemName = left.replaceAll("\\s","");
					id = right.trim();
					items.put(itemName, id);
					//log.info("Added " + itemName + "=" + id);
				}
			}
		}
    }
}