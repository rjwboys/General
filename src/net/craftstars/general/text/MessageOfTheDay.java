
package net.craftstars.general.text;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

import net.craftstars.general.General;
import net.craftstars.general.util.Time;
import net.craftstars.general.util.Toolbox;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MessageOfTheDay {
	public static String parseMotD(CommandSender sender, String original) {
		String displayName = getDisplayName(sender), name = getName(sender), location = getLocation(sender);
		double health = getHealth(sender);
		String address = getAddress(sender), balance = getBalance(sender), currency = getCurrency(sender);
		int numPlayers = General.plugin.getServer().getOnlinePlayers().length;
		String online = getOnline(), world = getWorld(sender), time = getTime(sender);
		return Messaging.format(original, 
			"dname", displayName, "name", name, "location", location, "health", health,
			"ip", address, "balance", balance, "currency", currency, "online", numPlayers,
			"list", online, "world", world, "time", time
		);
	}
	
	private static String getOnline() {
		List<String> lines = formatPlayerList(Toolbox.getPlayerList(General.plugin, null));
		StringBuilder stuff = new StringBuilder();
		for(String line : lines)
			stuff.append(line + " ");
		return stuff.toString();
	}
	
	public static List<String> formatPlayerList(List<String> players) {
		List<String> list = new ArrayList<String>();
		if(players.size() == 0) {
			list.add(LanguageText.MOTD_NOONE.value());
			return list;
		}
		StringBuilder playerList = new StringBuilder();
		for(String who : players) {
			if(playerList.length() + who.length() > 54) {
				list.add(playerList.toString());
				playerList.setLength(0);
			}
			playerList.append(who);
			playerList.append(", ");
		}
		int i = playerList.lastIndexOf(", ");
		if(i > 0) playerList.delete(i, playerList.length());
		list.add(playerList.toString());
		return list;
	}
	
	private static String getCurrency(CommandSender sender) {
		if(General.economy == null) return "none";
		Player player = sender instanceof Player ? (Player)sender : null;
		String zero = General.economy.getFormattedAmount(player, 0, -1);
		String currency = zero.replaceAll("\\d+(\\.\\d+)?", "");
		return currency;
	}
	
	private static String getAddress(CommandSender sender) {
		if(sender instanceof Player) return ((Player) sender).getAddress().getAddress().getHostAddress();
		return "127.0.0.1";
	}
	
	private static String getBalance(CommandSender sender) {
		if(General.economy == null || ! (sender instanceof Player)) return "0";
		return Double.toString(General.economy.getBalance((Player) sender, -1));
	}
	
	private static double getHealth(CommandSender sender) {
		if(sender instanceof Player) return ((Player) sender).getHealth() / 2.0;
		return 0;
	}
	
	private static String getTime(CommandSender sender) {
		if(sender instanceof Player) {
			long t = ((Player) sender).getWorld().getTime();
			return Time.formatTime(t);
		}
		return LanguageText.MOTD_UNKNOWN.value();
	}
	
	private static String getLocation(CommandSender sender) {
		if(sender instanceof Player) {
			Formatter fmt = new Formatter();
			Location loc = ((Player) sender).getLocation();
			return fmt.format("(%d, %d, %d)", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).toString();
		}
		return LanguageText.MOTD_UNKNOWN.value();
	}
	
	private static String getWorld(CommandSender sender) {
		if(sender instanceof Player) return ((Player) sender).getWorld().getName();
		return LanguageText.MOTD_UNKNOWN.value();
	}
	
	private static String getDisplayName(CommandSender sender) {
		if(sender instanceof Player) return ((Player) sender).getDisplayName();
		return "CONSOLE";
	}
	
	private static String getName(CommandSender sender) {
		if(sender instanceof Player) return ((Player) sender).getName();
		return "CONSOLE";
	}
	
	public static void showMotD(CommandSender sender) {
		File dataFolder = General.plugin.getDataFolder();
		if(!dataFolder.exists()) dataFolder.mkdirs();
		Scanner f;
		try {
			File helpFile = new File(dataFolder, "general.motd");
			f = new Scanner(helpFile);
		} catch(FileNotFoundException e) {
			Messaging.send(sender, LanguageText.MOTD_UNAVAILABLE);
			return;
		}
		Toolbox.showFile(sender, f, true);
	}
}
