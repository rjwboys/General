
package net.craftstars.general.command.teleport;

import java.util.Formatter;

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
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class setspawnCommand extends CommandBase {
	public setspawnCommand(General instance) {
		super(instance);
	}

	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		Destination dest;
		switch(args.length) {
		case 0: // /setspawn
			if(Toolbox.lacksPermission(sender, "general.setspawn.world", "general.spawn.set"))
				return Messaging.lacksPermission(sender, "set the spawn location");
			dest = Destination.locOf(sender);
			setSpawn(sender, dest, sender.getWorld());
		break;
		case 1: // /setspawn <destination>
			if(Toolbox.lacksPermission(sender, "general.setspawn.world", "general.spawn.set"))
				return Messaging.lacksPermission(sender, "set the spawn location");
			dest = Destination.get(args[1], sender);
			if(dest == null) return true;
			setSpawn(sender, dest, sender.getWorld());
		break;
		case 2: // /setspawn <player> <destination>
			Player who;
			if(Toolbox.equalsOne(args[0], "self", "$self", "me", sender.getName())) {
				if(Toolbox.lacksPermission(sender, "general.setspawn.self", "general.spawn.home"))
					return Messaging.lacksPermission(sender, "set your home location");
				who = sender;
			} else {
				if(Toolbox.lacksPermission(sender, "general.setspawn.other", "general.spawn.home.other"))
					return Messaging.lacksPermission(sender, "set someone else's home location");
				who = Toolbox.matchPlayer(args[0]);
			}
			if(who == null) return Messaging.invalidPlayer(sender, args[0]);
			dest = Destination.get(args[1], sender);
			setHome(sender, dest, who);
			return true;
		default:
			return SHOW_USAGE;
		}
		return true;
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		Destination dest;
		switch(args.length) {
		case 1: // /setspawn <destination>
			dest = Destination.get(args[1], null);
			if(dest == null) return true;
			setSpawn(sender, dest, null);
		break;
		case 2: // /setspawn <player> <destination>
			Player who = Toolbox.matchPlayer(args[0]);
			if(who == null) return Messaging.invalidPlayer(sender, args[0]);
			dest = Destination.get(args[1], null);
			setHome(sender, dest, who);
		break;
		default:
			return SHOW_USAGE;
		}
		return true;
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
