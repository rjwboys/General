
package net.craftstars.general.command.info;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.MessageOfTheDay;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class playerlistCommand extends CommandBase {
	public playerlistCommand(General instance) {
		super(instance);
	}
	
	private void doListing(CommandSender fromWhom, List<String> players) {
		List<String> playerList = MessageOfTheDay.formatPlayerList(players);
		for(String line : playerList)
			Messaging.send(fromWhom, line);
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		if(args.length == 0) params.put("world", null);
		else if(args.length == 1) {
			World world = Toolbox.matchWorld(args[0]);
			if(world == null) {
				Messaging.invalidWorld(sender, args[0]);
				return null;
			}
			params.put("world", world);
		} else return null;
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.playerlist", "general.basic"))
			return Messaging.lacksPermission(sender, "view the player list");
		World world = (World) args.get("world");
		List<String> players = Toolbox.getPlayerList(plugin, world);
		String worldName = world == null ? "" : " in world " + world.getName();
		Messaging.send(sender, "&eOnline Players" + worldName + " (" + players.size() + "):");
		doListing(sender, players);
		return true;
	}
}
