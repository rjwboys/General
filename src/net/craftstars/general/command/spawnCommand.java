
package net.craftstars.general.command;

import java.util.Formatter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class spawnCommand extends CommandBase {
    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "teleport to spawn", "general.spawn")) return true;
        switch(args.length) {
        case 0: // /spawn
            doTeleport(sender);
        break;
        case 1:
            if(args[0].equalsIgnoreCase("help")) return Toolbox.SHOW_USAGE; // /spawn help
            else if(args[0].equalsIgnoreCase("show")) { // /spawn show
                doShow(sender.getWorld(), sender);
            } else if(args[0].equalsIgnoreCase("set")) { // /spawn set
                if(Toolbox.lacksPermission(plugin, sender, "set the spawn location", "general.spawn.set")) return true;
                doSet(sender, sender);
            } else { // /spawn <player>
                if(Toolbox.lacksPermission(plugin, sender, "teleport others to spawn", "general.spawn.other")) return true;
                doTeleport(args[0], sender);
            }
        break;
        case 2:
            if(args[0].equalsIgnoreCase("set")) { // /spawn set <player>
                if(Toolbox.lacksPermission(plugin, sender, "set the spawn location", "general.spawn.set")) return true;
                doSet(sender, args[1]);
            } else if(args[1].equalsIgnoreCase("show")) { // /spawn <world> show
                doShow(args[0], sender);
            } else return Toolbox.SHOW_USAGE;
        break;
        case 4: // /spawn set <x> <y> <z>
            if(!args[0].equalsIgnoreCase("set")) return Toolbox.SHOW_USAGE;
            if(Toolbox.lacksPermission(plugin, sender, "set the spawn location", "general.spawn.set")) return true;
            doSet(sender, sender, args[1], args[2], args[3]);
        break;
        case 5: // /spawn <world> set <x> <y> <z>
            if(!args[1].equalsIgnoreCase("set")) return Toolbox.SHOW_USAGE;
            if(Toolbox.lacksPermission(plugin, sender, "set the spawn location", "general.spawn.set")) return true;
            doSet(args[0], sender, args[2], args[3], args[3]);
        break;
        default:
            return Toolbox.SHOW_USAGE;
        }
        return true;
    }

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        switch(args.length) {
        case 1:
            if(args[0].equalsIgnoreCase("help")) return Toolbox.SHOW_USAGE; // spawn help
            else { // spawn <player>
                doTeleport(args[0], sender);
            }
        break;
        case 2:
            if(args[0].equalsIgnoreCase("set")) { // /spawn set <player>
                doSet(sender, args[1]);
            } else if(args[1].equalsIgnoreCase("show")) { // /spawn <world> show
                doShow(args[0], sender);
            } else return Toolbox.SHOW_USAGE;
        break;
        case 5: // spawn <world> set <x> <y> <z>
            if(!args[1].equalsIgnoreCase("set")) return Toolbox.SHOW_USAGE;
            doSet(args[0], sender, args[2], args[3], args[4]);
        break;
        default:
            return Toolbox.SHOW_USAGE;
        }
        return true;
    }

    private void doTeleport(String who, CommandSender fromWhom) {
        Player player = Toolbox.getPlayer(who, fromWhom);
        if(player != null) doTeleport(player);
    }

    private void doTeleport(Player who) {
        Location spawnLocation = who.getWorld().getSpawnLocation();
        who.teleportTo(spawnLocation);
        Messaging.send(who, "You were teleported to the spawn location!");
    }

    private void doShow(String which, CommandSender toWhom) {
        World theWorld = Toolbox.getWorld(which, toWhom);
        if(theWorld != null) doShow(theWorld, toWhom);
    }

    private void doShow(World which, CommandSender toWhom) {
        Location pos = which.getSpawnLocation();
        Formatter fmt = new Formatter();
        String message = fmt.format("&eCurrent spawn location in world '&f%s&e' is &f(%d,%d,%d)", which.getName(),
                        pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()).toString();
        Messaging.send(toWhom,message);
    }

    private void doSet(CommandSender fromWhom, Player ofWhom, String xCoord, String yCoord,
            String zCoord) {
        World theWorld = ofWhom.getWorld();
        Location pos = Toolbox.getLocation(fromWhom, theWorld, xCoord, yCoord, zCoord);
        if(pos != null) doSet(theWorld, fromWhom, pos, "&eSpawn position changed to &f(%2$d,%3$d,%4$d)",
                "&rose;There was an error setting the spawn location. It has not been changed.");
    }

    private void doSet(String which, CommandSender fromWhom, String xCoord, String yCoord,
            String zCoord) {
        World theWorld = Toolbox.getWorld(which, fromWhom);
        Location pos = Toolbox.getLocation(fromWhom, theWorld, xCoord, yCoord, zCoord);
        if(pos != null && theWorld != null)
            doSet(theWorld, fromWhom, pos, "&eSpawn position in world '&f%s&e' changed to &f(%d,%d,%d)",
                "&rose;There was an error setting the spawn location. It has not been changed.");
    }

    private void doSet(CommandSender fromWhom, Player toWhom) {
        doSet(toWhom.getWorld(), fromWhom, toWhom, "&eSpawn position changed to where you are standing.",
                "&rose;There was an error setting the spawn location. It has not been changed.");
    }

    private void doSet(CommandSender fromWhom, String toWhom) {
        Player who = Toolbox.getPlayer(toWhom, fromWhom);
        if(who == null) return;
        doSet(who.getWorld(), fromWhom, who, "&eSpawn position changed to where " + toWhom + " is standing.",
                "&rose;There was an error setting the spawn location. It has not been changed.");
    }

    private void doSet(World which, CommandSender fromWhom, Player toWhom, String ifSet,
            String ifFail) {
        Location pos = toWhom.getLocation();
        doSet(which, fromWhom, pos, ifSet, ifFail);
    }

    private void doSet(World which, CommandSender fromWhom, Location pos, String ifSet,
            String ifFail) {
        Formatter fmt = new Formatter();
        ifSet = fmt.format(ifSet, which.getName(), pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()).toString();
        if(which.setSpawnLocation(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ())) {
            Messaging.send(fromWhom,ifSet);
        } else {
            Messaging.send(fromWhom,ifFail);
        }
    }
}
