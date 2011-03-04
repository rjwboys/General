
package net.craftstars.general.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;

public class spawnCommand extends GeneralCommand {

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(args.length > 0 && args[0].equalsIgnoreCase("set")) {
            if(!plugin.permissions.hasPermission(sender, "general.spawn.set")) {
                Messaging.send(sender, "&rose;You don't have permission to do that.");
                return true;
            }
            if(sender.getWorld().setSpawnLocation(sender.getLocation().getBlockX(),
                    sender.getLocation().getBlockY(), sender.getLocation().getBlockZ()))
                Messaging.send("&eSpawn position changed to where you are standing.");
            else
                Messaging.send("&rose;There was an error setting the spawn location. It has not been changed.");
            return true;
        }
        if(!plugin.permissions.hasPermission(sender, "general.spawn")) {
            Messaging.send(sender, "&rose;You don't have permission to do that.");
            return true;
        }
        Location spawnLocation = sender.getWorld().getSpawnLocation();
        sender.teleportTo(spawnLocation);
        Messaging.send(sender, "You were teleported to the spawn location!");
        return true;
    }
}
