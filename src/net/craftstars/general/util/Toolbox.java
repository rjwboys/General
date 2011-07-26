
package net.craftstars.general.util;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.money.AccountStatus;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.config.Configuration;

public class Toolbox {
	public static Player matchPlayer(String pat) {
		Player[] players = General.plugin.getServer().getOnlinePlayers();
		if(players.length == 0) return null;
		HashMap<Player,Integer> closeness = new HashMap<Player,Integer>();
		for(Player p : players) {
			String name = p.getName();
			String dname = p.getDisplayName();
			if(name.equalsIgnoreCase(pat) || dname.equalsIgnoreCase(pat)) {
				closeness.put(p, 0);
			} else if(name.toLowerCase().startsWith(pat.toLowerCase())) {
				closeness.put(p, name.length() - pat.length());
			} else if(dname.toLowerCase().startsWith(pat.toLowerCase())) {
				closeness.put(p, dname.length() - pat.length());
			} else if(name.toLowerCase().contains(pat.toLowerCase())) {
				closeness.put(p, 1 + name.length() - pat.length());
			} else if(dname.toLowerCase().contains(pat.toLowerCase())) {
				closeness.put(p, 1 + dname.length() - pat.length());
			}
		}
		int min = Integer.MAX_VALUE;
		for(int i : closeness.values())
			if(i < min) min = i;
		ArrayList<Player> closest = new ArrayList<Player>();
		for(Player p : closeness.keySet())
			if(closeness.get(p) == min) closest.add(p);
		// If there are multiple equally close matches, we'll make no attempt to resolve
		if(closest.size() == 1) return closest.get(0);
		return null;
	}
	
	public static World matchWorld(String pat) {
		List<World> worlds = General.plugin.getServer().getWorlds();
		if(worlds.size() == 0) return null;
		HashMap<World,Integer> closeness = new HashMap<World,Integer>();
		for(World w : worlds) {
			String name = w.getName();
			if(name.equalsIgnoreCase(pat)) {
				closeness.put(w, 0);
			} else if(name.toLowerCase().startsWith(pat.toLowerCase())) {
				closeness.put(w, name.length() - pat.length());
			} else if(name.toLowerCase().contains(pat.toLowerCase())) {
				closeness.put(w, 1 + name.length() - pat.length());
			}
		}
		int min = Integer.MAX_VALUE;
		for(int i : closeness.values())
			if(i < min) min = i;
		ArrayList<World> closest = new ArrayList<World>();
		for(World p : closeness.keySet())
			if(closeness.get(p) == min) closest.add(p);
		// If there are multiple equally close matches, we'll make no attempt to resolve
		if(closest.size() == 1) return closest.get(0);
		return null;
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
		if(str == null) return "";
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
	
	public static boolean lacksPermission(CommandSender sender, String... permissions) {
		return !hasPermission(sender, permissions);
	}
	
	public static boolean hasPermission(CommandSender sender, String... permissions) {
		if(sender instanceof ConsoleCommandSender)
			return true;
		else if(!(sender instanceof Player)) return false; // TODO: Some allowance for non-player-or-console permissions?
		Player who = (Player) sender;
		boolean foundPermission = false;
		for(String permission : permissions) {
			if(General.plugin.permissions.hasPermission(who, permission)) foundPermission = true;
		}
		return foundPermission;
	}
	
	private static AccountStatus hasFunds(CommandSender sender, int quantity, String... permissions) {
		if(hasPermission(sender, "general.no-money") || sender instanceof ConsoleCommandSender)
			return AccountStatus.BYPASS;
		Player player = (Player) sender;
		AccountStatus.price = 0;
		for(String permission : permissions)
			if(permission.startsWith("$"))
				AccountStatus.price += Double.parseDouble(permission.substring(1));
			else if(permission.startsWith("%"))
				AccountStatus.price *= Double.parseDouble(permission.substring(1)) / 100.0;
			else AccountStatus.price += Option.ECONOMY_COST(permission).get() * quantity;
		if(CommandBase.isFrozen(player)) return AccountStatus.FROZEN;
		if(General.plugin.economy.getBalance(player) >= AccountStatus.price)
			return AccountStatus.SUFFICIENT;
		return AccountStatus.INSUFFICIENT;
	}
	
	public static boolean canPay(CommandSender sender, int quantity, String... permissions) {
		//General.logger.debug("Checking cost of: " + Arrays.toString(permissions));
		if(General.plugin.economy == null) return true;
		if(sender instanceof ConsoleCommandSender) return true;
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		// Don't want the price to change between checking for funds and removing them!
		synchronized(AccountStatus.class) {
			AccountStatus canPay = Toolbox.hasFunds(player, quantity, permissions);
			switch(canPay) {
			case BYPASS:
				break;
			case FROZEN:
				Messaging.showCost(player);
				return false;
			case INSUFFICIENT:
				Messaging.showCost(player);
				Messaging.send(sender, "&cUnfortunately, you don't have that much.");
				return false;
			case SUFFICIENT:
				Messaging.showPayment(player);
				General.plugin.economy.takePayment(player, AccountStatus.price);
			}
			return true;
		}
	}
	
	public static String join(String[] args, String with, int startAt) {
		if(args.length == 0) return "";
		StringBuilder message = new StringBuilder(args[0]);
		for(int i = startAt + 1; i < args.length; i++) {
			message.append(with);
			message.append(args[i]);
		}
		return message.toString();
	}
	
	public static String join(String[] args, String with) {
		return join(args, with, 0);
	}
	
	public static String join(String[] args, int startAt) {
		return join(args, " ", startAt);
	}
	
	public static String join(String[] args) {
		return join(args, " ");
	}
	
	public static List<String> getPlayerList(General plugin, World world) {
		Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
		List<String> players = new ArrayList<String>();
		
		for(Player who : onlinePlayers) {
			if(world == null || who.getWorld().equals(world))
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
		where = block.getLocation();
		while(isSolid(block) || isSolid(block.getRelative(BlockFace.UP)))
			block = block.getRelative(BlockFace.UP);
		where = block.getLocation();
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

	public static String formatLocation(Location loc) {
		return LanguageText.MISC_LOCATION.value("x", loc.getX(), "y", loc.getY(), "z", loc.getZ(), 
			"yaw", loc.getYaw(), "pitch", loc.getPitch());
	}

	public static boolean nodeExists(Configuration config, String node) {
		Object prop = config.getProperty(node);
		return prop != null;
	}
	
	public static <T> T[] arrayCopy(T[] src, int srcPos, T[] dest, int destPos, int len) {
		System.arraycopy(src, srcPos, dest, destPos, len);
		return dest;
	}

	public static double sellItem(ItemID item, int amount) {
		if(General.plugin.economy == null) return 0;
		String node = "economy.give.item" + item.toString();
		double percent = Option.ECONOMY_SELL.get() / 100.0;
		return Option.ECONOMY_COST(node).get() * amount * percent;
	}

	private static HashMap<String, HashSet<World>> inCooldown = new HashMap<String, HashSet<World>>();
	public static boolean checkCooldown(CommandSender sender, final World world, final String command, String permissionBase) {
		int cooldownTime = Option.COOLDOWN(command).get();
		if(cooldownTime > 0) {
			String instant = permissionBase + ".instant";
			if(Toolbox.hasPermission(sender, instant)) return true;
			if(!inCooldown.containsKey(command)) inCooldown.put(command, new HashSet<World>());
			if(inCooldown.get(command).contains(world)) {
				String langNode = permissionBase.replace('.', '_').replace("general_", "permissions.");
				return Messaging.lacksPermission(sender, instant, LanguageText.LACK_INSTANT,
					"action", LanguageText.byNode(langNode).value());
			}
			inCooldown.get(command).add(world);
			Runnable cooldown = new Runnable() {
				@Override
				public void run() {
					inCooldown.get(command).remove(world);
				}
			};
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(General.plugin, cooldown, cooldownTime);
		}
		return true;
	}

	public static CreatureType getCreatureType(LivingEntity entity) {
		if(entity instanceof Pig) return CreatureType.PIG;
		else if(entity instanceof Sheep) return CreatureType.SHEEP;
		else if(entity instanceof Cow) return CreatureType.COW;
		else if(entity instanceof Chicken) return CreatureType.CHICKEN;
		else if(entity instanceof Creeper) return CreatureType.CREEPER;
		else if(entity instanceof Wolf) return CreatureType.WOLF;
		else if(entity instanceof Squid) return CreatureType.SQUID;
		else if(entity instanceof Skeleton) return CreatureType.SKELETON;
		else if(entity instanceof Slime) return CreatureType.SLIME;
		else if(entity instanceof Spider) return CreatureType.SPIDER;
		else if(entity instanceof Ghast) return CreatureType.GHAST;
		else if(entity instanceof Giant) return CreatureType.GIANT;
		else if(entity instanceof PigZombie) return CreatureType.PIG_ZOMBIE;
		else if(entity instanceof Zombie) return CreatureType.ZOMBIE;
		else if(entity instanceof Monster) return CreatureType.MONSTER;
		return null;
	}
}
