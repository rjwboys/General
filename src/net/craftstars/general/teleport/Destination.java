
package net.craftstars.general.teleport;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;
import net.minecraft.server.ChunkCoordinates;
import net.minecraft.server.EntityPlayer;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Destination {
	private Location calc;
	private Player keystone;
	private DestinationType[] t;
	private String title;
	private static Pattern locPat = Pattern.compile("([a-zA-Z0-9_-]+)\\(([^,]+),([^,]+),([^)]+)\\)");
	
	private Destination(Location loc, String name, DestinationType... types) {
		calc = loc;
		keystone = null;
		t = types;
		title = name;
	}
	
	private Destination(World world, DestinationType... types) {
		calc = world.getSpawnLocation();
		keystone = null;
		t = types;
		title = world.getName();
	}
	
	private Destination(Player player, DestinationType... types) {
		calc = player.getLocation();
		keystone = player;
		t = types;
		title = player.getDisplayName();
	}
	
	private Destination(Location loc, Player key, String name, DestinationType... types) {
		calc = loc;
		keystone = key;
		t = types;
		title = name;
	}
	
	public Location getLoc() {
		return calc;
	}
	
	public Player getPlayer() {
		return keystone;
	}
	
	public boolean hasPermission(CommandSender sender, String action, String base) {
		if(sender instanceof ConsoleCommandSender) return true;
		if(! (sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(player.isOp()) return true;
		boolean perm = true;
		for(DestinationType type : t) {
			boolean can = type.hasPermission(player, action, base);
			perm = perm && can;
			if(type.isSpecial() && !player.equals(keystone)) {
				boolean canOther = type.hasOtherPermission(player, action, base);
				perm = perm && canOther;
			}
		}
		return perm;
	}
	
	public static Destination get(String dest, Player keystone) {
		Server mc = General.plugin.getServer();
		CommandSender notify;
		if(keystone == null)
			notify = new ConsoleCommandSender(mc);
		else notify = keystone;
		// Is it a player? Optionally prefixed with !
		Player who = Toolbox.matchPlayer(dest.replaceFirst("^!", ""));
		if(who != null) return locOf(who);
		// Is it a world? Optionally prefixed with @
		World globe = Toolbox.matchWorld(dest.replaceFirst("^@", ""));
		if(globe != null) return fromWorld(globe);
		if(keystone != null) {
			// Is it a special keyword? Optionally prefixed with $
			if(Toolbox.equalsOne(dest, "there", "$there")) return targetOf(keystone);
			if(Toolbox.equalsOne(dest, "here", "$here")) return locOf(keystone);
			if(Toolbox.equalsOne(dest, "home", "$home")) return homeOf(keystone);
			if(Toolbox.equalsOne(dest, "spawn", "$spawn")) return spawnOf(keystone);
			if(Toolbox.equalsOne(dest, "compass", "$compass")) return compassOf(keystone);
			// Is it a coordinate? x,y,z
			try {
				String[] split = dest.split(",");
				if(split.length == 3) {
					double x = Double.valueOf(split[0]);
					double y = Double.valueOf(split[1]);
					double z = Double.valueOf(split[2]);
					Location loc = new Location(keystone.getWorld(), x, y, z);
					return new Destination(loc, keystone, dest, DestinationType.COORDS);
				}
			} catch(NumberFormatException e) {}
		}
		// Is it a world + coordinate? world(x,y,z)
		Matcher m = locPat.matcher(dest);
		if(m.matches()) {
			World flat = Toolbox.matchWorld(m.group(1));
			if(flat != null) {
				try {
					double x = Double.valueOf(m.group(2));
					double y = Double.valueOf(m.group(3));
					double z = Double.valueOf(m.group(4));
					Location loc = new Location(flat, x, y, z);
					String name = flat.getName() + " at " + x + "," + y + "," + z;
					return new Destination(loc, name, DestinationType.WORLD, DestinationType.COORDS);
				} catch(NumberFormatException e) {}
			}
		}
		// Is it a player + special keyword? player$keyword
		if(dest.contains("$")) {
			String[] split = dest.split("\\$");
			General.logger.debug(Arrays.asList(split).toString());
			if(split.length == 2) {
				Player target = Toolbox.matchPlayer(split[0]);
				if(split[1].equalsIgnoreCase("there")) return targetOf(target);
				if(split[1].equalsIgnoreCase("home")) return homeOf(target);
				if(split[1].equalsIgnoreCase("spawn")) return spawnOf(target);
				if(split[1].equalsIgnoreCase("compass")) return compassOf(target);
			}
		}
		// Well, nothing matches; give up.
		Messaging.send(notify, "&cInvalid target.");
		return null;
	}
	
	public static Destination fromWorld(World globe) {
		if(globe == null) return null;
		return new Destination(globe, DestinationType.WORLD);
	}
	
	public static Destination targetOf(Player player) {
		if(player == null) return null;
		Location targetBlock = Toolbox.getTargetBlock(player);
		return new Destination(targetBlock, player, player.getDisplayName(), DestinationType.TARGET);
	}
	
	public static Destination locOf(Player player) {
		if(player == null) return null;
		return new Destination(player, DestinationType.PLAYER);
	}
	
	public static Destination spawnOf(Player player) {
		if(player == null) return null;
		Destination d = new Destination(player.getWorld(), DestinationType.SPAWN);
		d.keystone = player;
		return d;
	}
	
	public static Destination compassOf(Player player) {
		if(player == null) return null;
		String name = player.getDisplayName() + "'s compass";
		Destination d = new Destination(player.getCompassTarget(), name, DestinationType.COMPASS);
		d.keystone = player;
		return d;
	}
	
	public static Destination homeOf(Player player) {
		if(player == null) return null;
		// Begin accessing Minecraft code
		// TODO: Rewrite to use Bukkit API
		CraftPlayer cp = (CraftPlayer) player;
		EntityPlayer ep = cp.getHandle();
		ChunkCoordinates coords = ep.M();
		if(coords != null) {
			Location loc = new Location(player.getWorld(), coords.x, coords.y, coords.z);
			String name = player.getDisplayName() + "'s home";
			return new Destination(loc, player, name, DestinationType.HOME);
		}
		// End accessing Minecraft code
		return spawnOf(player);
	}
	
	public String getName() {
		return title;
	}
	
	public void setWorld(World world) {
		calc.setWorld(world);
	}
}
