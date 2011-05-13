
package net.craftstars.general.command.teleport;

import java.util.Formatter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.teleport.Destination;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class spawnCommand extends CommandBase {
	protected spawnCommand(General instance) {
		super(instance);
	}

	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.spawn"))
			return Messaging.lacksPermission(sender, "see the spawn location");
		World dest;
		switch(args.length) {
		case 0: // /spawn
			dest = sender.getWorld();
		break;
		case 1: // /spawn <world|player>
			dest = Toolbox.matchWorld(args[0]);
			if(dest == null) {
				Player player = Toolbox.matchPlayer(args[0]);
				if(player == null) return Messaging.invalidPlayer(sender, args[0]);
				if(!player.equals(sender) && Toolbox.lacksPermission(sender, "general.spawn.home.other"))
					return Messaging.lacksPermission(sender, "see a player's home location");
				else if(Toolbox.lacksPermission(sender, "general.spawn.home"))
					return Messaging.lacksPermission(sender, "see your home location");
				showHome(player, sender);
				return true;
			}
		break;
		default:
			return SHOW_USAGE;
		}
		showSpawn(dest, sender);
		return true;
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		World dest;
		switch(args.length) {
		case 1: // /spawn <world>
			dest = plugin.getServer().getWorld(args[0]);
			if(dest == null) {
				Player player = Toolbox.matchPlayer(args[0]);
				if(player == null) return true;
				showHome(player, sender);
			} else showSpawn(dest, sender);
		break;
		default:
			return SHOW_USAGE;
		}
		return true;
	}
	
	private void showHome(Player player, CommandSender toWhom) {
		Destination dest = Destination.homeOf(player);
		Location pos = dest.getLoc();
		Formatter fmt = new Formatter();
		String message = fmt.format("&eCurrent home location of player '&f%s&e' is &f(%d,%d,%d)", player.getName(),
				pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()).toString();
		Messaging.send(toWhom, message);
		
	}
	
	private void showSpawn(World which, CommandSender toWhom) {
		Location pos = which.getSpawnLocation();
		Formatter fmt = new Formatter();
		String message = fmt.format("&eCurrent spawn location in world '&f%s&e' is &f(%d,%d,%d)", which.getName(),
				pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()).toString();
		Messaging.send(toWhom, message);
	}
}
