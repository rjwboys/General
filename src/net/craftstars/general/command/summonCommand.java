
package net.craftstars.general.command;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Teleport;
import net.craftstars.general.util.Toolbox;

public class summonCommand extends GeneralCommand {

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(!plugin.permissions.hasPermission(sender, "general.summon")) {
            Messaging.send(sender, "&rose;You don't have permission to do that.");
            return false;
        }
        if(args.length < 1) {
            return false;
        }

        Player player = Toolbox.playerMatch(args[0]);

        if(player == null) {
            Messaging.send(sender, "Couldn't find player.");

            return true;
        }

        if(player.equals(sender)) {
            Messaging.send(sender, "You cannot teleport yourself.");

            return true;
        }

        Teleport.teleportPlayerToPlayer(player, sender);

        Messaging.send(sender, "Teleported " + player.getName() + " to you!");

        return true;
    }

}
