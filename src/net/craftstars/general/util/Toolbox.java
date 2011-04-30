
package net.craftstars.general.util;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

import net.craftstars.general.General;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class Toolbox {
	public static Player playerMatch(String name) {
		if(General.plugin.getServer().getOnlinePlayers().length < 1) {
			return null;
		}
		
		Player[] online = General.plugin.getServer().getOnlinePlayers();
		
		Player lastPlayer = null;
		
		for(Player player : online) {
			String playerName = player.getName();
			String playerDisplayName = player.getDisplayName();
			
			if(playerName.equalsIgnoreCase(name)) {
				lastPlayer = player;
				
				break;
			} else if(playerDisplayName.equalsIgnoreCase(name)) {
				lastPlayer = player;
				
				break;
			}
			if(playerName.toLowerCase().indexOf(name.toLowerCase()) != -1) {
				if(lastPlayer != null) {
					return null;
				}
				
				lastPlayer = player;
			} else if(playerDisplayName.toLowerCase().indexOf(name.toLowerCase()) != -1) {
				if(lastPlayer != null) {
					return null;
				}
				
				lastPlayer = player;
			}
		}
		
		return lastPlayer;
	}
	
	public static boolean equalsOne(String what, String... choices) {
		for(String thisOne : choices) {
			if(what.equalsIgnoreCase(thisOne)) return true;
		}
		return false;
	}
	
	public static String repeat(char c, int i) {
		String tst = "";
		for(int j = 0; j < i; j++) {
			tst = tst + c;
		}
		
		return tst;
	}
	
	public static String string(int i) {
		return String.valueOf(i);
	}
	
	/**
	 * Turns "SomeName" into "Some Name" or "MyABC" into "My ABC". (Inserts a space before a capital letter unless it
	 * is at the beginning of the string or preceded by a capital letter.) Also turns "SOME_NAME" into "Some Name".
	 * 
	 * @param str The string to expand.
	 * @return The expanded string.
	 */
	public static String formatItemName(String str) {
		String newStr = "";
		str = str.replace("_", " ");
		
		for(int i = 0; i < str.length(); i++) {
			if(i > 0 && Character.isUpperCase(str.charAt(i)) && !Character.isUpperCase(str.charAt(i - 1)))
				newStr += ' ';
			newStr += str.charAt(i);
		}
		
		String[] words = newStr.trim().split("\\s+");
		newStr = "";
		for(String word : words) {
			newStr += ' ';
			newStr += word.substring(0, 1).toUpperCase();
			if(word.length() == 1) continue;
			newStr += word.substring(1).toLowerCase();
		}
		
		return newStr.trim();
	}
	
	public static Player getPlayer(String name, CommandSender fromWhom) {
		Player who = Toolbox.playerMatch(name);
		if(who == null) {
			Formatter fmt = new Formatter();
			String ifNone = fmt.format("&rose;There is no player named &f%s&rose;.", name).toString();
			Messaging.send(fromWhom, ifNone);
		}
		return who;
	}
	
	public static World getWorld(String name, CommandSender fromWhom) {
		World theWorld = General.plugin.getServer().getWorld(name);
		if(theWorld == null) {
			Formatter fmt = new Formatter();
			String ifNone = fmt.format("&rose;There is no world named &f%s&rose;.", name).toString();
			Messaging.send(fromWhom, ifNone);
		}
		return theWorld;
	}
	
	public static boolean lacksPermission(General plugin, CommandSender sender, String message,
			String... permissions) {
		if(sender instanceof ConsoleCommandSender)
			return false;
		else if(! (sender instanceof Player)) return true; // TODO: Some allowance for non-player-or-console
															// permissions?
		Player who = (Player) sender;
		if(message == null) message = "do that";
		boolean foundPermission = false;
		for(String permission : permissions) {
			if(plugin.permissions.hasPermission(who, permission)) foundPermission = true;
		}
		if(!foundPermission) {
			Messaging.send(who, "&rose;You don't have permission to " + message + ".");
			return true;
		}
		return false;
	}
	
	public static Location getLocation(CommandSender fromWhom, World which, String xCoord, String yCoord,
			String zCoord) {
		int x, y, z;
		try {
			x = Integer.valueOf(xCoord);
			y = Integer.valueOf(yCoord);
			z = Integer.valueOf(zCoord);
			return new Location(which, x, y, z);
		} catch(NumberFormatException ex) {
			Messaging.send(fromWhom, "&rose;Invalid number.");
			return null;
		}
	}
	
	public static String combineSplit(String[] args, int startAt) {
		StringBuilder message = new StringBuilder();
		for(int i = startAt; i < args.length; i++) {
			message.append(args[i]);
			message.append(" ");
		}
		return message.toString();
	}
	
	public static List<String> getPlayerList(General plugin) {
		Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
		List<String> players = new ArrayList<String>();
		
		for(Player who : onlinePlayers) {
			players.add(who.getName());
		}
		
		return players;
	}
	
	public static void showFile(CommandSender sender, Scanner f, boolean motd) {
		while(f.hasNextLine()) {
			String line = f.nextLine();
			if(motd) line = MessageOfTheDay.parseMotD(sender, line);
			Messaging.send(sender, line);
		}
	}
	
	public static Location getTargetBlock(Player sender) {
		Location where = null;
		BlockIterator iter = new BlockIterator(sender);
		Block block;
		for(block = iter.next(); !isSolid(block) && iter.hasNext(); block = iter.next());
		if(isSolid(block) && !isSolid(block.getRelative(BlockFace.UP)))
			where = block.getRelative(BlockFace.UP).getLocation();
		if(where == null) where = sender.getLocation();
		return where;
	}
	
	public static boolean isSolid(Block block) {
		if(block == null) return false;
		Material mat = block.getType();
		if(mat.getId() >= 256) return true; // should never happen, but just in case...
		switch(mat) {
		case AIR:
		case SAPLING:
		case WATER:
		case STATIONARY_WATER:
		case LAVA:
		case STATIONARY_LAVA:
		case POWERED_RAIL:
		case DETECTOR_RAIL:
		case WEB:
		case YELLOW_FLOWER:
		case RED_ROSE:
		case BROWN_MUSHROOM:
		case RED_MUSHROOM:
		case FIRE:
		case REDSTONE_WIRE:
		case CROPS:
		case SIGN_POST:
		case WOODEN_DOOR:
		case LADDER:
		case RAILS:
		case WALL_SIGN:
		case LEVER:
		case STONE_PLATE:
		case IRON_DOOR_BLOCK:
		case WOOD_PLATE:
		case REDSTONE_TORCH_OFF:
		case REDSTONE_TORCH_ON:
		case STONE_BUTTON:
		case SNOW:
		case SUGAR_CANE_BLOCK:
		case PORTAL:
		case DIODE_BLOCK_ON:
		case DIODE_BLOCK_OFF:
			return false;
		default:
			return true;
		}
	}
}
