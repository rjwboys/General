
package net.craftstars.general.command.info;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.MessageOfTheDay;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class playerlistCommand extends CommandBase {
	public playerlistCommand(General instance) {
		super(instance);
	}

	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.playerlist", "general.basic"))
			return Messaging.lacksPermission(sender, "view the player list");
		List<String> players = Toolbox.getPlayerList(plugin);
		Messaging.send(sender, "&eOnline Players (" + players.size() + "):");
		doListing(sender, players);
		return true;
	}
	
	private void doListing(CommandSender fromWhom, List<String> players) {
		List<String> playerList = MessageOfTheDay.formatPlayerList(players);
		for(String line : playerList)
			Messaging.send(fromWhom, line);
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		List<String> players = Toolbox.getPlayerList(plugin);
		Messaging.send(sender, "&eOnline Players (" + players.size() + "):");
		doListing(sender, players);
		return true;
	}
}
