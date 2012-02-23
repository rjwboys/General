
package net.craftstars.general.text;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import net.craftstars.general.General;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Time;
import net.craftstars.general.util.Toolbox;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public final class MessageOfTheDay {
	private MessageOfTheDay() {}
	
	public static String parseMotD(CommandSender sender, String original) {
		String displayName = getDisplayName(sender), name = getName(sender), location = getLocation(sender);
		double health = getHealth(sender);
		String address = getAddress(sender), balance = getBalance(sender), currency = getCurrency(sender);
		int numPlayers = Bukkit.getOnlinePlayers().length;
		String online = getOnline(), world = getWorld(sender), time = getTime(sender);
		String server = Bukkit.getServerName();
		int level = getLevel(sender);
		return Messaging.format(original,
			"dname", displayName, "name", name, "location", location, "health", health,
			"ip", address, "balance", balance, "currency", currency, "online", numPlayers,
			"list", online, "world", world, "time", time, "server", server, "level", level
		);
	}
	
	private static int getLevel(CommandSender sender) {
		if(sender instanceof Player) return ((Player)sender).getLevel();
		return 0;
	}

	private static String getOnline() {
		List<String> lines = formatPlayerList(Toolbox.getPlayerList(null));
		StringBuilder stuff = new StringBuilder();
		for(String line : lines) stuff.append(line + " ");
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
		if(Option.NO_ECONOMY.get()) return "none";
		Player player = sender instanceof Player ? (Player)sender : null;
		String zero = EconomyManager.formatCost(player, 0);
		String currency = zero.replaceAll("\\d+(\\.\\d+)?", "");
		return currency;
	}
	
	private static String getAddress(CommandSender sender) {
		if(sender instanceof Player) return ((Player) sender).getAddress().getAddress().getHostAddress();
		return "127.0.0.1";
	}
	
	private static String getBalance(CommandSender sender) {
		if(Option.NO_ECONOMY.get() || !(sender instanceof Player)) return "0";
		return Double.toString(EconomyManager.getBalance((Player) sender));
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
		String motd = getMotDFor(sender);
		Scanner f;
		try {
			File helpFile = new File(dataFolder, motd);
			f = new Scanner(helpFile);
		} catch(FileNotFoundException e) {
			Messaging.send(sender, LanguageText.MOTD_UNAVAILABLE);
			return;
		}
		Toolbox.showFile(sender, f, true);
	}

	private static String getMotDFor(CommandSender sender) {
		Set<PermissionAttachmentInfo> perms = sender.getEffectivePermissions();
		for(PermissionAttachmentInfo attachment : perms) {
			String permission = attachment.getPermission();
			if(permission.startsWith("general.motd.") && attachment.getValue()) {
				permission = permission.replace("general.motd.", "");
				return permission + ".motd";
			}
		}
		return "general.motd";
	}
}
