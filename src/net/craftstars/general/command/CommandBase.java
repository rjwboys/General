
package net.craftstars.general.command;


import net.craftstars.general.General;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandBase {
    public static final boolean SHOW_USAGE = false; // Change to true to not spew out usage notes on incorrect syntax

    public boolean runCommand(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(sender instanceof Player) {
            return this.fromPlayer(plugin, (Player) sender, command, commandLabel, args);
            // } else if (sender instanceof Console) {
        } else {
            // We're going to assume this command is coming from the console.
            return this.fromConsole(plugin, sender, command, commandLabel, args);
        }
    }

    public abstract boolean fromPlayer(General plugin, Player sender, Command command,
            String commandLabel, String[] args);

    public abstract boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args);
    
    protected String[] prependArg(String[] args, String first) {
        String[] newArgs = new String[args.length+1];
        newArgs[0] = first;
        for(int i = 0; i < args.length; i++)
            newArgs[i+1] = args[i];
        return newArgs;
    }
    
    protected String[] appendArg(String[] args, String last) {
        String[] newArgs = new String[args.length+1];
        newArgs[args.length] = last;
        for(int i = 0; i < args.length; i++)
            newArgs[i] = args[i];
        return newArgs;
    }
}
