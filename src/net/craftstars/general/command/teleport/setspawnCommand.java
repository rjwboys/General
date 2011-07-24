
package net.craftstars.general.command.teleport;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.teleport.Destination;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;
import net.minecraft.server.ChunkCoordinates;
import net.minecraft.server.EntityPlayer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class setspawnCommand extends CommandBase {
	public setspawnCommand(General instance) {
		super(instance);
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Destination dest;
		switch(args.length) {
		case 0: // /setspawn
			if(!isPlayer) return null;
			params.put("dest", Destination.locOf((Player) sender));
			params.put("world", ((Player) sender).getWorld());
		break;
		case 1: // /setspawn <destination>
			dest = Destination.get(args[1], isPlayer ? (Player) sender : null);
			if(dest == null) return null;
			setSpawn(sender, dest, isPlayer ? ((Player) sender).getWorld() : null);
		break;
		case 2: // /setspawn <player> <destination>
			if(isPlayer && Toolbox.equalsOne(args[0], "self", "$self", "me", ((Player) sender).getName())) {
				params.put("player", sender);
				params.put("other", false);
			} else {
				params.put("player", Toolbox.matchPlayer(args[0]));
				params.put("other", true);
			}
			Player who = (Player) params.get("player");
			if(who == null) {
				Messaging.invalidPlayer(sender, args[0]);
				return null;
			}
			params.put("dest", Destination.get(args[1], isPlayer ? (Player) sender : null));
			setCommand("sethome");
		break;
		default:
			return null;
		}
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(command.equals("setspawn")) {
			if(Toolbox.lacksPermission(sender, "general.setspawn.world", "general.spawn.set"))
				return Messaging.lacksPermission(sender, "set the spawn location");
			Destination dest = (Destination) args.get("dest");
			World world = (World) args.get("world");
			setSpawn(sender, dest, world);
		} else if(command.equals("sethome")) {
			Destination dest = (Destination) args.get("dest");
			Player who = (Player) args.get("player");
			boolean other = (Boolean) args.get("other");
			if(other) {
				if(Toolbox.lacksPermission(sender, "general.setspawn.other", "general.spawn.home.other"))
					return Messaging.lacksPermission(sender, "set someone else's home location");
			} else if(Toolbox.lacksPermission(sender, "general.setspawn.self", "general.spawn.home"))
				return Messaging.lacksPermission(sender, "set your home location");
			setHome(sender, dest, who);
		}
		return SHOW_USAGE;
	}
	
	private void setHome(CommandSender sender, Destination dest, Player who) {
		if(sender instanceof Player) {
			Player setter = (Player) sender;
			String targetCost;
			if(setter.equals(who))
				targetCost = "economy.setspawn.self";
			else targetCost = "economy.setspawn.other";
			if(cannotPay(dest, setter, targetCost)) return;
		}
		Location loc = dest.getLoc();
		// Begin accessing Minecraft internals
		// TODO Rewrite to use Bukkit API
		CraftPlayer cp = (CraftPlayer) who;
		EntityPlayer ep = cp.getHandle();
		ChunkCoordinates coords = new ChunkCoordinates(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		ep.a(coords);
		// End accessing Minecraft internals
		Formatter fmt = new Formatter();
		String feedback = "&eHome position of player '&f%s&e' changed to &f(%d,%d,%d)";
		feedback = fmt.format(feedback, who.getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).toString();
		Messaging.send(sender, feedback);
	}

	private boolean cannotPay(Destination dest, Player setter, String targetCost) {
		String[] costs = dest.getCostClasses(setter, "economy.setspawn");
		costs = Toolbox.arrayCopy(costs, 0, new String[costs.length+1], 1, costs.length);
		costs[0] = targetCost;
		if(!Toolbox.canPay(setter, 1, costs)) return true;
		return false;
	}
	
	private void setSpawn(CommandSender sender, Destination dest, World from) {
		if(sender instanceof Player) {
			Player setter = (Player) sender;
			if(cannotPay(dest, setter, "economy.setspawn.world")) return;
		}
		Formatter fmt = new Formatter();
		General.logger.debug("Checking more permission...");
		if(dest.hasPermission(sender, "set the spawn location", "general.spawn.set")) {
			Location loc = dest.getLoc();
			World world = loc.getWorld();
			String feedback;
			if(from == null || !world.equals(from)) {
				feedback = "&eSpawn position in world '&f%s&e' changed to &f(%d,%d,%d)";
			} else {
				feedback = "&eSpawn position changed to &f(%2$d,%3$d,%4$d)";
			}
			Player who = dest.getPlayer();
			if(who != null && loc.equals(who.getLocation())) {
				if(who.equals(sender)) {
					feedback = "&eSpawn position changed to where you are standing.";
				} else {
					feedback = "&eSpawn position changed to where " + who.getDisplayName() + " is standing.";
				}
			}
			General.logger.debug("Finally setting...");
			if(!world.setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
				feedback = "&rose;There was an error setting the spawn location. It has not been changed.";
			feedback = fmt.format(feedback, world.getName(), loc.getBlockX(), loc.getBlockY(),
				loc.getBlockZ()).toString();
			Messaging.send(sender, feedback);
		}
	}
}
