
package net.craftstars.general.command.teleport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.teleport.Destination;
import net.craftstars.general.teleport.Target;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class goCommand extends CommandBase {

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "teleport", "general.teleport")) return true;
        Target target;
        Destination dest;
        switch(args.length) {
        case 1: // /tele <destination>
            target = Target.fromPlayer(sender);
            dest = Destination.get(args[0], sender);
        break;
        case 2: // /tele <what> <destination>
            target = Target.get(args[0], sender);
            dest = Destination.get(args[1], sender);
        break;
        default:
            return SHOW_USAGE;
        }
        if(dest == null || target == null) return true;
        if(target.hasPermission(sender) && dest.hasPermission(sender, "teleport", "general.teleport")) {
            target.teleport(dest);
            Messaging.send(sender, "&fYou teleported &9" + target.getName() + "&f to &9" + dest.getName() + "&f!");
        }
        return true;
    }

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        Target target;
        Destination dest;
        switch(args.length) {
        case 2: // /tele <what> <destination>
            target = Target.get(args[0], null);
            dest = Destination.get(args[1], null);
        break;
        default:
            return SHOW_USAGE;
        }
        if(dest == null || target == null) return true;
        target.teleport(dest);
        Messaging.send(sender, "&fYou teleported &9" + target.getName() + "&f to &9" + dest.getName() + "&f!");
        return true;
    }
}
