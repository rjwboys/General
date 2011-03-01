package net.craftstars.general.util;

import org.bukkit.entity.Player;

import net.craftstars.general.General;

public class Teleport
{
    private static General plugin = General.plugin;
    
    public static boolean teleportPlayerToPlayer(Player who, Player destination)
    {
        who.teleportTo(destination.getLocation());
        
        return true;
    }
    
    public static boolean teleportAllToPlayer(Player destination)
    {
        Player[] players = Teleport.plugin.getServer().getOnlinePlayers();
        
        for (Player player : players)
        {
            if (!player.equals(destination))
            {
                player.teleportTo(destination.getLocation());
            }
        }
        
        return true;
    }
}