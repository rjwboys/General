
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
			General.logger.debug("Setting spawn...");
			dest = Destination.locOf(sender);
		break;
		case 1: // /setspawn <destination>
			General.logger.debug("Setting spawn to...");
			dest = Destination.get(args[1], sender);
			if(dest == null) return true;
		break;
		case 2: // /setspawn <player> <destination>
			General.logger.debug("Setting home to...");
			Player who;
			if(Toolbox.equalsOne(args[0], "self", "$self", "me")) {
				if(Toolbox.lacksPermission(sender, "general.spawn.home"))
					return Messaging.lacksPermission(sender, "see your home location");
				who = sender;
			} else {
				if(Toolbox.lacksPermission(sender, "general.spawn.home.other"))
					return Messaging.lacksPermission(sender, "see someone else's home location");
				who = Toolbox.matchPlayer(args[0]);
			}
			if(who == null) return Messaging.invalidPlayer(sender, args[0]);
			dest = Destination.get(args[1], sender);
			setHome(sender, dest, who);
			return true;
		default:
			return SHOW_USAGE;
		}
		General.logger.debug("Checking permission...");
		if(Toolbox.lacksPermission(sender, "general.spawn.set"))
			return Messaging.lacksPermission(sender, "see the spawn location");
		General.logger.debug("Setting...");
		setSpawn(sender, dest, sender.getWorld());
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
		// Begin accessing Minecraft internals
		Location loc = dest.getLoc();
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
	
	private void setSpawn(CommandSender sender, Destination dest, World from) {
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
			feedback =
					fmt.format(feedback, world.getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())
							.toString();
			Messaging.send(sender, feedback);
		}
	}
}
