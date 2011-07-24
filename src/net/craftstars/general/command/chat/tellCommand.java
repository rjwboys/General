
package net.craftstars.general.command.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
			sendMessage(sender, who, Toolbox.combineSplit(args, 1));
			plugin.hasMessaged(who.getName(), sender.getName());
		} else if(args[0].equals("@"))
			Messaging.send(sender, "&cNo-one has messaged you yet.");
		else Messaging.invalidPlayer(sender, args[0]);
		return true;
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel, String[] args) {
		if(args.length < 2) return SHOW_USAGE;
		Player who = Toolbox.matchPlayer(args[0]);
		if(who != null) {
			sendMessage(sender, who, Toolbox.combineSplit(args, 1));
		} else Messaging.invalidPlayer(sender, args[0]);
		return true;
	}
	
	@Override
	public boolean fromUnknown(CommandSender sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.hasPermission(sender, "general.tell") || sender.isOp()) {
			if(args.length < 2) return SHOW_USAGE;
			Player who = Toolbox.matchPlayer(args[0]);
			if(who != null) {
				sendMessage(sender, who, Toolbox.combineSplit(args, 1));
			} else Messaging.invalidPlayer(sender, args[0]);
		}
		return true;
	}
	
	@Override
	protected boolean isHelpCommand(Command command, String commandLabel, String[] args) {
		return false; // No help topic for chat commands
	}
	
	private void sendMessage(CommandSender from, Player to, String message) {
		String toFmt = plugin.config.getString("messaging.whisper-to", "{gray}(whisper)   to <{name}> {message}");
		String fromFmt;
		if(from instanceof Player)
			fromFmt = plugin.config.getString("messaging.whisper-from-player", "(whisper) from <{name}> {message}");
		else fromFmt = plugin.config.getString("messaging.whisper-from-unknown", "(whisper) from [{name}] {message}");
		// TODO: Actually use these formats (even if I have to write my own formatter!)
		Messaging.send(from, "&gray;(whisper)   to <" + to.getName() + "> " + message);
		Messaging.send(to, "(whisper) from [" + getName(from) + "] " + message);
		if(General.plugin.isAway(to)) {
			Messaging.send(from, "&7" + to.getDisplayName() + " is currently away.");
			Messaging.send(from, "&7Reason: " + General.plugin.whyAway(to));
		}
	}
}
