
package net.craftstars.general.text;

import static java.lang.Math.max;
import java.io.File;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.craftstars.general.General;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Time;
import net.craftstars.general.util.Toolbox;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.TextWrapper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class Messaging {
	private static HashMap<String, Object> colours = new HashMap<String, Object>();
	private static FileConfiguration config;
	private static String langFile;
	static final String[] defaultColours = {
		"black", "navy", "green", "teal", "red", "purple", "gold", "silver",
		"grey", "blue", "lime", "aqua", "rose", "pink", "yellow", "white", "gray", "magic"
	};
	private Messaging() {}
	
	public static void load() {
		File dataFolder = General.plugin.getDataFolder();
		if(!dataFolder.exists()) dataFolder.mkdirs();
		String lang = Option.LANGUAGE.get();
		langFile = "messages_" + lang + ".yml";
		config = LanguageText.setLanguage(lang, dataFolder, langFile);
		List<String> names = config.getStringList("colours");
		if(names == null) names = Arrays.asList(defaultColours);
		for(int i = 0; i < 16; i++)
			colours.put(names.get(i), ChatColor.getByChar(Integer.toHexString(i)));
		colours.put(names.get(16), ChatColor.GRAY);
		colours.put(names.get(17), ChatColor.MAGIC);
	}
	
	public static void save() {
		try {
			config.save(new File(General.plugin.getDataFolder(), langFile));
		} catch(IOException e) { // TODO: LanguageText
			General.logger.warn("Error saving config.yml: " + e.getMessage());
		}
	}
	
	public static void send(CommandSender who, LanguageText simple) {
		send(who, simple.value());
	}
	
	public static void send(CommandSender who, String string) {
		String coloured = MappedMessageFormat.format(string, colours);
		for(String line : splitLines(coloured))
			who.sendMessage(line);
	}
	
	public static void broadcast(String string) {
		for(Player p : Bukkit.getOnlinePlayers())
			send(p, string);
		send(Bukkit.getConsoleSender(), string);
	}
	
	/**
	 * Formats a string using a pattern similar to that used by java.text.MessageFormat, except
	 * that field names are used in place of numeric indices.
	 * 
	 * @author Celtic Minstrel
	 * @param format The format string
	 * @param args A list of arguments in the order key, arg, key arg; if an odd number, the last argument
	 * fills in for unknown arguments
	 * @return The formatted string
	 */
	public static String format(String format, Object... args) {
		HashMap<String, Object> keyArgs = new HashMap<String, Object>();
		keyArgs.putAll(colours);
		for(int i = 0; i < args.length; i += 2) {
			if(i == args.length - 1) keyArgs.put(null, args[i]);
			else keyArgs.put(args[i].toString(), args[i+1]);
		}
		return format(format, keyArgs);
	}
	
	public static String format(String format, Map<String,Object> args) {
		return MappedMessageFormat.format(format, args).toString();
	}
	
	/**
	 * Splits a message into lines that will fit in the chat window. Colour codes, as indicated by §[0-9a-f], are not
	 * counted in the line length. Make sure you pass through colourize() first to convert the colour codes to the §
	 * syntax.
	 * 
	 * Splitting at a space or hyphen will be preferred. Any newlines already present in the string will be
	 * preserved. Colour codes will be duplicated at the beginning of wrapped lines.
	 * 
	 * @author Celtic Minstrel
	 * @param original The string to split into lines.
	 * @return A list of the lines.
	 */
	public static List<String> splitLines(String original) {
		List<String> lines = new ArrayList<String>();
		for(String line : original.split("[\\n\\r][\\n\\r]?")) {
			while(!line.isEmpty()) {
				String[] split = TextWrapper.wrapText(line);
				if(split.length > 1) {
					String quotient, remainder;
					int i = split[0].lastIndexOf(' '), j = split[0].lastIndexOf('-');
					if(i > -1 || j > -1) {
						quotient = split[0].substring(0, max(i,j) + 1);
						remainder = split[0].substring(max(i,j) + 1);
						int c = split[0].lastIndexOf('\u00A7'); // §
						if(c > -1) {
							char clr = split[0].charAt(c + 1);
							if(clr != 'f' && clr != 'F')
								remainder = "\u00A7" + clr + remainder;
						}
					} else {
						quotient = split[0];
						remainder = Toolbox.join(split, "", 1);
					}
					line = remainder + line.replaceFirst(quotient, "");
					lines.add(quotient);
				} else if(split.length > 0) {
					lines.add(split[0]);
					line = "";
				}
			}
		}
		return lines;
	}
	
	public static boolean invalidPlayer(CommandSender from, String name) {
		String ifNone = LanguageText.MISC_BAD_PLAYER.value("name", name).toString();
		send(from, ifNone);
		return true;
	}
	
	public static boolean invalidWorld(CommandSender from, String name) {
		String ifNone = LanguageText.MISC_BAD_WORLD.value("name", name).toString();
		send(from, ifNone);
		return true;
	}
	
	public static boolean invalidNumber(CommandSender from, String str) {
		send(from, LanguageText.MISC_BAD_NUMBER.value("num", str));
		return true;
	}
	
	public static boolean lacksPermission(CommandSender from, String node) {
		LanguageText action = LanguageText.byNode(node.replace('.', '_').replace("general_", "permissions."));
		return lacksPermission(from, node, action);
	}
	
	public static boolean inCooldown(CommandSender from, String perm, LanguageText node, Object... params) {
		long ticks = Toolbox.getCooldown(from, perm);
		String time = Time.formatDuration(ticks), action = node.value(params);
		send(from, LanguageText.IN_COOLDOWN.value("action", action, "ticks", ticks, "duration", time));
		return true;
	}
	
	public static boolean lacksPermission(CommandSender from, String node, LanguageText action, Object... args) {
		send(from, LanguageText.PERMISSION_LACK.value("action", action.value(args), "permission", node));
		return true;
	}

	public static void showCost(Player sender) {
		if(Option.NO_ECONOMY.get()) return;
		String cost = EconomyManager.formatCost(sender, EconomyManager.getLastPrice());
		send(sender, LanguageText.ECONOMY_SHOW_COST.value("cost", cost));
	}
	
	public static void showPayment(Player sender) {
		if(Option.NO_ECONOMY.get()) return;
		if(EconomyManager.getLastPrice() > 0) {
			String cost = EconomyManager.formatCost(sender, EconomyManager.getLastPrice());
			send(sender, LanguageText.ECONOMY_PAY.value("cost", cost));
		}
	}

	public static void earned(Player who, double revenue) {
		if(Option.NO_ECONOMY.get()) return;
		send(who, LanguageText.ECONOMY_EARN.value("income", revenue));
	}
}
