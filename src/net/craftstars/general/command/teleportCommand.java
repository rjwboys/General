
package net.craftstars.general.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Teleport;
import net.craftstars.general.util.Toolbox;

public class teleportCommand extends GeneralCommand {

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "general.teleport")) return true;
        switch(args.length) {
        case 1: {// /tp <player>
            Player destination = Toolbox.getPlayer(args[0], sender);
            if(destination == null) return true;
            if(destination.getName().equals(sender.getName())) {
                Messaging.send(sender, "&fCongrats! You just teleported yourself to yourself!");
                return true;
            }

            Teleport.teleportPlayerToPlayer(sender, destination);
            Messaging.send(sender, "&fTeleported you to &9" + destination.getName() + "&f!");
        }
        break;
        case 2: { // /tp <player> <to-player>
            Player destination = Toolbox.getPlayer(args[1], sender);
            if(destination == null) return true;

            if(args[0].equalsIgnoreCase("*")) {
                if(Toolbox.lacksPermission(plugin, sender, "general.teleport.other.mass")) return true;
                Teleport.teleportAllToPlayer(destination);
                Messaging.send(sender, "&fTeleported all players to &9" + destination.getName()
                        + "&f!");
                return true;
            }

            if(args[0].contains(",")) {
                if(Toolbox.lacksPermission(plugin, sender, "general.teleport.other.mass")) return true;
                Teleport.teleportManyToPlayer(args[0], destination);
                Messaging.send(sender, "&fTeleported several players to &9" + destination.getName()
                        + "&f!");
                return true;
            }

            Player who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;

            if(who.getName().equals(sender.getName())
                    && destination.getName().equals(sender.getName())) {
                Messaging.send(sender, "&fCongrats! You just teleported yourself to yourself!");
                return true;
            } else {
                if(Toolbox.lacksPermission(plugin, sender, "general.teleport.other")) return true;
                Messaging.send(who, "&fYou have been teleported to &9" + destination.getName()
                        + "&f!");
            }

            Teleport.teleportPlayerToPlayer(who, destination);
            Messaging.send(sender, "&fTeleported &9" + who.getName() + "&f to &9"
                    + destination.getName() + "&f!");
        }
        break;
        case 3: { // /tp <x> <y> <z>
            if(Toolbox.lacksPermission(plugin, sender, "general.teleport.coords")) return true;
            Location destination =
                    Toolbox.getLocation(sender, sender.getWorld(), args[0], args[1], args[2]);
            if(destination == null) return true;
            sender.teleportTo(destination);
            Messaging.send(sender, "&fTeleported you to &9(" + args[0] + "," + args[1] + ","
                    + args[2] + ")&f!");
        }
        break;
        case 4: { // /tp <player> <x> <y> <z>
            if(Toolbox.lacksPermission(plugin, sender, "general.teleport.coords")) return true;
            Player who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;
            Location destination =
                    Toolbox.getLocation(sender, who.getWorld(), args[0], args[1], args[2]);
            if(destination == null) return true;
            if(!who.getName().equals(sender.getName())) {
                if(Toolbox.lacksPermission(plugin, sender, "general.teleport.other")) return true;
                Messaging.send(who, "&fYou have been teleported to &9(" + args[0] + "," + args[1]
                        + "," + args[2] + ")&f!");
            }
            who.teleportTo(destination);
            Messaging.send(sender, "&fTeleported &9" + who.getName() + "&f to &9(" + args[0] + ","
                    + args[1] + "," + args[2] + ")&f!");
        }
        break;
        default:
            return false;
        }
        return true;
    }

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        switch(args.length) {
        case 2: { // tp <player> <to-player>
            Player destination = Toolbox.getPlayer(args[1], sender);
            if(destination == null) return true;

            if(args[0].equalsIgnoreCase("*")) {
                Teleport.teleportAllToPlayer(destination);
                Messaging.send(sender, "&fTeleported all players to &9" + destination.getName()
                        + "&f!");
                return true;
            }

            if(args[0].contains(",")) {
                Teleport.teleportManyToPlayer(args[0], destination);
                Messaging.send(sender, "&fTeleported several players to &9" + destination.getName()
                        + "&f!");
                return true;
            }

            Player who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;
            Messaging.send(who, "&fYou have been teleported to &9" + destination.getName()
                        + "&f!");
            Teleport.teleportPlayerToPlayer(who, destination);
            Messaging.send(sender, "&fTeleported &9" + who.getName() + "&f to &9"
                    + destination.getName() + "&f!");
        }
        break;
        case 4: { // tp <player> <x> <y> <z>
            Player who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;
            Location destination =
                    Toolbox.getLocation(sender, who.getWorld(), args[0], args[1], args[2]);
            if(destination == null) return true;
            Messaging.send(who, "&fYou have been teleported to &9(" + args[0] + "," + args[1]
                        + "," + args[2] + ")&f!");
            who.teleportTo(destination);
            Messaging.send(sender, "&fTeleported &9" + who.getName() + "&f to &9(" + args[0] + ","
                    + args[1] + "," + args[2] + ")&f!");
        }
        break;
        default:
            return false;
        }
        return true;
    }

}
