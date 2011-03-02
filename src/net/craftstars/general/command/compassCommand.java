package net.craftstars.general.command;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;

public class compassCommand extends GeneralCommand
{
    
    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel, String[] args)
    {
        if(!plugin.permissions.hasPermission(sender, "general.compass")) {
            Messaging.send(sender, "&rose;You don't have permission to do that.");
            return false;
        }
        double degreeRotation = ((sender.getLocation().getYaw() - 90) % 360);
        
        if (degreeRotation < 0)
        {
            degreeRotation += 360.0;
        }
        
        Messaging.send(sender, "&cCompass: "+this.getDirection(degreeRotation));
        
        return true;
    }
    
    private String getDirection(double degrees)
    {
        if (0 <= degrees && degrees < 22.5) return "N";
        else if (22.5 <= degrees && degrees < 67.5) return "NE";
        else if (67.5 <= degrees && degrees < 112.5) return "E";
        else if (112.5 <= degrees && degrees < 157.5) return "SE";
        else if (157.5 <= degrees && degrees < 202.5) return "S";
        else if (202.5 <= degrees && degrees < 247.5) return "SW";
        else if (247.5 <= degrees && degrees < 292.5) return "W";
        else if (292.5 <= degrees && degrees < 337.5) return "NW";
        else if (337.5 <= degrees && degrees < 360.0) return "N";
        else return "ERR";
    }
}