package net.craftstars.general.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginLogger
{	
	private final Logger logger = Logger.getLogger("Minecraft");
	private final String pluginName;
	private String pluginVersion;
	private final boolean debugMode;
	
	private PluginLogger(String pluginName, boolean debugMode)
	{
		this.pluginName = pluginName;
		this.pluginVersion = "0.0";
		this.debugMode = debugMode;
	}
	
	public static PluginLogger getLogger(String pluginName)
	{
		return new PluginLogger(pluginName, false);
	}
	
	public static PluginLogger getLogger(String pluginName, boolean debugMode)
	{
		return new PluginLogger(pluginName, debugMode);
	}
	
	public void debug(String msg, Throwable thrown)
	{
		if (this.debugMode) this.logger.log(Level.INFO, this.formatDebugMessage(msg), thrown);
	}
	
	public void debug(String msg)
	{
		if (this.debugMode) this.logger.log(Level.INFO, this.formatDebugMessage(msg));
	}
	
	public void info(String msg)
	{
		this.log(Level.INFO, msg);
	}
	
	public void info(String msg, Throwable thrown)
	{
		this.log(Level.INFO, msg, thrown);
	}
	
	public void warn(String msg)
	{
		this.log(Level.WARNING, msg);
	}
	
	public void warn(String msg, Throwable thrown)
	{
		this.log(Level.WARNING, msg, thrown);
	}
	
	public void error(String msg)
	{
		this.log(Level.SEVERE, msg);
	}
	
	public void error(String msg, Throwable thrown)
	{
		this.log(Level.SEVERE, msg, thrown);
	}
	
	public void log(Level level, String msg, Throwable thrown)
	{
		this.logger.log(level, this.formatMessage(msg), thrown);
	}
	
	public void log(Level level, String msg)
	{
		this.logger.log(level, this.formatMessage(msg));
	}
	
	private String formatMessage(String msg)
	{
		return "["+this.pluginName+"-"+this.pluginVersion+"] "+msg;
	}
	
	private String formatDebugMessage(String msg)
	{
		return this.formatMessage("")+"[DEBUG] "+msg;
	}
	
	public void setPluginVersion(String pluginVersion)
	{
		this.pluginVersion = pluginVersion;
	}
}