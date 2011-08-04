package net.craftstars.general.command.admin;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.util.LanguageText;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class kickCommand extends CommandBase {
	
	public kickCommand(General instance) {
		super(instance);
	}
	
	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		if(args.length < 1) return null;
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("reason", args.length == 1 ? "" : Toolbox.join(args, 1));
		Player villain = Toolbox.matchPlayer(args[0]);
		if(villain == null) {
			Messaging.invalidPlayer(sender, args[0]);
			return null;
		}
		params.put("villain", villain);
		return params;
	}
	
	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.kick"))
			return Messaging.lacksPermission(sender, "general.kick");
		Player villain = (Player) args.get("villain");
		String reason = args.get("reason").toString();
		reason = reason.isEmpty() ? LanguageText.MISC_KICKED.value() : reason;
		Messaging.send(sender, LanguageText.MISC_KICKING.value("player", villain.getName(), "reason", reason));
		villain.kickPlayer(reason);
		return true;
	}
	
}
