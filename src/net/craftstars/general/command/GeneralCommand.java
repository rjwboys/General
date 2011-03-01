package net.craftstars.general.command;

import net.craftstars.general.General;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class GeneralCommand
{    
    public boolean runCommand(General plugin, CommandSender sender, Command command, String commandLabel, String[] args)
    {
        if (sender instanceof Player)
        {
            return this.fromPlayer(plugin, (Player)sender, command, commandLabel, args);
        }
        /*
        else if (sender instanceof Console)
        {
            // This is example code! Not implemented in to Bukkit yet!
            return this.fromConsole(plugin, sender, command, commandLabel, args);
        }
        */
        else
        {
            // We're not sure where this command is coming from, so, we're going to fail.
            return false;
        }
    }
    
    public abstract boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel, String[] args);
    
    // More example code! Not implemented in to Bukkit yet!
    //public abstract boolean fromConsole(General plugin, CommandSender sender, Command command, String commandLabel, String[] args);
}