package net.craftstars.general.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.config.Configuration;

public class CommandHandler {
	public static boolean setAliases = false;
	private static SimpleCommandMap commandMap = null;
	private static Method register = null;
	public static String[] compassAliases;
	public static String[] posAliases;
	
	public static void setup(Configuration config) {
		if(setAliases) return;
		if(commandMap == null && !getCommandMap()) return;
		if(register == null && !getRegisterMethod()) return;
		if(!config.getKeys(null).contains("aliases"))
			General.logger.warn(LanguageText.LOG_COMMAND_NO_ALIASES.value());
		PluginDescriptionFile plug = General.plugin.getDescription();
		try {
			@SuppressWarnings({"unchecked", "rawtypes"})
			Map<String,Map> commands = (Map<String, Map>) plug.getCommands();
			for(String key : commands.keySet()) {
				PluginCommand generalCommand = General.plugin.getCommand(key);
				//General.logger.debug("Registering aliases for command: " + key);
				if(key.contains("."))
					register(key.split("\\.")[1], generalCommand);
				for(String alias : config.getStringList("aliases." + key, null))
					register(alias, generalCommand);
				try {
					Class<? extends CommandBase> clazz = General.plugin.getClass().getClassLoader()
						.loadClass("net.craftstars.general.command." + generalCommand.getName() + "Command")
						.asSubclass(CommandBase.class);
					CommandBase commandInstance = clazz.getConstructor(General.class).newInstance(General.plugin);
					generalCommand.setExecutor(commandInstance);
				} catch(ClassNotFoundException e) {
					General.logger.error(LanguageText.LOG_COMMAND_REG_ERROR.value("command", generalCommand.getName()),e);
				} catch(IllegalArgumentException e) {
					General.logger.error(LanguageText.LOG_COMMAND_REG_ERROR.value("command", generalCommand.getName()),e);
				} catch(SecurityException e) {
					General.logger.error(LanguageText.LOG_COMMAND_REG_ERROR.value("command", generalCommand.getName()),e);
				} catch(InstantiationException e) {
					General.logger.error(LanguageText.LOG_COMMAND_REG_ERROR.value("command", generalCommand.getName()),e);
				} catch(IllegalAccessException e) {
					General.logger.error(LanguageText.LOG_COMMAND_REG_ERROR.value("command", generalCommand.getName()),e);
				} catch(InvocationTargetException e) {
					General.logger.error(LanguageText.LOG_COMMAND_REG_ERROR.value("command", generalCommand.getName()),e);
				} catch(NoSuchMethodException e) {
					General.logger.error(LanguageText.LOG_COMMAND_REG_ERROR.value("command", generalCommand.getName()),e);
				}
			}
		} catch(NullPointerException e) {
			return;
		} catch(ClassCastException e) {
			General.logger.error("Commands are of wrong type!",e);
		}
	}
	
	public static boolean register(String label, Command command) {
		try {
			boolean success = (Boolean) register.invoke(commandMap, label, "General.dynalias", command, true);
			if(!success) {
				Command cmd = Bukkit.getServer().getPluginCommand(label);
				String claimant;
				if(cmd instanceof PluginCommand)
					claimant = ((PluginCommand) cmd).getPlugin().getDescription().getName();
				else
					claimant = Bukkit.getServer().getName();
				General.logger.info(LanguageText.LOG_COMMAND_TAKEN.value("alias", label, "plugin", claimant));
			}
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
