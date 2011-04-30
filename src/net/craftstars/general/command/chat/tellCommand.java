
package net.craftstars.general.command.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class tellCommand extends CommandBase {
	
	@Override
	public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(plugin, sender, "send private messages to players", "general.tell",
				"general.basic")) return true;
		if(args.length < 2) return SHOW_USAGE;
		Player who = Toolbox.getPlayer(args[0], sender);
		if(who != null) {
			if(who.getName().equals(sender.getName())) {
				Messaging.send(sender, "&c;You can't message yourself!");
				return true;
			}
			Messaging.send(sender, "&gray;(whisper)   to <" + who.getName() + "> " + Toolbox.combineSplit(args, 1));
			Messaging.send(who, "&gray;(whisper) from <" + sender.getName() + "> " + Toolbox.combineSplit(args, 1));
			if(General.plugin.isAway(who)) {
				Messaging.send(sender, "&7" + who.getDisplayName() + " is currently away.");
				Messaging.send(sender, "&7Reason: " + General.plugin.whyAway(who));
			}
		}
		return true;
	}
	
	@Override
	public boolean fromConsole(General plugin, CommandSender sender, Command command, String commandLabel,
			String[] args) {
		if(args.length < 2) return SHOW_USAGE;
		Player who = Toolbox.getPlayer(args[0], sender);
		if(who != null) {
			Messaging.send(sender, "&gray;(whisper)   to <" + who.getName() + "> " + Toolbox.combineSplit(args, 1));
			Messaging.send(who, "(whisper) from [CONSOLE] " + Toolbox.combineSplit(args, 1));
			if(General.plugin.isAway(who)) {
				Messaging.send(sender, "&7" + who.getDisplayName() + " is currently away.");
				Messaging.send(sender, "&7Reason: " + General.plugin.whyAway(who));
			}
		} else {
			Messaging.send(sender, "Couldn't find player " + args[0]);
		}
		return true;
	}
}
