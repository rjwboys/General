package net.craftstars.general.command.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Toolbox;

public class deopCommand extends CommandBase {
	
	public deopCommand(General instance) {
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
		if(Toolbox.lacksPermission(sender, "general.deop"))
			return Messaging.lacksPermission(sender, "general.deop");
		@SuppressWarnings("unchecked")
		ArrayList<Player> players = (ArrayList<Player>) args.get("players");
		String[] names = new String[players.size()];
		int i = 0;
		for(Player who : players) {
			who.setOp(false);
			names[i++] = who.getDisplayName();
			if(!who.equals(sender)) Messaging.send(who, LanguageText.MISC_DEOPPED);
		}
		Messaging.send(sender, LanguageText.MISC_DEOPPING.value("ops", Toolbox.join(names, ", ")));
		return true;
	}
	
}
