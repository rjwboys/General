package net.craftstars.general.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;

public class spawnCommand extends GeneralCommand
{

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel, String[] args)
    {
        Location spawnLocation = sender.getWorld().getSpawnLocation();
        
        sender.teleportTo(spawnLocation);
        
        Messaging.send(sender, "You were teleported to the spawn location!");
        
        return true;
    }
    
}