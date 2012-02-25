package net.craftstars.general.option;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public abstract class Options {
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
	public static Option<Boolean> SHOW_INVISIBLE = new OptionBoolean("playerlist.show-invisible", false);
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
	private Options() {}
}