package net.craftstars.general.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import net.craftstars.general.General;
import net.craftstars.general.text.LanguageText;

public abstract class Option<T> {
	// Misc settings
	public static Option<Boolean> AWAY_SLEEP = new OptionBoolean("away-sleep", true);
	public static Option<Boolean> SHOW_USAGE = new OptionBoolean("show-usage-on-fail", true);
	public static Option<String> TAG_FORMAT = new OptionString("tag-fmt", "name:");
	public static Option<Boolean> SHOW_MOTD = new OptionBoolean("show-motd", true);
	public static Option<Boolean> AUTO_SAVE = new OptionBoolean("auto-save", false);
	public static Option<Boolean> LOG_COMMANDS = new OptionBoolean("log-commands", false);
	public static Option<String> LANGUAGE = new OptionString("language", "en");
	public static Option<Boolean> EXPORT_PERMISSIONS = new OptionBoolean("export-permissions", false);
	public static Option<Boolean> EXPORT_PERMISSIONS_CHILDREN = new OptionBoolean("export-permissions-children", false);
	public static Option<String> BAN_KICK = new OptionString("ban-kick-msg", "You have been banned from this server!");
	public static Option<Boolean> HEAL_HUNGER = new OptionBoolean("heal-hunger", true);
	public static Option<String> DEFAULT_AWAY_MSG = new OptionString("default-away-msg", "Generic away reason");
	public static Option<Boolean> AWAY_DEFAULTS = new OptionBoolean("away-defaults", true);
	// Playerlist settings
	public static Option<Boolean> SHOW_WORLD = new OptionBoolean("playerlist.show-world", false);
	public static Option<Boolean> SHOW_HEALTH = new OptionBoolean("playerlist.show-health", true);
	public static Option<Boolean> SHOW_COORDS = new OptionBoolean("playerlist.show-coords", true);
	public static Option<Boolean> SHOW_IP = new OptionBoolean("playerlist.show-ip", false);
	public static Option<Boolean> ALLOW_OVERRIDE = new OptionBoolean("playerlist.allow-all", false);
	// Economy settings
	public static Option<Boolean> NO_ECONOMY = new OptionBoolean("economy.disable", true);
	public static Option<Integer> ECONOMY_ITEM = new OptionInteger("economy.item", -1);
	public static Option<Double> ECONOMY_SELL = new OptionDouble("economy.give.sell", 100);
	public static Option<String> ECONOMY_TAKE_SELL = new OptionString("economy.give.take", "sell");
	public static Option<String> ECONOMY_CLEAR_SELL = new OptionString("economy.give.clear", "sell");
	public static Option<String> KIT_METHOD = new OptionString("economy.give.kits", "individual");
	public static Option<Double> KIT_DISCOUNT = new OptionDouble("economy.give.discount", 80);
	// Give settings
	public static Option<Integer> GIVE_MASS = new OptionInteger("give.mass", 64);
	public static Option<Boolean> OTHERS4ALL = new OptionBoolean("give.others-for-all", true);
	public static Option<Set<String>> ITEM_GROUPS = new OptionKeys("give.groups");
	// Range settings
	public static Option<Integer> LIGHTNING_RANGE = new OptionInteger("lightning-range", 20);
	public static Option<Integer> SUMMON_RANGE = new OptionInteger("summon-range", 30);
	// Teleport settings
	public static Option<List<String>> TELEPORT_BASICS = new OptionStringList("teleport.basics",
		Arrays.asList("world", "player", "home", "spawn"));
	public static Option<Integer> TELEPORT_WARMUP = new OptionInteger("teleport.warm-up", 0);
	// Time Settings
	public static Option<Boolean> SHOW_TICKS = new OptionBoolean("time.show-ticks", true);
	public static Option<Boolean> TIME_FORMAT = new OptionBoolean("time.format-24-hour", false);
	// Complex settings
	public static Option<Integer> COOLDOWN(String command) {
		return new OptionInteger("cooldown." + command, 0);
	}
	public static Option<Double> ECONOMY_COST(String node) {
		return new OptionDouble(node, 0.0);
	}
	public static Option<List<Integer>> GROUP(String group) {
		return new OptionIntegerList("give.groups." + group, null);
	}
	protected String node;
	protected T def;
	private static Configuration config;
	
	@SuppressWarnings("hiding")
	protected Option(String node, T def) {
		this.node = node;
		this.def = def;
	}
	
	public abstract T get();
	
	public void set(T value) {
		config.set(node, value);
	}
	
	public void remove() {
		config.set(node, null);
	}
	
	public void reset() {
		set(def);
	}

	public static boolean nodeExists(String node) {
		Object prop = config.get(node);
		return prop != null;
	}
	
	public static void setConfiguration(Configuration c) {
		config = c;
	}
	
	public static void setProperty(String path, Object value) {
		config.set(path, value);
	}
	
	public static Object getProperty(String path) {
		return config.get(path);
	}
	
	protected Configuration setDefault() {
		if(def != null && config.get(node) == null) config.set(node, def);
		return config;
	}
	
	public static void save() {
		try {
			((FileConfiguration)config).save(General.plugin.configFile);
		} catch(IOException e) {
			General.logger.warn(LanguageText.LOG_CONFIG_SAVE_ERROR.value("msg", e.getMessage()));
		} catch(ClassCastException e) {
			General.logger.warn(LanguageText.LOG_CONFIG_SAVE_ERROR.value("msg", e.getMessage()));
		}
	}
}

class OptionBoolean extends Option<Boolean> {
	@SuppressWarnings("hiding") OptionBoolean(String node, boolean def) {
		super(node, def);
	}

	@Override
	public Boolean get() {
		return setDefault().getBoolean(node, def);
	}
}

class OptionString extends Option<String> {
	@SuppressWarnings("hiding") OptionString(String node, String def) {
		super(node, def);
	}

	@Override
	public String get() {
		return setDefault().getString(node, def);
	}
}

class OptionInteger extends Option<Integer> {
	@SuppressWarnings("hiding") OptionInteger(String node, int def) {
		super(node, def);
	}

	@Override
	public Integer get() {
		return setDefault().getInt(node, def);
	}
}

class OptionDouble extends Option<Double> {
	@SuppressWarnings("hiding") OptionDouble(String node, double def) {
		super(node, def);
	}

	@Override
	public Double get() {
		return setDefault().getDouble(node, def);
	}
}

class OptionStringList extends Option<List<String>> {
	@SuppressWarnings("hiding") OptionStringList(String node, List<String> def) {
		super(node, def);
	}

	@Override
	public List<String> get() {
		List<String> list = setDefault().getStringList(node);
		if(list == null) return def;
		return list;
	}
}

class OptionIntegerList extends Option<List<Integer>> {
	@SuppressWarnings("hiding") OptionIntegerList(String node, List<Integer> def) {
		super(node, def);
	}

	@Override
	public List<Integer> get() {
		List<Integer> list = setDefault().getIntegerList(node);
		if(list == null) return def;
		return list;
	}
}

class OptionKeys extends Option<Set<String>> {
	@SuppressWarnings("hiding") OptionKeys(String node) {
		super(node, null);
	}

	@Override
	public Set<String> get() {
		ConfigurationSection section = setDefault().getConfigurationSection(node);
		if(section == null) return new HashSet<String>();
		return section.getKeys(false);
	}
}