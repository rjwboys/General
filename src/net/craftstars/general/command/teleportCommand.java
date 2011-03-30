
package net.craftstars.general.command;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Teleport;
import net.craftstars.general.util.Toolbox;

public class teleportCommand extends CommandBase {

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "teleport", "general.teleport")) return true;
        switch(args.length) {
        case 1: {// /tp <player|world>
            Player destination = Toolbox.getPlayer(args[0], sender);
            if(destination == null) {
                World where = Toolbox.getWorld(args[0], sender);
                if(where == null) return true;
                sender.teleport(where.getSpawnLocation());
                return true;
            }
            if(destination.getName().equals(sender.getName())) {
                Messaging.send(sender, "&fCongrats! You just teleported yourself to yourself!");
                return true;
            }

            Teleport.teleportPlayerToPlayer(sender, destination);
            Messaging.send(sender, "&fTeleported you to &9" + destination.getName() + "&f!");
        }
        break;
        case 2: { // /tp <player> <to-player|world>
            Player destination = Toolbox.getPlayer(args[1], sender);
            if(destination == null) {
                World where = Toolbox.getWorld(args[1], sender);
                if(where == null) return true;
                sender.teleport(where.getSpawnLocation());
                return true;
            }
            if(args[0].equalsIgnoreCase("*")) {
                if(Toolbox.lacksPermission(plugin, sender, "teleport many players at once", "general.teleport.other.mass")) return true;
                Teleport.teleportAllToPlayer(destination);
                Messaging.send(sender, "&fTeleported all players to &9" + destination.getName()
                        + "&f!");
                return true;
            }

            if(args[0].contains(",")) {
                if(Toolbox.lacksPermission(plugin, sender, "teleport many players at once", "general.teleport.other.mass")) return true;
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
                if(Toolbox.lacksPermission(plugin, sender, "teleport players other than yourself", "general.teleport.other")) return true;
                Messaging.send(who, "&fYou have been teleported to &9" + destination.getName()
                        + "&f!");
            }

            Teleport.teleportPlayerToPlayer(who, destination);
            Messaging.send(sender, "&fTeleported &9" + who.getName() + "&f to &9"
                    + destination.getName() + "&f!");
        }
        break;
        case 3: { // /tp <x> <y> <z>
            if(Toolbox.lacksPermission(plugin, sender, "teleport to coordinates", "general.teleport.coords")) return true;
            Location destination =
                    Toolbox.getLocation(sender, sender.getWorld(), args[0], args[1], args[2]);
            if(destination == null) return true;
            sender.teleport(destination);
            Messaging.send(sender, "&fTeleported you to &9(" + args[0] + "," + args[1] + ","
                    + args[2] + ")&f!");
        }
        break;
        case 4: { // /tp <player> <x> <y> <z> OR /tp <x> <y> <z> <world>
            if(Toolbox.lacksPermission(plugin, sender, "teleport to coordinates", "general.teleport.coords")) return true;
            Player who = Toolbox.getPlayer(args[0], sender);
            Location destination;
            if(who == null) {
                World where = Toolbox.getWorld(args[3], sender);
                if(where == null) return true;
                destination = Toolbox.getLocation(sender, where, args[0], args[1], args[2]);
            } else {
                destination = Toolbox.getLocation(sender, who.getWorld(), args[1], args[2], args[3]);
            }
            if(destination == null) return true;
            if(!who.getName().equals(sender.getName())) {
                if(Toolbox.lacksPermission(plugin, sender, "teleport players other than yourself", "general.teleport.other")) return true;
                Messaging.send(who, "&fYou have been teleported to &9(" + args[0] + "," + args[1]
                        + "," + args[2] + ")&f!");
            }
            who.teleport(destination);
            Messaging.send(sender, "&fTeleported &9" + who.getName() + "&f to &9(" + args[0] + ","
                    + args[1] + "," + args[2] + ")&f!");
        }
        break;
        case 5: { // /tp <player> <x> <y> <z> <world>
            if(Toolbox.lacksPermission(plugin, sender, "teleport to coordinates", "general.teleport.coords")) return true;
            Player who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;
            World where = Toolbox.getWorld(args[3], sender);
            if(where == null) return true;
            Location destination = Toolbox.getLocation(sender, where, args[0], args[1], args[2]);
            if(destination == null) return true;
            if(!who.getName().equals(sender.getName())) {
                if(Toolbox.lacksPermission(plugin, sender, "teleport players other than yourself", "general.teleport.other")) return true;
                Messaging.send(who, "&fYou have been teleported to &9(" + args[0] + "," + args[1]
                        + "," + args[2] + ")&f!");
            }
            who.teleport(destination);
            Messaging.send(sender, "&fTeleported &9" + who.getName() + "&f to &9(" + args[0] + ","
                    + args[1] + "," + args[2] + ")&f!");
        }
        default:
            return Toolbox.SHOW_USAGE;
        }
        return true;
    }

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        switch(args.length) {
        case 2: { // tp <player> <to-player|world>
            Player destination = Toolbox.getPlayer(args[1], sender);
            World where = null;
            if(destination == null) {
                where = Toolbox.getWorld(args[1], sender);
                if(where == null) return true;
            } else {
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
            }

            Player who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;
            Messaging.send(who, "&fYou have been teleported to &9" + destination.getName()
                        + "&f!");
            if(destination != null)
                Teleport.teleportPlayerToPlayer(who, destination);
            else who.teleport(where.getSpawnLocation());
            Messaging.send(sender, "&fTeleported &9" + who.getName() + "&f to &9"
                    + destination.getName() + "&f!");
        }
        break;
        case 4: { // tp <player> <x> <y> <z>
            Player who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;
            Location destination =
                    Toolbox.getLocation(sender, who.getWorld(), args[1], args[2], args[3]);
            if(destination == null) return true;
            Messaging.send(who, "&fYou have been teleported to &9(" + args[1] + "," + args[2]
                        + "," + args[3] + ")&f!");
            who.teleport(destination);
            Messaging.send(sender, "&fTeleported &9" + who.getName() + "&f to &9(" + args[1] + ","
                    + args[2] + "," + args[3] + ")&f!");
        }
        break;
        case 5: { // /tp <player> <x> <y> <z> <world>
            Player who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;
            World where = Toolbox.getWorld(args[3], sender);
            if(where == null) return true;
            Location destination = Toolbox.getLocation(sender, where, args[0], args[1], args[2]);
            if(destination == null) return true;
            Messaging.send(who, "&fYou have been teleported to &9(" + args[0] + "," + args[1]
                    + "," + args[2] + ")&f!");
            who.teleport(destination);
            Messaging.send(sender, "&fTeleported &9" + who.getName() + "&f to &9(" + args[0] + ","
                    + args[1] + "," + args[2] + ")&f!");
        }
        default:
            return Toolbox.SHOW_USAGE;
        }
        return true;
    }

}
