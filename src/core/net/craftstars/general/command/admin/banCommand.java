package net.craftstars.general.command.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;

public class banCommand extends CommandBase {
	public banCommand(General instance) {
		super(instance);
	}
	
	@Override
	public Map<String,Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		if(args.length < 1) return null;
		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		boolean missing = false;
		for(String name : args) {
			OfflinePlayer player = Toolbox.matchPlayer(name);
			if(player == null) {
				Messaging.invalidPlayer(sender, name);
				missing = true;
				player = Bukkit.getOfflinePlayer(name);
			}
			players.add(player);
		}
		if(missing) Messaging.send(sender, LanguageText.MISC_BAN_ANYWAY);
		return Collections.singletonMap("players", (Object)players);
	}
	
	@Override
	public boolean execute(CommandSender sender, String command, Map<String,Object> args) {
		if(!sender.hasPermission("general.ban"))
			return Messaging.lacksPermission(sender, "general.ban");
		@SuppressWarnings("unchecked")
		List<OfflinePlayer> players = (List<OfflinePlayer>)args.get("players");
		for(OfflinePlayer player : players) {
			player.setBanned(true);
			if(player.isOnline()) {
				Player online = (Player)player;
				online.kickPlayer(Option.BAN_KICK.get());
			}
			Messaging.send(sender, LanguageText.MISC_BANNED.value("player", player.getName()));
		}
		return true;
	}
	
}
