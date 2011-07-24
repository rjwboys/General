
package net.craftstars.general.command.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class awayCommand extends CommandBase {
	public awayCommand(General instance) {
		super(instance);
	}

	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		Messaging.send(sender, "It's not possible for the console to be marked as away.");
		return true;
	}

	@Override
	public boolean fromUnknown(CommandSender sender, Command command, String commandLabel,
			String[] args) {
		Messaging.send(sender, "I don't know what you are, so I can't mark you as away.");
		return true;
	}
	
	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.away", "general.basic"))
			return Messaging.lacksPermission(sender, "set your away status");
		if(args.length == 0) {
			if(General.plugin.isAway(sender)) {
				General.plugin.unAway(sender);
				Messaging.send(sender, "&7You have been marked as back.");
			} else {
				Messaging.send(sender, "&7You are not away.");
			}
		} else {
			if(General.plugin.isAway(sender)) {
				Messaging.send(sender, "&7Away reason changed.");
			} else {
				Messaging.send(sender, "&7You are now marked as away.");
			}
			String reason = Toolbox.combineSplit(args, 0);
			General.plugin.goAway(sender, reason);
		}
		return true;
	}
	
	@Override
	protected boolean isHelpCommand(Command command, String commandLabel, String[] args) {
		return false; // No help topic for chat commands
	}
}
