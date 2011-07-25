
package net.craftstars.general.util;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.craftstars.general.General;
import net.craftstars.general.money.AccountStatus;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class Messaging {
	private static HashMap<String, Object> colours = new HashMap<String, Object>();
	private static Configuration config;
	static final String[] defaultColours = {
		"black", "navy", "green", "teal", "red", "purple", "gold", "silver",
		"grey", "blue", "lime", "aqua", "rose", "pink", "yellow", "white", "gray"
	};
	
	public static void load() {
		File dataFolder = General.plugin.getDataFolder();
		if(!dataFolder.exists()) dataFolder.mkdirs();
		File configFile = new File(dataFolder, "messages.yml");
		config = new Configuration(configFile);
		List<String> names = config.getStringList("colours", Arrays.asList(defaultColours));
		for(int i = 0; i < 16; i++)
			colours.put(names.get(i), ChatColor.getByCode(i));
		colours.put(names.get(16), ChatColor.GRAY);
	}
	
	public static void send(CommandSender who, String string) {
		String coloured = colourize(string);
		coloured = substitute(coloured, new String[] {"&&","~!@#$%^&*()"}, new String[] {"~!@#$%^&*()","&"});
		coloured = MessageFormat.format(coloured, colours);
		for(String line : splitLines(coloured).split("[\\n\\r][\\n\\r]?"))
			who.sendMessage(line);
	}
	
	@Deprecated // in favour of format
	public static String colourize(String string) {
		String coloured = substitute(string,
				new String[] {
					"&black;,&0","&navy;,&1","&green;,&2","&teal;,&3",
					"&red;,&4","&purple;,&5","&gold;,&6","&silver;,&7",
					"&gray;,&grey;,&8","&blue;,&9","&lime;,&a,&A","&aqua;,&b,&B",
					"&rose;,&c,&C","&pink;,&d,&D","&yellow;,&e,&E","&white;,&f,&F"
				},
				ChatColor.values()
		);
		return coloured;
	}
	
	public static void broadcast(String string) {
		Server mc = General.plugin.getServer();
		for(Player p : mc.getOnlinePlayers())
			send(p, string);
		send(new ConsoleCommandSender(mc), string);
	}
	
	public static String get(String key, String defaultVal) {
		return General.plugin.config.getString("messages." + key, defaultVal);
	}
	
	/**
	 * Substitutes values for variables in a string. If there are more values than arguments, the excess are ignored;
	 * if there are more arguments than variables, the extras are assumed to be the empty string.
	 * 
	 * @deprecated in favour of format
	 * @author Celtic Minstrel
	 * @param format The format string
	 * @param arguments A list of arguments and comma-separated lists of equivalent arguments
	 * @param values A list of values to substitute for each respective argument
	 * @return The resulting string
	 */
	@Deprecated
	public static String substitute(String format, String[] arguments, Object[] values) {
		for(int i = 0; i < arguments.length; i++) {
			String subst = i < values.length ? values[i].toString() : "";
			if(arguments[i].contains(",")) {
				String[] args = arguments[i].split(",");
				for(String arg : args)
					format = format.replace(arg, subst);
			} else format = format.replace(arguments[i], subst);
		}
		return format;
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
		return new MappedMessageFormat(format).format(args).toString();
	}
	
	/**
	 * Splits a message into lines of no more than 54 characters. Colour codes, as indicated by §[0-9a-f], are not
	 * counted in the line length. Make sure you pass through colourize() first to convert the colour codes to the §
	 * syntax.
	 * 
	 * Splitting at a space or hyphen will be preferred. Any newlines already present in the string will be
	 * preserved. Colour codes will be duplicated at the beginning of wrapped lines.
	 * 
	 * @author Celtic Minstrel
	 * @param original The string to split into lines.
	 * @return The string with newlines inserted as required.
	 */
	public static String splitLines(String original) {
		if(original.contains("\r") || original.contains("\n")) {
			String[] lines = original.split("\n|\r|\n\r|\r\n");
			String joined = "";
			for(String line : lines)
				joined += splitLines(line) + '\n';
			return joined.substring(0, joined.length() - 2);
		}
		StringBuilder splitter = new StringBuilder(original);
		int splitAt = 0;
		int effectiveLen = 0;
		char lastColourCode = ' ';
		for(int i = 0; i < splitter.length(); i++) {
			if(splitter.charAt(i) == '\u00A7') { // §
				try {
					char c = splitter.charAt(i + 1);
					if( (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
						lastColourCode = c;
						i++;
						continue;
					}
				} catch(IndexOutOfBoundsException x) {

				}
			}
			effectiveLen++;
			char c = splitter.charAt(i);
			if(c == ' ' || c == '-') splitAt = i;
			if(effectiveLen > 60) {
				if(splitAt == 0) splitAt = i; // as a last resort, just split at the limit
				effectiveLen = i - splitAt;
				String toAdd = "\n";
				if(lastColourCode != ' ') {
					toAdd += '\u00A7';
					toAdd += lastColourCode;
					i += 2;
				}
				splitter.insert(splitAt + 1, toAdd);
				if(splitter.charAt(splitAt) == ' ')
					splitter.deleteCharAt(splitAt);
				else i++;
				splitAt = 0;
			}
		}
		return splitter.toString();
	}
	
	public static boolean invalidPlayer(CommandSender from, String name) {
		Formatter fmt = new Formatter();
		String ifNone = fmt.format("&rose;There is no player named &f%s&rose;.", name).toString();
		Messaging.send(from, ifNone);
		return true;
	}
	
	public static boolean invalidWorld(CommandSender from, String name) {
		Formatter fmt = new Formatter();
		String ifNone = fmt.format("&rose;There is no world named &f%s&rose;.", name).toString();
		Messaging.send(from, ifNone);
		return true;
	}
	
	public static boolean invalidNumber(CommandSender from, String str) {
		Messaging.send(from, "&rose;Invalid number " + str);
		return true;
	}
	
	public static boolean lacksPermission(CommandSender from, String message) {
		Messaging.send(from, "&rose;You don't have permission to " + message + ".");
		return true;
	}
	
	public static void showCost(Player sender) {
		String cost = General.plugin.economy.formatCost(AccountStatus.price);
		Messaging.send(sender, "&eThat would cost " + cost + ".");
	}
	
	public static void showPayment(Player sender) {
		if(AccountStatus.price > 0) {
			String cost = General.plugin.economy.formatCost(AccountStatus.price);
			Messaging.send(sender, "&eYou pay " + cost + ".");
		}
	}

	public static void earned(Player who, double revenue) {
		Messaging.send(who, "You have earned " + General.plugin.economy.formatCost(revenue) + "!");
	}
}
