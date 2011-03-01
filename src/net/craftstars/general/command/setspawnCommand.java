package net.craftstars.general.command;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class setspawnCommand extends GeneralCommand
{
    
    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel, String[] args)
    {
        
        // TODO: This is gunna be rough. [Plutonium239]
        Messaging.send(sender, "This command isn't implemented yet!");
        
        return true;
    }
    
}