package net.craftstars.general.command.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.option.Options;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Toolbox;

public class banCommand extends CommandBase {
	public banCommand(General instance) {
		super(instance);
	}
	
	@Override
	public Map<String,Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		if(args.length < 1) return null;
		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		List<String> ips = new ArrayList<String>();
		boolean missing = false;
		for(String name : args) {
			if(Toolbox.isIP(name)) {
				ips.add(name);
				continue;
			}
			OfflinePlayer player = Toolbox.matchPlayer(name);
			if(player == null) {
				Messaging.invalidPlayer(sender, name);
				missing = true;
				player = Bukkit.getOfflinePlayer(name);
			}
			players.add(player);
		}
		if(missing) Messaging.send(sender, LanguageText.MISC_BAN_ANYWAY);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("ips", ips);
		params.put("players", players);
		return params;
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
				online.kickPlayer(Options.BAN_KICK.get());
			}
			Messaging.send(sender, LanguageText.MISC_BANNED.value("player", player.getName()));
		}
		@SuppressWarnings("unchecked")
		List<String> ips = (List<String>)args.get("ips");
		for(String ip : ips) {
			Bukkit.banIP(ip);
			Messaging.send(sender, LanguageText.MISC_BANNED.value("player", ip));
		}
		return true;
	}
	
}
