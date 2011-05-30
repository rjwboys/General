package net.craftstars.general.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.craftstars.general.General;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.CraftServer;
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
		try {
			for(String cat : config.getKeys("aliases")) {
				for(String cmd : config.getKeys("aliases." + cat)) {
					Command generalCommand = General.plugin.getCommand(cat + '.' + cmd);
					if(Toolbox.equalsOne(cat + '.' + cmd, "info.compass", "info.where")) {
						if(cmd.equals("compass")) {
							List<String> ls = config.getStringList("aliases.info.compass", null);
							compassAliases = new String[ls.size() + 1];
							compassAliases[0] = "compass";
							for(int i = 1; i <= ls.size(); i++)
								compassAliases[i] = ls.get(i-1);
						} else if(cmd.equals("where")) {
							List<String> ls = config.getStringList("aliases.info.where", null);
							posAliases = new String[ls.size() + 1];
							posAliases[0] = "where";
							for(int i = 1; i <= ls.size(); i++)
								posAliases[i] = ls.get(i-1);
						}
					} else for(String alias : config.getStringList("aliases." + cat + '.' + cmd, null))
						register(alias, generalCommand);
				}
			}
		} catch(NullPointerException e) {
			return;
		}
	}
	
	public static boolean register(String label, Command command) {
		try {
			return (Boolean) register.invoke(commandMap, label, "General.dynalias", command, true);
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
