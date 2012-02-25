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

public abstract class Options<T> {
	// Misc settings
	public static Options<Boolean> AWAY_SLEEP = new OptionBoolean("away-sleep", true);
	public static Options<Boolean> SHOW_USAGE = new OptionBoolean("show-usage-on-fail", true);
	public static Options<String> TAG_FORMAT = new OptionString("tag-fmt", "name:");
	public static Options<Boolean> SHOW_MOTD = new OptionBoolean("show-motd", true);
	public static Options<Boolean> AUTO_SAVE = new OptionBoolean("auto-save", false);
	public static Options<Boolean> LOG_COMMANDS = new OptionBoolean("log-commands", false);
	public static Options<String> LANGUAGE = new OptionString("language", "en");
	public static Options<Boolean> EXPORT_PERMISSIONS = new OptionBoolean("export-permissions", false);
	public static Options<Boolean> EXPORT_PERMISSIONS_CHILDREN = new OptionBoolean("export-permissions-children", false);
	public static Options<String> BAN_KICK = new OptionString("ban-kick-msg", "You have been banned from this server!");
	public static Options<Boolean> HEAL_HUNGER = new OptionBoolean("heal-hunger", true);
	public static Options<String> DEFAULT_AWAY_MSG = new OptionString("default-away-msg", "Generic away reason");
	public static Options<Boolean> AWAY_DEFAULTS = new OptionBoolean("away-defaults", true);
	// Playerlist settings
	public static Options<Boolean> SHOW_WORLD = new OptionBoolean("playerlist.show-world", false);
	public static Options<Boolean> SHOW_HEALTH = new OptionBoolean("playerlist.show-health", true);
	public static Options<Boolean> SHOW_COORDS = new OptionBoolean("playerlist.show-coords", true);
	public static Options<Boolean> SHOW_IP = new OptionBoolean("playerlist.show-ip", false);
	public static Options<Boolean> ALLOW_OVERRIDE = new OptionBoolean("playerlist.allow-all", false);
	public static Options<Boolean> SHOW_INVISIBLE = new OptionBoolean("playerlist.show-invisible", false);
	// Economy settings
	public static Options<Boolean> NO_ECONOMY = new OptionBoolean("economy.disable", true);
	public static Options<Integer> ECONOMY_ITEM = new OptionInteger("economy.item", -1);
	public static Options<Double> ECONOMY_SELL = new OptionDouble("economy.give.sell", 100);
	public static Options<String> ECONOMY_TAKE_SELL = new OptionString("economy.give.take", "sell");
	public static Options<String> ECONOMY_CLEAR_SELL = new OptionString("economy.give.clear", "sell");
	public static Options<String> KIT_METHOD = new OptionString("economy.give.kits", "individual");
	public static Options<Double> KIT_DISCOUNT = new OptionDouble("economy.give.discount", 80);
	// Give settings
	public static Options<Integer> GIVE_MASS = new OptionInteger("give.mass", 64);
	public static Options<Boolean> OTHERS4ALL = new OptionBoolean("give.others-for-all", true);
	public static Options<Set<String>> ITEM_GROUPS = new OptionKeys("give.groups");
	// Range settings
	public static Options<Integer> LIGHTNING_RANGE = new OptionInteger("lightning-range", 20);
	public static Options<Integer> SUMMON_RANGE = new OptionInteger("summon-range", 30);
	// Teleport settings
	public static Options<List<String>> TELEPORT_BASICS = new OptionStringList("teleport.basics",
		Arrays.asList("world", "player", "home", "spawn"));
	public static Options<Integer> TELEPORT_WARMUP = new OptionInteger("teleport.warm-up", 0);
	// Time Settings
	public static Options<Boolean> SHOW_TICKS = new OptionBoolean("time.show-ticks", true);
	public static Options<Boolean> TIME_FORMAT = new OptionBoolean("time.format-24-hour", false);
	// Complex settings
	public static Options<Integer> COOLDOWN(String command) {
		return new OptionInteger("cooldown." + command, 0);
	}
	public static Options<Double> ECONOMY_COST(String node) {
		return new OptionDouble(node, 0.0);
	}
	public static Options<List<Integer>> GROUP(String group) {
		return new OptionIntegerList("give.groups." + group, null);
	}
	private Options();
}