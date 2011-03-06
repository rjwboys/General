
package net.craftstars.general.command;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class clearCommand extends CommandBase {
    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("help")) return Toolbox.USAGE;
            Player who = Toolbox.getPlayer(args[0], sender);
            doClean(who, sender);
        } else return Toolbox.USAGE;
        return true;
    }

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "general.clear")) return true;
        if(args.length == 0) {
            doClean(sender, sender);
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("help")) return Toolbox.USAGE;
            if(Toolbox.lacksPermission(plugin, sender, "general.clear.other")) return true;
            Player who = Toolbox.getPlayer(args[0], sender);
            doClean(who, sender);
        } else return Toolbox.USAGE;
        return true;
    }

    private void doClean(Player who, CommandSender fromWhom) {
        boolean selfClear = false;
        if(fromWhom instanceof Player) {
            if( ((Player) fromWhom).getName().equalsIgnoreCase(who.getName())) selfClear = true;
        }
        who.getInventory().clear();
        if(selfClear) {
            Messaging.send(who, "&2You have cleared your inventory.");
        } else {
            Messaging.send(who, "&2Your inventory has been cleared.");
            Messaging.send(fromWhom, "&f" + who.getName() + "&2's inventory has been cleared.");
        }
    }
}
