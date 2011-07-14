
package net.craftstars.general.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Scanner;

import me.taylorkelly.help.Help;
import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

public class HelpHandler {
	public static boolean gotHelp;
	
	public static void setup() {
		Plugin test = General.plugin.getServer().getPluginManager().getPlugin("Help");
		if(test != null) {
			Help helpPlugin = ((Help) test);
			helpPlugin.registerCommand("playerlist",
					"Lists online players." + fetchAliases("playerlist"),
					General.plugin, "general.playerlist", "general.basic");
			helpPlugin.registerCommand("who ([player])",
					"Displays information about a player." + fetchAliases("who", "whoami"),
					General.plugin, "general.who", "general.basic");
			helpPlugin.registerCommand("whoami",
					"Displays information about you.",
					General.plugin);
			helpPlugin.registerCommand("time ([world])",
					"Displays the current time in [world]." + fetchAliases("time"),
					General.plugin, "general.time", "general.basic");
			helpPlugin.registerCommand("time help",
					"Shows syntax for setting the time.", 
					General.plugin, "general.time.set");
			helpPlugin.registerCommand("give [item](:[variant]) ([amount]) ([player])",
					"Gives [player] [amount] of [item]." + fetchAliases("give"),
					General.plugin, "general.give");
			helpPlugin.registerCommand("give help",
					"More detailed info on the give command.",
					General.plugin, "general.give");
			helpPlugin.registerCommand("items [item1] [item2] ... [itemN]",
					"Give yourself several different items at once. You get one of each item." + fetchAliases("items"),
					General.plugin, "general.give.mass");
			helpPlugin.registerCommand("getpos ([player])",
					"Get the current position of [player]." + fetchAliases("getpos", "compass", "where"),
					General.plugin, "general.getpos", "general.basic");
			helpPlugin.registerCommand("compass",
					"Show your direction.",
					General.plugin, "general.getpos");
			helpPlugin.registerCommand("where ([player])",
					"Show the location of [player]; less detailed form of /getpos. Aliases: pos, coords",
					General.plugin, "general.getpos");
			helpPlugin.registerCommand("tell [player] [message]",
					"Whisper to a player." + fetchAliases("tell"),
					General.plugin, "general.tell", "general.basic");
			helpPlugin.registerCommand("setspawn ([player|world]) ([destination])",
					"Sets the spawn location of the world or player to the specified destination." + fetchAliases("setspawn"),
					General.plugin,
					"general.spawn.set");
			helpPlugin.registerCommand("teleport ([target]) [destination]",
					"Teleport the target(s) to the specified destination." + fetchAliases("teleport"),
					General.plugin, "general.teleport");
			helpPlugin.registerCommand("teleport help",
					"More detailed information on types of targets and destinations.",
					General.plugin, "general.teleport");
			helpPlugin.registerCommand("summon [player]",
					"Teleports a player to your location." + fetchAliases("summon"),
					General.plugin, "general.teleport.other");
			helpPlugin.registerCommand("clear ([player]) (pack|quickbar|armo(u)r|all)",
					"Clears [player]'s inventory." + fetchAliases("clear"),
					General.plugin, "general.clear");
			helpPlugin.registerCommand("take [item](:[variant]) ([amount]) ([player])",
					"Deletes something from [player]'s inventory." + fetchAliases("take"),
					General.plugin, "general.take");
			helpPlugin.registerCommand("heal ([player]) ([amount])",
					"Heals [player] by [amount] hearts (0-10). If [amount] is omitted, full heal." + fetchAliases("heal"),
					General.plugin, "general.heal");
			helpPlugin.registerCommand("general reload",
					"Reloads the configuration files.",
					General.plugin, "OP", "general.admin");
			helpPlugin.registerCommand("general die",
					"Kills the plugin.",
					General.plugin, "OP", "general.admin");
			helpPlugin.registerCommand("general motd",
					"Displays the message of the day.",
					General.plugin);
			helpPlugin.registerCommand("mobspawn [mob](;[mount])",
					"Spawns a [mob] riding a [mount]. " + fetchAliases("mobspawn"),
					General.plugin, "general.mobspawn");
			helpPlugin.registerCommand("mobspawn help",
					"More detailed help on spawning mobs.",
					General.plugin, "general.mobspawn");
			helpPlugin.registerCommand("help General",
					"Help for the General plugin.",
					General.plugin, true);
			helpPlugin.registerCommand("away [reason]",
					"Sets your away status." + fetchAliases("away"), 
					General.plugin, "general.away", "general.basic");
			helpPlugin.registerCommand("kit [kit]",
					"Gives you the [kit], or shows a list of available kits." + fetchAliases("kit"),
					General.plugin, "general.kit");
			helpPlugin.registerCommand("worldinfo [world]",
					"Shows info on a given world, such as the spawn location or the seed." + fetchAliases("worldinfo"),
					General.plugin, "general.worldinfo");
			helpPlugin.registerCommand("weather ([world|player]) on|off|start|stop|thunder|zap|[duration]",
					"Alter the weather" + fetchAliases("weather"),
					General.plugin, "general.weather");
			helpPlugin.registerCommand("general item help",
					"Information on how to edit the item definitions.",
					General.plugin, "general.admin");
			General.logger.info("[Help " + helpPlugin.getDescription().getVersion() + "] support enabled.");
			gotHelp = true;
		} else {
			General.logger.warn("[Help] isn't detected. No /help support; instead use /general help");
			gotHelp = false;
		}
	}
	
	public static void showHelp(CommandSender sender, String filename) {
		File dataFolder = General.plugin.getDataFolder();
		if(!dataFolder.exists()) dataFolder.mkdirs();
		Scanner f;
		try {
			File helpFile = new File(dataFolder, filename);
			f = new Scanner(helpFile);
		} catch(FileNotFoundException e) {
			Messaging.send(sender, "&rose;Help topic unavailable.");
			return;
		}
		Toolbox.showFile(sender, f, false);
	}
	
	public static boolean hasEntry(String name) {
		File dir = General.plugin.getDataFolder();
		File helpFile = new File(dir, name + ".help");
		if(helpFile.exists()) return true;
		try {
			HelpHandler.class.getDeclaredField(name.toLowerCase());
			return true;
		} catch(SecurityException e) {
			e.printStackTrace();
		} catch(NoSuchFieldException e) {}
		return false;
	}
	
	public static boolean displayEntry(CommandSender who, String name) {
		File dir = General.plugin.getDataFolder();
		File helpFile = new File(dir, name + ".help");
		if(helpFile.exists()) {
			Scanner f;
			try {
				f = new Scanner(helpFile);
				Toolbox.showFile(who, f, false);
				return true;
			} catch(FileNotFoundException e) {}
		}
		try {
			Field helpEntry = HelpHandler.class.getDeclaredField(name.toLowerCase());
			Messaging.send(who, (String) helpEntry.get(null));
			return true;
		} catch(SecurityException e) {
			e.printStackTrace();
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(NoSuchFieldException e) {}
		return CommandBase.SHOW_USAGE;
	}
	
	private static String fetchAliases(String command, String... skip) {
		PluginCommand cmd = General.plugin.getCommand(command);
		if(cmd == null) return "";
		List<String> aliases = cmd.getAliases();
		aliases.add(cmd.getName());
		for(String what : skip) {
			while(aliases.contains(what)) aliases.remove(what);
		}
		while(aliases.contains(command)) aliases.remove(command);
		String output;
		if(aliases.isEmpty()) output = "";
		else {
			output = "Alias";
			if(aliases.size() > 1) output += "es";
			String separator = ": ";
			for(String alias : aliases) {
				output += separator;
				output += alias;
				separator = ", ";
			}
		}
		return output;
	}
	
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String time = "&c/time day|night|nood|midday|midnight\n"
			+ "&c/time dawn|sunrise|morning|dusk|sunset|evening\n" + "&c/time +&7[ticks]&f : Fast-forward time.\n"
			+ "&c/time -&7[ticks|time]&f : Rewind time.\n" + "&c/time =&7[ticks|time]&f : Set time.\n"
			+ "&cTime can be given in &7hh&f:&7mm&f format (with optional &7am&f/&7pm&f)"
			+ " or as a number of ticks (&70&f-&724000&f).";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String give = "&c/give &7[item]&c (&7[amount]&c)&f : Gives something to you.\n"
			+ "&c/give &7[item]&c (&7[amount]&c)&f &7[player]&f : Gives something to someone else.\n"
			+ "&fAn amount of &7-1&f is an infinite stack; &70&f is one full stack.\n"
			+ "&fThe &7[item]&f and &7[variant]&f both may be either a number or a name.\n"
			+ "&fExample: &c/give Notch wool:red 5&f : Gives a stack of five red wool to Notch.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String take = "&c/take &7[item]&c (&7[amount]&c)&f : Takes something from you.\n"
			+ "&c/take &7[item]&c (&7[amount]&c)&f &7[player]&f : Takes something from someone else.\n"
			+ "&fAn amount of &7-1&f means take all (but is also default); &70&f is one full stack.\n"
			+ "&fThe &7[item]&f and &7[variant]&f both may be either a number or a name.\n"
			+ "&fExample: &c/take wool:red 5 Notch&f : Takes five red wool from Notch.\n"
			+ "&fExample: &c/take wool 5 Notch&f : Takes five wool of any colour from Notch.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String mobspawn = "&c/mobspawn &7[mob]&c(;&7[mount]&c)&f : Spawns a mob.\n"
			+ "&fValid mobs and mounts are:\n"
			+ "&7pig&f(:&7[saddle]&f), &7chicken&f, &7squid&f, &7cow&f, &7sheep&f(:&7[colour|bare]&f),"
			+ "&7pigzombie&f(:&7[anger]&f), &7creeper&f(:&7[power]&f), &7ghast&f, &7skeleton&f,"
			+ "&7spider&f, &7zombie&f, &7slime&f(:&7[size]&f), &7giant&f(&7zombie&f), &7human&f, "
			+ "&7wolf&f(:&7[owner|anger]&f)\n" + "(sheep can be spawned without wool as well)\n"
			+ "&cExample: /mobspawn skeleton;spider : Spawns a skeleton riding a spider.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String go = "&c/teleport (&7[target]&c) &7[destination]&f : Teleports someone to somewhere.\n"
		+ "&fValid targets are: &7self, there, near, nearmob, *, a world name, a player name, a list of players\n"
		+ "&fValid destinations are: &7there, here, home, spawn, compass, a player, a world\n"
		+ "&7player$there|home|spawn|compass   x,y,z   world(x,y,z)";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_item = "&c/general item &7[command] [key] [value]&f : Edits item aliases and names.\n"
		+ "&fThere are five subcommands:\n"
		+ "&calias &7[alias] [item]&f : Set the item that an alias refers to.\n"
		+ "&cvariant &7[item]&c:&7[data] [name]&f : Set the name of a variant for an item.\n"
		+ "&cname &7[item] [name]&7 : Set the display name of an item.\n"
		+ "&chook &7[primary]&c:&7[secondary] [item]&f : Set a new pseudo-variant alias.\n";
		+ "&cgroup &7[group-name] delete|[item]&f : Edit the item white/blacklists.\n";
	private static String general_set;
	private static String general_set_list;
	private static String general_set_permissions;
	private static String general_set_others_for_all;
	private static String general_set_give_mass;
	private static String general_set_show_health;
	private static String general_set_show_coords;
	private static String general_set_show_world;
	private static String general_set_show_ip;
	private static String general_set_show_motd;
	private static String general_set_24_hour;
	private static String general_set_show_ticks;
	private static String general_set_economy;
	private static String general_set_economy_take;
	private static String general_set_economy_clear;
	private static String general_set_economy_sell;
	private static String general_set_economy_kits;
	private static String general_set_kits_discount;
	private static String general_set_chat_tag;
	private static String general_set_auto_save;
	private static String general_set_lightning_range;
	private static String general_kit;
	// TODO: A way to account for permissions?
	
}
