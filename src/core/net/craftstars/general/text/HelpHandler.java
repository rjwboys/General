
package net.craftstars.general.text;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Scanner;

import me.taylorkelly.help.Help;
import net.craftstars.general.General;
import net.craftstars.general.util.Toolbox;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

public final class HelpHandler {
	public static boolean gotHelp;
	private HelpHandler() {}
	
	public static void setup() {
		Plugin test = Bukkit.getPluginManager().getPlugin("Help");
		if(test != null) {
			Help help = ((Help) test);
			help.registerCommand("playerlist ([world])",
				"Lists online players." + fetchAliases("playerlist"),
				General.plugin, "general.playerlist", "general.basic");
			help.registerCommand("who ([player])",
				"Displays information about a player." + fetchAliases("who", "whoami"),
				General.plugin, "general.who", "general.basic");
			help.registerCommand("whoami",
				"Displays information about you.", General.plugin);
			help.registerCommand("time ([world])",
				"Displays the current time in [world]." + fetchAliases("time"),
				General.plugin, "general.time", "general.basic");
			help.registerCommand("time help",
				"Shows syntax for setting the time.", General.plugin, "general.time.set");
			help.registerCommand("give [item](:[variant]) ([amount]) ([player])",
				"Gives [player] [amount] of [item]." + fetchAliases("give"), General.plugin, "general.give");
			help.registerCommand("give help",
				"More detailed info on the give command.", General.plugin, "general.give");
			help.registerCommand("items [item1] [item2] ... [itemN]",
				"Give yourself several different items at once. You get one of each item." + fetchAliases("items"),
				General.plugin, "general.give.mass");
			help.registerCommand("getpos ([player]) (compass|dir|pos|rot)",
				"Get the current position of [player]." + fetchAliases("getpos", "compass", "where"),
				General.plugin, "general.getpos", "general.basic");
			help.registerCommand("tell [player] [message]",
				"Whisper to a player." + fetchAliases("tell"), General.plugin, "general.tell", "general.basic");
			help.registerCommand("setspawn ([player|world]) ([destination])",
				"Sets the spawn location of the world or player to the specified destination." + fetchAliases("setspawn"),
				General.plugin, "general.spawn.set");
			help.registerCommand("teleport ([target]) [destination]",
				"Teleport the target(s) to the specified destination." + fetchAliases("teleport"),
				General.plugin, "general.teleport");
			help.registerCommand("teleport help",
				"More detailed information on types of targets and destinations.", General.plugin, "general.teleport");
			help.registerCommand("clear ([player]) (pack|quickbar|armo(u)r|all)",
				"Clears [player]'s inventory." + fetchAliases("clear"), General.plugin, "general.clear");
			help.registerCommand("take [item](:[variant]) ([amount]) ([player])",
				"Deletes something from [player]'s inventory." + fetchAliases("take"),
				General.plugin, "general.take");
			help.registerCommand("heal ([player]) ([amount])",
				"Heals [player] by [amount] hearts (0-10). If [amount] is omitted, full heal." + fetchAliases("heal"),
				General.plugin, "general.heal");
			help.registerCommand("general reload",
				"Reloads the configuration files.", General.plugin, "OP", "general.admin.reload");
			help.registerCommand("general motd",
				"Displays the message of the day.", General.plugin);
			help.registerCommand("mobspawn [mob](;[mount])",
				"Spawns a [mob] riding a [mount]. " + fetchAliases("mobspawn"), General.plugin, "general.mobspawn");
			help.registerCommand("mobspawn help",
				"More detailed help on spawning mobs.", General.plugin, "general.mobspawn");
			help.registerCommand("help General",
				"Help for the General plugin.", General.plugin, true);
			help.registerCommand("away [reason]",
				"Sets your away status." + fetchAliases("away"), General.plugin, "general.away", "general.basic");
			help.registerCommand("kit [kit]",
				"Gives you the [kit], or shows a list of available kits." + fetchAliases("kit"),
				General.plugin, "general.kit");
			help.registerCommand("worldinfo [world]",
				"Shows info on a given world, such as the spawn location or the seed." + fetchAliases("worldinfo"),
				General.plugin, "general.worldinfo");
			help.registerCommand("weather ([world|player]) on|off|start|stop|thunder|zap|[duration]",
				"Alter the weather" + fetchAliases("weather"), General.plugin, "general.weather");
			help.registerCommand("general item help",
				"Information on how to edit the item definitions.", General.plugin, "general.admin.item");
			help.registerCommand("general kit help",
				"Information on how to edit the kit definitions.", General.plugin, "general.admin.kit");
			help.registerCommand("op [player1] ... [playerN]",
				"Promotes players to operator", General.plugin, "general.op");
			help.registerCommand("deop [player1] ... [playerN]",
				"Demotes players from operator", General.plugin, "general.op");
			help.registerCommand("kick [player] ([reason])",
				"Kicks a player off the server", General.plugin, "general.kick");
			General.logger.info(LanguageText.LOG_HELP_ENABLED.value("version", help.getDescription().getVersion()));
			gotHelp = true;
		} else {
			General.logger.warn(LanguageText.LOG_HELP_MISSING.value());
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
			Messaging.send(sender, LanguageText.HELP_UNAVAILABLE);
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
		return false;
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
		return " " + output;
	}
	
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String time = "{rose}/time day|night|nood|midday|midnight\n"
			+ "{rose}/time dawn|sunrise|morning|dusk|sunset|evening\n" + "{rose}/time +{grey}[ticks]{white} : Fast-forward time.\n"
			+ "{rose}/time -{grey}[ticks|time]{white} : Rewind time.\n" + "{rose}/time ={grey}[ticks|time]{white} : Set time.\n"
			+ "{rose}Time can be given in {grey}hh{white}:{grey}mm{white} format (with optional {grey}am{white}/{grey}pm{white})"
			+ " or as a number of ticks ({grey}0{white}-{grey}24000{white}).";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String give = "{rose}/give {grey}[item]{rose} ({grey}[amount]{rose}){white} : Gives something to you.\n"
			+ "{rose}/give {grey}[item]{rose} ({grey}[amount]{rose}){white} {grey}[player]{white} : Gives something to someone else.\n"
			+ "{white}An amount of {grey}-1{white} is an infinite stack; {grey}0{white} is one full stack.\n"
			+ "{white}The {grey}[item]{white} and {grey}[variant]{white} both may be either a number or a name.\n"
			+ "{white}Example: {rose}/give Notch wool:red 5{white} : Gives a stack of five red wool to Notch.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String take = "{rose}/take {grey}[item]{rose} ({grey}[amount]{rose}){white} : Takes something from you.\n"
			+ "{rose}/take {grey}[item]{rose} ({grey}[amount]{rose}){white} {grey}[player]{white} : Takes something from someone else.\n"
			+ "{white}An amount of {grey}-1{white} means take all (but is also default); {grey}0{white} is one full stack.\n"
			+ "{white}The {grey}[item]{white} and {grey}[variant]{white} both may be either a number or a name.\n"
			+ "{white}Example: {rose}/take wool:red 5 Notch{white} : Takes five red wool from Notch.\n"
			+ "{white}Example: {rose}/take wool 5 Notch{white} : Takes five wool of any colour from Notch.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String mobspawn = "{rose}/mobspawn {grey}[mob]{rose}(;{grey}[mount]{rose}){white} : Spawns a mob.\n"
			+ "{white}Valid mobs and mounts are:\n"
			+ "{grey}pig{white}(:{grey}[saddle]{white}), {grey}chicken{white}, {grey}squid{white}, {grey}cow{white}, {grey}sheep{white}(:{grey}[colour|bare]{white}),"
			+ "{grey}pigzombie{white}(:{grey}[anger]{white}), {grey}creeper{white}(:{grey}[power]{white}), {grey}ghast{white}, {grey}skeleton{white},"
			+ "{grey}spider{white}, {grey}zombie{white}, {grey}slime{white}(:{grey}[size]{white}), {grey}giant{white}({grey}zombie{white}), {grey}human{white}, "
			+ "{grey}wolf{white}(:{grey}[owner|anger]{white})\n" + "(sheep can be spawned without wool as well)\n"
			+ "{rose}Example: /mobspawn skeleton;spider : Spawns a skeleton riding a spider.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String go = "{rose}/teleport ({grey}[target]{rose}) {grey}[destination]{white} : Teleports someone to somewhere.\n"
		+ "{white}Valid targets are: {grey}self, there, near, nearmob, *, a world name, a player name, a list of players\n"
		+ "{white}Valid destinations are: {grey}there, here, home, spawn, compass, a player, a world\n"
		+ "{grey}player$there|home|spawn|compass   x,y,z   world(x,y,z)";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_item = "{rose}/general item {grey}[command] [key] [value]{white} : Edits item aliases and names.\n"
		+ "{white}There are five subcommands:\n"
		+ "{rose}alias {grey}[alias] [item]{white} : Set the item that an alias refers to.\n"
		+ "{rose}variant {grey}[item]{rose}:{grey}[data] [name]{white} : Set the name of a variant for an item.\n"
		+ "{rose}name {grey}[item] [name]{grey} : Set the display name of an item.\n"
		+ "{rose}hook {grey}[primary]{rose}:{grey}[secondary] [item]{white} : Set a new pseudo-variant alias.\n"
		+ "{rose}group {grey}[group-name] delete|[item]{white} : Edit the item white/blacklists.\n";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set = "{rose}/general set {grey}[var] [value]{white} : Sets configuration variables";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_list = "{rose}The following variables can be set. Type /general set help {grey}[var]{white}"
		+ "for more details.\n" 
		+ "permissions  others-for-all  give-mass  show-health  show-coords  show-world  show-ip  show-motd\n"
		+ "24-hour  show-ticks  economy  economy-take  economy-clear  economy-kits  economy-sell  kits-discount\n"
		+ "chat-tag  log-commands  auto-save  lightning-range  teleport-warmup  time-cooldown  storm-cooldown\n"
		+ "thunder-cooldown  lighting-cooldown  show-usage";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_permissions =
		"The permissions system to use; changes take effect on server restart.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_others_for_all =
		"If true, item groups are a blacklist. Otherwise they're a whitelist.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_give_mass =
		"The maximum amount a player can /give at one time without needing extra permission.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_show_health =
		"Whether to show a health bar in the /who command.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_show_coords =
		"Whether to show the player's location and bed location in the /who command.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_show_world =
		"Whether to show the player's world in the /who command.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_show_ip =
		"Whether to consider showing the player's IP addres in the /who command.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_show_motd =
		"Whether to show the Message of the Day to players when they join.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_24_hour =
		"If true, time will be formatted in the 24-hour format.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_show_ticks =
		"If true, time will include the ticks as well as the formatted time.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_economy =
		"Set the economy system; changes take effect on server restart.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_economy_take =
		"Sets the behaviour of economy with the /take command. Valid values are sell (items are sold and " + 
		"the player gains money) or trash (the items just disappear).";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_economy_clear =
		"Sets the behaviour of economy with the /clear command. Valid values are sell (items are sold and " + 
		"the player gains money) or trash (the items just disappear).";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_economy_sell =
		"The percentage of the /give price for an item that the player gets when selling it using /take or /clear. " +
		"For example, set it to 60, and an item that costs 10 coins with /give will earn them 6 coins with /take.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_economy_kits =
		"Sets the behaviour of economy with the /kit command. Valid values are cumulative (the cost of a kit " +
		"is the sum of the costs of its components), discount (like above, but with a discount.), and individual " +
		"(the cost of a kit is the value set in kits.yml).";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_kits_discount =
		"If kits are set to use the discount method, this is the percentage of the cumulative cost that is used " +
		"as the actual cost. It's important to understand that this is actually the inverse of the normal meaning " +
		"of 'discount'; to get a 20% discount, for example, you must set it to 80.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_chat_tag =
		"The format used to 'tag' people; at present, the only use for this is that if you tag someone who's " +
		"away, you get to see their away message. It must contain the string 'name', since that will be replaced " +
		"with the actual name of the player when checking if someone is tagged.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_auto_save =
		"Whether to automatically save config.yml and kits.yml on server shutdown.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_lightning_range =
		"The range withing /weather zap will cause a lightning strike. Lower values increase the chance of " +
		"you (or the targeted player) being hit.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_teleport_warmup = "The warmup period for teleporting, in ticks.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_lightning_cooldown = "The cooldown period for lightning, in ticks.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_storm_cooldown = "The cooldown period for starting or stopping a storm, in ticks.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_thunder_cooldown =
		"The cooldown period for starting or stopping thunder, in ticks.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_set_time_cooldown = "The cooldown period for changing the time, in ticks.";
	@SuppressWarnings("unused")
	// because it only LOOKS unused; it's accessed reflectively
	private static String general_kit = "{rose}/general kit {grey}[kit] [command] [key] [value]{white} : Edits kit definitions.\n"
		+ "{white}There are five subcommands:\n"
		+ "{rose}add ({grey}[item]{white} ({grey}[amount]{white})) : Add an item to a kit; creates it if it doesn't exist.\n"
		+ "{rose}remove ({grey}[item]{white} ({grey}[amount]{white})) : Removes an item from a kit.\n"
		+ "{rose}delay {grey}[delay]{grey} : Set the cooldown delay of a kit.\n"
		+ "{rose}cost {grey}[price]{white} : Set the price of a kit (if economy is configured to use per-kit prices).\n"
		+ "{rose}trash : Delete a kit.\n"
		+ "{rose}list : List the contents of a kit.\n";
	// TODO: A way to account for permissions?
}
