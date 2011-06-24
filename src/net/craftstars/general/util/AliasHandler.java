package net.craftstars.general.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import net.craftstars.general.General;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.config.Configuration;

public class AliasHandler {
	public static boolean setAliases = false;
	private static SimpleCommandMap commandMap = null;
	private static Method register = null;
	public static String[] compassAliases;
	public static String[] posAliases;
	
	public static void setup() {
		if(setAliases) return;
		if(commandMap == null && !getCommandMap()) return;
		if(register == null && !getRegisterMethod()) return;
		Configuration config = General.plugin.config;
		PluginDescriptionFile plug = General.plugin.getDescription();
		try {
			@SuppressWarnings({"unchecked", "rawtypes"})
			Map<String,Map> commands = (Map<String, Map>) plug.getCommands();
			for(String key : commands.keySet()) {
				Command generalCommand = General.plugin.getCommand(key);
				//General.logger.debug("Registering aliases for command: " + key);
				if(key.contains("."))
					register(key.split("\\.")[1], generalCommand);
				else register(key, generalCommand);
				for(String alias : config.getStringList("aliases." + key, null))
					register(alias, generalCommand);
			}
			Command posCommand = General.plugin.getCommand("info.getpos");
			// Compass
			List<String> ls = config.getStringList("aliases.info.compass", null);
			compassAliases = new String[ls.size() + 1];
			compassAliases[0] = "compass";
			register("compass", posCommand);
			for(int i = 1; i <= ls.size(); i++) {
				compassAliases[i] = ls.get(i-1);
				register(compassAliases[i], posCommand);
			}
			// Where
			ls = config.getStringList("aliases.info.where", null);
			posAliases = new String[ls.size() + 1];
			posAliases[0] = "where";
			register("where", posCommand);
			for(int i = 1; i <= ls.size(); i++) {
				posAliases[i] = ls.get(i-1);
				register(posAliases[i], posCommand);
			}
		} catch(NullPointerException e) {
			return;
		} catch(ClassCastException e) {
			General.logger.error("Commands are of wrong type!",e);
		}
	}
	
	public static boolean register(String label, Command command) {
		try {
			//General.logger.debug("Registering " + label + " as an alias for " + command.getName());
			boolean success = (Boolean) register.invoke(commandMap, label, "General.dynalias", command, true);
			if(!success)
				General.logger.info("Command alias " + label + 
					" was not registered because another plugin claimed it.");
			return success;
		} catch(IllegalArgumentException e) {
			General.logger.warn(e.getMessage());
		} catch(IllegalAccessException e) {
			General.logger.warn(e.getMessage());
		} catch(InvocationTargetException e) {
			General.logger.warn(e.getMessage());
		}
		return false;
	}

	private static boolean getCommandMap() {
		CraftServer cs = (CraftServer) Bukkit.getServer();
		Field cm;
		try {
			cm = CraftServer.class.getDeclaredField("commandMap");
		} catch(SecurityException e) {
			General.logger.warn(e.getMessage());
			return false;
		} catch(NoSuchFieldException e) {
			General.logger.warn(e.getMessage());
			return false;
		}
		cm.setAccessible(true);
		try {
			commandMap = (SimpleCommandMap) cm.get(cs);
		} catch(IllegalArgumentException e) {
			General.logger.warn(e.getMessage());
			return false;
		} catch(IllegalAccessException e) {
			General.logger.warn(e.getMessage());
			return false;
		}
		return true;
	}
	
	private static boolean getRegisterMethod() {
		if(commandMap == null) return false;
		try {
			register = SimpleCommandMap.class.getDeclaredMethod("register", String.class, String.class, Command.class, boolean.class);
		} catch(SecurityException e) {
			General.logger.warn(e.getMessage());
			return false;
		} catch(NoSuchMethodException e) {
			General.logger.warn(e.getMessage());
			return false;
		}
		register.setAccessible(true);
		return true;
	}
}
