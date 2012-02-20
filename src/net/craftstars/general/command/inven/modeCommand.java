package net.craftstars.general.command.inven;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Toolbox;

public class modeCommand extends CommandBase {
	public modeCommand(General instance) {
		super(instance);
	}
	
	@Override
	public Map<String,Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String,Object> params = new HashMap<String,Object>();
		GameMode mode;
		switch(args.length) {
		case 0: // /mode
			if(!isPlayer) return null;
			setCommand("view");
			params.put("who", sender);
			break;
		case 1: // /mode <mode> OR /mode <player>
			try {
				mode = GameMode.getByValue(Integer.parseInt(args[0]));
			} catch(NumberFormatException e) {
				mode = null;
			}
			if(mode == null) {
				try {
					mode = Toolbox.enumValue(GameMode.class, args[0].toUpperCase());
				} catch(IllegalArgumentException e) {
					Player player = Toolbox.matchPlayer(args[0]);
					if(player == null) {
						Messaging.send(sender, LanguageText.MISC_BAD_MODE);
						Messaging.invalidPlayer(sender, args[0]);
						return null;
					}
					setCommand("view");
					params.put("who", player);
					break;
				}
			}
			if(!isPlayer) return null;
			setCommand("set");
			params.put("who", sender);
			params.put("mode", mode);
			break;
		case 2: // /mode <player> <mode>
			try {
				mode = GameMode.getByValue(Integer.parseInt(args[0]));
				if(mode == null) mode = Toolbox.enumValue(GameMode.class, args[1].toUpperCase());
				Player player = Toolbox.matchPlayer(args[0]);
				if(player == null) {
					Messaging.invalidPlayer(sender, args[0]);
					return null;
				}
				setCommand("set");
				params.put("who", player);
				params.put("mode", mode);
			} catch(RuntimeException e) {
				if(!(e instanceof IllegalArgumentException || e instanceof NumberFormatException)) throw e;
				Messaging.send(sender, LanguageText.MISC_BAD_MODE);
				return null;
			}
			break;
		default:
			return null;
		}
		return params;
	}
	
	@Override
	public boolean execute(CommandSender sender, String command, Map<String,Object> args) {
		Player who = (Player)args.get("who");
		if(command.equals("view")) {
			if(!sender.hasPermission("general.gamemode.view"))
				return Messaging.lacksPermission(sender, "general.gamemode.view");
			String modeName = Toolbox.formatItemName(who.getGameMode().toString());
			Messaging.send(sender, LanguageText.MISC_IN_MODE.value("player", who.getDisplayName(), "mode", modeName));
		} else if(command.equals("set")) {
			if(who.equals(sender)) {
				if(!sender.hasPermission("general.gamemode.set"))
					return Messaging.lacksPermission(sender, "general.gamemode.set");
			} else if(!sender.hasPermission("general.gamemode.set.other"))
				return Messaging.lacksPermission(sender, "general.gamemode.set.other");
			GameMode mode = (GameMode)args.get("mode");
			String modeName = Toolbox.formatItemName(mode.toString());
			who.setGameMode(mode);
			if(who.equals(sender))
				Messaging.send(sender, LanguageText.MISC_SET_OWN_MODE.value("mode", modeName));
			else Messaging.send(sender, LanguageText.MISC_SET_MODE.value("player", who.getDisplayName(), "mode", modeName));
		} else return false;
		return true;
	}
	
}
