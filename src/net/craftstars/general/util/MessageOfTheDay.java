
package net.craftstars.general.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

import net.craftstars.general.General;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageOfTheDay {
	public static String parseMotD(CommandSender sender, String original) {
		return Messaging.argument(original, new String[] {"++", "+dname,+d,&dname;", "+name,+n,&name;",
				"+location,+l,&location;", "+health,+h,&health;", "+ip,+a,&ip;", "+balance,+$,&balance;",
				"+currency,+m,&currency;", "+online,+c,&online;", "+list,+p,&list;", "+world,+w,&world;",
				"+time,+t,&time;", "~!@#$%^&*()"}, new Object[] {"~!@#$%^&*()", getDisplayName(sender),
				getName(sender), getLocation(sender), getHealth(sender), getAddress(sender), getBalance(sender),
				getCurrency(), General.plugin.getServer().getOnlinePlayers().length, getOnline(), getWorld(sender),
				getTime(sender), "+"});
	}
	
	private static String getOnline() {
		List<String> lines = formatPlayerList(Toolbox.getPlayerList(General.plugin));
		StringBuilder stuff = new StringBuilder();
		for(String line : lines)
			stuff.append(line + " ");
		return stuff.toString();
	}
	
	public static List<String> formatPlayerList(List<String> players) {
		List<String> list = new ArrayList<String>();
		if(players.size() == 0) {
			list.add("(no-one)");
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
	
	private static Object getCurrency() {
		if(General.plugin.economy == null) return "none";
		return General.plugin.economy.getCurrency();
	}
	
	private static String getAddress(CommandSender sender) {
		if(sender instanceof Player) return ((Player) sender).getAddress().getAddress().getHostAddress();
		return "127.0.0.1";
	}
	
	private static String getBalance(CommandSender sender) {
		if(General.plugin.economy == null || ! (sender instanceof Player)) return "0";
		return General.plugin.economy.getBalanceForDisplay((Player) sender);
	}
	
	private static double getHealth(CommandSender sender) {
		if(sender instanceof Player) return ((double) ((Player) sender).getHealth()) / 2.0;
		return 0;
	}
	
	private static String getTime(CommandSender sender) {
		if(sender instanceof Player) {
			long t = ((Player) sender).getWorld().getTime();
			return Time.formatTime(t, Time.currentFormat);
		}
		return "unknown";
	}
	
	private static String getLocation(CommandSender sender) {
		if(sender instanceof Player) {
			Formatter fmt = new Formatter();
			Location loc = ((Player) sender).getLocation();
			return fmt.format("(%d, %d, %d)", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).toString();
		}
		return "null";
	}
	
	private static String getWorld(CommandSender sender) {
		if(sender instanceof Player) return ((Player) sender).getWorld().getName();
		return "null";
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
			Messaging.send(sender, "&rose;No message of the day available.");
			return;
		}
		Toolbox.showFile(sender, f, true);
	}
}
