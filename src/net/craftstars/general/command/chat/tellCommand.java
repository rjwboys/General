
package net.craftstars.general.command.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class tellCommand extends CommandBase {
	public tellCommand(General instance) {
		super(instance);
	}

	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.tell", "general.basic"))
			return Messaging.lacksPermission(sender, "send private messages to players");
		if(args.length < 2) return SHOW_USAGE;
		Player who;
		if(args[0].equals("@")) {
			String name = plugin.lastMessaged(sender.getName());
			who = name == null ? null : Bukkit.getServer().getPlayer(name);
		} else who = Toolbox.matchPlayer(args[0]);
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
			plugin.hasMessaged(who.getName(), sender.getName());
		} else if(args[0].equals("@"))
			Messaging.send(sender, "&cNo-one has messaged you yet.");
		else Messaging.invalidPlayer(sender, args[0]);
		return true;
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		if(args.length < 2) return SHOW_USAGE;
		Player who = Toolbox.matchPlayer(args[0]);
		if(who != null) {
			Messaging.send(sender, "&gray;(whisper)   to <" + who.getName() + "> " + Toolbox.combineSplit(args, 1));
			Messaging.send(who, "(whisper) from [CONSOLE] " + Toolbox.combineSplit(args, 1));
			if(General.plugin.isAway(who)) {
				Messaging.send(sender, "&7" + who.getDisplayName() + " is currently away.");
				Messaging.send(sender, "&7Reason: " + General.plugin.whyAway(who));
			}
		} else Messaging.invalidPlayer(sender, args[0]);
		return true;
	}
}
