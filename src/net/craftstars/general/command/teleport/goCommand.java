
package net.craftstars.general.command.teleport;

import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.teleport.*;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class goCommand extends CommandBase {
	public goCommand(General instance) {
		super(instance);
	}

	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.teleport"))
			return Messaging.lacksPermission(sender, "teleport");
		Target target;
		Destination dest;
		switch(args.length) {
		case 1: // /tele <destination>
			target = Target.fromPlayer(sender);
			dest = Destination.get(args[0], sender);
		break;
		case 2: // /tele <what> <destination>
			target = Target.get(args[0], sender);
			dest = Destination.get(args[1], sender);
		break;
		default:
			return SHOW_USAGE;
		}
		if(dest == null || target == null) return true;
		if(target.hasPermission(sender) && hasDestPermission(sender, target, dest)) {
			String[] costs = dest.getCostClasses(sender, "general.teleport");
			costs = Toolbox.arrayCopy(costs, 0, new String[costs.length+1], 1, costs.length);
			costs[0] = target.getCostClass();
			if(!Toolbox.canPay(sender, target.count(), costs)) return true;
			target.teleport(dest);
			if(target.getType() == TargetType.SELF)
				Messaging.send(sender, "&fYou teleported to &9" + dest.getName() + "&f!");
			else
				Messaging.send(sender, "&fYou teleported &9" + target.getName() + "&f to &9" + dest.getName() + "&f!");
		}
		return true;
	}

	public boolean hasDestPermission(Player sender, Target targ, Destination dest) {
		if(dest.hasPermission(sender, "teleport", "general.teleport")) return true;
		return dest.hasPermission(sender, "teleport", targ.getType().getPermission("general.teleport"));
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		Target target;
		Destination dest;
		switch(args.length) {
		case 2: // /tele <what> <destination>
			target = Target.get(args[0], null);
			dest = Destination.get(args[1], null);
		break;
		default:
			return SHOW_USAGE;
		}
		if(dest == null || target == null) return true;
		target.teleport(dest);
		Messaging.send(sender, "&fYou teleported &9" + target.getName() + "&f to &9" + dest.getName() + "&f!");
		return true;
	}
}
