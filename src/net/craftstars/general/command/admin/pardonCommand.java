package net.craftstars.general.command.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Toolbox;

public class pardonCommand extends CommandBase {
	public pardonCommand(General instance) {
		super(instance);
	}
	
	@Override
	public Map<String,Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		if(args.length < 1) return null;
		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		for(String name : args) players.add(Bukkit.getOfflinePlayer(name));
		return Collections.singletonMap("players", (Object)players);
	}
	
	@Override
	public boolean execute(CommandSender sender, String command, Map<String,Object> args) {
		if(Toolbox.lacksPermission(sender, "general.pardon"))
			return Messaging.lacksPermission(sender, "general.pardon");
		@SuppressWarnings("unchecked")
		List<OfflinePlayer> players = (List<OfflinePlayer>)args.get("players");
		for(OfflinePlayer player : players) {
			player.setBanned(false);
			Messaging.send(sender, LanguageText.MISC_PARDONED.value("player", player.getName()));
		}
		return true;
	}
	
}
