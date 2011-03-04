
package net.craftstars.general.command;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class tellCommand extends GeneralCommand {

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(!plugin.permissions.hasPermission(sender, "general.tell")) {
            Messaging.send(sender, "&rose;You don't have permission to do that.");
            return true;
        }
        if(args.length < 2) {
            return false;
        }

        Player who = Toolbox.playerMatch(args[0]);

        if(who != null) {
            if(who.getName().equals(sender.getName())) {
                Messaging.send(sender, "&c;You can't message yourself!");

                return true;
            }

            Messaging.send(sender, "(MSG) To <" + who.getName() + "> " + this.getMessage(args));
            Messaging.send(who, "(MSG) <" + sender.getName() + "> " + this.getMessage(args));
        } else {
            Messaging.send(sender, "&cCouldn't find player " + args[0]);
        }

        return true;
    }

    private String getMessage(String[] args) {
        String message = null;

        for(int i = 1; i < (args.length - 1); i++) {
            message = message + args[i] + " ";
        }

        return message;
    }

}
