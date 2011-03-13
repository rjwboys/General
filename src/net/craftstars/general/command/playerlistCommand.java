
package net.craftstars.general.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class playerlistCommand extends CommandBase {

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "general.playerlist", "general.basic")) return true;
        String[] players = this.getPlayerList(plugin);
        Messaging.send(sender, "&eOnline Players (" + players.length + "):");
        doListing(plugin, sender, players);
        return true;
    }

    private String[] getPlayerList(General plugin) {
        Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
        String[] players = new String[onlinePlayers.length];

        for(int i = 0; i < onlinePlayers.length; i++) {
            players[i] = onlinePlayers[i].getName();
        }

        return players;
    }
    
    private void doListing(General plugin, CommandSender fromWhom, String[] players) {
        StringBuilder playerList = new StringBuilder();
        for(int i = 0; i < players.length; i++) {
            playerList.append(players[i]);
            if( ( (i + 1) % 4) == 0) {
                Messaging.send(fromWhom,playerList.toString());
                playerList.setLength(0);
            } else if( (i + 1) != players.length) {
                playerList.append(", ");
            }
        }
        Messaging.send(fromWhom,playerList.toString());
    }

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        String[] players = this.getPlayerList(plugin);
        Messaging.send(sender,"&eOnline Players (" + players.length + "):");
        doListing(plugin, sender, players);
        return true;
    }
}
