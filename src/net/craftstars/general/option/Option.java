package net.craftstars.general.option;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import net.craftstars.general.General;
import net.craftstars.general.text.LanguageText;

public abstract class Option<T> {
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