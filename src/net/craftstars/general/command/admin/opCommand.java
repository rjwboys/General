package net.craftstars.general.command.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.util.LanguageText;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class opCommand extends CommandBase {
	
	public opCommand(General instance) {
		super(instance);
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		ArrayList<Player> players = new ArrayList<Player>();
		if(args.length > 0) {
			for(String name : args) {
				Player who = Toolbox.matchPlayer(name);
				if(who == null) {
					Messaging.invalidPlayer(sender, name);
					continue;
				}
				players.add(who);
			}
		} else if(isPlayer) players.add((Player) sender);
		else return null;
		return Collections.singletonMap("players", (Object) players);
	}
	
	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.op"))
			return Messaging.lacksPermission(sender, "general.op");
		@SuppressWarnings("unchecked")
		ArrayList<Player> players = (ArrayList<Player>) args.get("players");
		String[] names = new String[players.size()];
		int i = 0;
		for(Player who : players) {
			who.setOp(true);
			names[i++] = who.getDisplayName();
			if(!who.equals(sender)) Messaging.send(who, LanguageText.MISC_OPPED);
		}
		Messaging.send(sender, LanguageText.MISC_OPPING.value("ops", Toolbox.join(names, ", ")));
		return true;
	}
	
}
