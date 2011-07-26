
package net.craftstars.general.command.chat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.LanguageText;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class tellCommand extends CommandBase {
	public tellCommand(General instance) {
		super(instance);
	}
	
	@Override
	protected boolean isHelpCommand(Command command, String commandLabel, String[] args) {
		return false; // No help topic for chat commands
	}
	
	private void sendMessage(CommandSender from, Player to, String message) {
		LanguageText fromFmt, toFmt = LanguageText.WHISPER_TO;
		if(from instanceof Player) fromFmt = LanguageText.WHISPER_FROM_PLAYER;
		else fromFmt = LanguageText.WHISPER_FROM_UNKNOWN;
		Messaging.send(from, toFmt.value("name", to.getName(), "message", message));
		Messaging.send(to, fromFmt.value("name", getName(from), "message", message));
		if(General.plugin.isAway(to)) {
			Messaging.send(from, LanguageText.AWAY_IS.value("name", to.getDisplayName()));
			Messaging.send(from, LanguageText.AWAY_REASON.value("reason", General.plugin.whyAway(to)));
		}
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		if(args.length < 2) return null;
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("message", Toolbox.join(args, 1));
		Player recipient;
		if(isPlayer && args[0].equals("@")){
			String name = plugin.lastMessaged(((Player) sender).getName());
			if(name == null) {
				Messaging.send(sender, LanguageText.NO_REPLY);
				return null;
			}
			recipient = Bukkit.getServer().getPlayer(name);
		} else recipient = Toolbox.matchPlayer(args[0]);
		if(recipient == null) {
			Messaging.invalidPlayer(sender, args[0]);
			return null;
		}
		params.put("recipient", recipient);
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.tell", "general.basic"))
			return Messaging.lacksPermission(sender, "general.tell");
		Player who = (Player) args.get("recipient");
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(who.getName().equals(player.getName())) {
				Messaging.send(sender, LanguageText.WHISPER_SELF);
				return true;
			}
			plugin.hasMessaged(who.getName(), player.getName());
		}
		sendMessage(sender, who, args.get("message").toString());
		return true;
	}
}
