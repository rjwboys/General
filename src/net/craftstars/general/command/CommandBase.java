
package net.craftstars.general.command;


import net.craftstars.general.General;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandBase {
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
}
