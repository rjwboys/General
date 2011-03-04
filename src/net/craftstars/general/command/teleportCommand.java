
package net.craftstars.general.command;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Teleport;
import net.craftstars.general.util.Toolbox;

public class teleportCommand extends GeneralCommand {

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(!plugin.permissions.hasPermission(sender, "general.teleport")) {
            Messaging.send(sender, "&rose;You don't have permission to do that.");
            return true;
        }
        if(args.length < 1) {
            return false;
        }

        if(args.length == 1) {
            Player destination = Toolbox.playerMatch(args[0]);

            if(destination == null) {
                Messaging.send(sender, "Couldn't find player.");
            }

            if(destination.getName().equals(sender.getName())) {
                Messaging.send(sender, "You just teleported yourself to yourself!");

                return true;
            }

            Teleport.teleportPlayerToPlayer(sender, destination);

            Messaging.send(sender, "Teleported you to " + destination.getName());

            return true;
        }

        if(args.length == 2) {
            Player destination = Toolbox.playerMatch(args[1]);

            if(destination == null) {
                Messaging.send(sender, "Couldn't find player.");

                return true;
            }

            if(args[0].equalsIgnoreCase("*")) {
                Teleport.teleportAllToPlayer(destination);

                Messaging.send(sender, "Teleported all players!");

                return true;
            }

            if(args[0].contains(",")) {
                Teleport.teleportManyPlayersToPlayer(args[0], destination);

                Messaging.send(sender, "Teleported several players!");

                return true;
            }

            Player who = Toolbox.playerMatch(args[0]);

            if(who == null) {
                Messaging.send(sender, "Couldn't find player.");

                return true;
            }

            if(who.getName().equals(sender.getName())
                    && destination.getName().equals(sender.getName())) {
                Messaging.send(sender, "You just teleported yourself to yourself!");

                return true;
            }

            Teleport.teleportPlayerToPlayer(who, destination);

            Messaging.send(sender, "Teleported player to other player!");
        }

        return true;
    }

}
