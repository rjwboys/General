
package net.craftstars.general.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.MessageOfTheDay;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class playerlistCommand extends CommandBase {

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "view the player list", "general.playerlist", "general.basic")) return true;
        List<String> players = Toolbox.getPlayerList(plugin);
        Messaging.send(sender, "&eOnline Players (" + players.size() + "):");
        doListing(plugin, sender, players);
        return true;
    }
    
    private void doListing(General plugin, CommandSender fromWhom, List<String> players) {
        List<String> playerList = MessageOfTheDay.formatPlayerList(players);
        for(String line : playerList)
            Messaging.send(fromWhom,line);
    }

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        List<String> players = Toolbox.getPlayerList(plugin);
        Messaging.send(sender,"&eOnline Players (" + players.size() + "):");
        doListing(plugin, sender, players);
        return true;
    }
}
