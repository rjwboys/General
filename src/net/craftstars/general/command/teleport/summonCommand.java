
package net.craftstars.general.command.teleport;

import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.teleport.Destination;
import net.craftstars.general.teleport.Target;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class summonCommand extends CommandBase {
	protected summonCommand(General instance) {
		super(instance);
	}

	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(plugin, sender, "summon players", "general.teleport.other")) return true;
		if(args.length != 1) return SHOW_USAGE;
		
		Target target = Target.get(args[0], sender);
		Destination dest = Destination.locOf(sender);
		if(dest == null || target == null) return true;
		
		if(target.hasPermission(sender) && dest.hasPermission(sender, "teleport", "general.teleport")) {
			target.teleport(dest);
			Messaging.send(sender, "&fTeleported &9" + target.getName() + "&f to you!");
		}
		return true;
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		Messaging.send(sender, "This command is not useable from the console.");
		return true;
	}
	
}
