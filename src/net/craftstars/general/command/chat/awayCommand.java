
package net.craftstars.general.command.chat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Toolbox;

public class awayCommand extends CommandBase {
	public awayCommand(General instance) {
		super(instance);
	}
	
	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(!sender.hasPermission("general.away"))
			return Messaging.lacksPermission(sender, "general.away");
		Player who = (Player) sender;
		String reason = args.get("reason").toString();
		if(reason.isEmpty()) {
			if(General.players.isAway(who)) {
				General.players.unAway(who);
				Messaging.send(sender, LanguageText.AWAY_BACK);
			} else {
				Messaging.send(sender, LanguageText.AWAY_HERE);
			}
		} else {
			if(General.players.isAway(who)) {
				Messaging.send(sender, LanguageText.AWAY_CHANGE);
			} else {
				Messaging.send(sender, LanguageText.AWAY_SET);
			}
			General.players.goAway(who, reason);
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
			Messaging.send(sender, LanguageText.AWAY_CONSOLE);
		else Messaging.send(sender, LanguageText.AWAY_UNKNOWN);
		return null;
	}
}
