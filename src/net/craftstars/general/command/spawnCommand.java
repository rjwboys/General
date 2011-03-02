
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
        if(!plugin.permissions.hasPermission(sender, "general.spawn")) {
            Messaging.send(sender, "&rose;You don't have permission to do that.");
            return false;
        }
        Location spawnLocation = sender.getWorld().getSpawnLocation();

        sender.teleportTo(spawnLocation);

        Messaging.send(sender, "You were teleported to the spawn location!");

        return true;
    }

}
