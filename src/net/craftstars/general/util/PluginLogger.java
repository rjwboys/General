
package net.craftstars.general.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

public class PluginLogger {
	private static PluginLogger instance;
	private final Logger logger;
	private final String codename;
	private final boolean debugMode;
	
	@SuppressWarnings("hiding")
	private PluginLogger(Plugin plugin, String codename, boolean debugMode) {
		this.logger = plugin.getLogger();
		this.codename = codename;
		this.debugMode = debugMode;
	}
	
	public static PluginLogger getLogger(Plugin plugin, String codename) {
		return getLogger(plugin, codename, false);
	}
	
	public static PluginLogger getLogger(Plugin plugin, String codename, boolean debugMode) {
		if(instance == null) instance = new PluginLogger(plugin, codename, debugMode);
		return instance;
	}
	
	public void debug(String msg, Throwable thrown) {
		if(this.debugMode) this.logger.log(Level.INFO, this.formatDebugMessage(msg), thrown);
	}
	
	public void debug(String msg) {
		if(this.debugMode) this.logger.log(Level.INFO, this.formatDebugMessage(msg));
	}
	
	public void info(String msg) {
		this.log(Level.INFO, msg);
	}
	
	public void info(String msg, Throwable thrown) {
		this.log(Level.INFO, msg, thrown);
	}
	
	public void warn(String msg) {
		this.log(Level.WARNING, msg);
	}
	
	public void warn(String msg, Throwable thrown) {
		this.log(Level.WARNING, msg, thrown);
	}
	
	public void error(String msg) {
		this.log(Level.SEVERE, msg);
	}
	
	public void error(String msg, Throwable thrown) {
		this.log(Level.SEVERE, msg, thrown);
	}
	
	public void log(Level level, String msg, Throwable thrown) {
		this.logger.log(level, msg, thrown);
	}
	
	public void log(Level level, String msg) {
		this.logger.log(level, msg);
	}
	
	private String formatDebugMessage(String msg) {
		return "[" + codename + "-DEBUG] " + msg;
	}
	
	public Logger getInternal() {
		return logger;
	}
}
