
package net.craftstars.general.command.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class awayCommand extends CommandBase {
	@Override
	public boolean fromConsole(General plugin, CommandSender sender, Command command, String commandLabel,
			String[] args) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(plugin, sender, "set your away status", "general.away", "general.basic"))
			return true;
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
}
