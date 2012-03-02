package net.craftstars.general.command.inven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
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
			params.put("who", Collections.singletonList(sender));
			params.put("world", false);
			params.put("mode", mode);
			break;
		case 2: // /mode <player> <mode>
			try {
				try {
					mode = GameMode.getByValue(Integer.parseInt(args[1]));
				} catch(NumberFormatException e) {
					mode = null;
				}
				if(mode == null) mode = GameMode.valueOf(args[1].toUpperCase());
				params.put("world", false);
				Player player = Toolbox.matchPlayer(args[0]);
				if(player == null) {
					final World world = Toolbox.matchWorld(args[0]);
					if(world == null) {
						Messaging.invalidPlayer(sender, args[0]);
						return null;
					}
					List<Player> online = new ArrayList<Player>(Arrays.asList(Bukkit.getOnlinePlayers()));
					params.put("who", Collections2.filter(online, new Predicate<Player>(){
						@Override public boolean apply(Player who) {
							return who.getWorld().equals(world);
						}
					}));
					params.put("world", true);
				} else params.put("who", Collections.singletonList(player));
				setCommand("set");
				params.put("mode", mode);
			} catch(IllegalArgumentException e) {
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
		if(command.equals("view")) {
			Player who = (Player)args.get("who");
			if(!sender.hasPermission("general.gamemode.view"))
				return Messaging.lacksPermission(sender, "general.gamemode.view");
			String modeName = Toolbox.formatItemName(who.getGameMode().toString());
			Messaging.send(sender, LanguageText.MISC_IN_MODE.value("player", who.getDisplayName(), "mode", modeName));
		} else if(command.equals("set")) {
			@SuppressWarnings("unchecked")
			Collection<Player> players = (Collection<Player>)args.get("who");
			boolean world = (Boolean)args.get("world");
			for(Player who : players) {
				if(world) {
					if(!sender.hasPermission("general.gamemode.set.world"))
						return Messaging.lacksPermission(sender, "general.gamemode.set.world");
				} else if(who.equals(sender)) {
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
			}
		} else return false;
		return true;
	}
	
}
