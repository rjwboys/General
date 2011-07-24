
package net.craftstars.general.command.chat;

import java.util.HashMap;
import java.util.Map;

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
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.away", "general.basic"))
			return Messaging.lacksPermission(sender, "set your away status");
		Player who = (Player) sender;
		String reason = args.get("reason").toString();
		if(reason.isEmpty()) {
			if(General.plugin.isAway(who)) {
				General.plugin.unAway(who);
				Messaging.send(sender, Messaging.get("away.back", "{silver}You have been marked as back."));
			} else {
				Messaging.send(sender, Messaging.get("away.here", "{silver}You are not away."));
			}
		} else {
			if(General.plugin.isAway(who)) {
				Messaging.send(sender, Messaging.get("away.change", "{silver}Away reason changed."));
			} else {
				Messaging.send(sender, Messaging.get("away.set", "{silver}You are now marked as away."));
			}
			General.plugin.goAway(who, reason);
		}
		return true;
	}
	
	@Override
	protected boolean isHelpCommand(Command command, String commandLabel, String[] args) {
		return false; // No help topic for chat commands
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		if(isPlayer) {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("reason", Toolbox.join(args, " "));
			return params;
		} else if(sender instanceof ConsoleCommandSender)
			Messaging.send(sender, Messaging.get("away.console", "{rose}It's not possible for the console to be marked as away."));
		else Messaging.send(sender, Messaging.get("away.unknown", "{rose}I don't know what you are, so I can't mark you as away."));
		return null;
	}
}
