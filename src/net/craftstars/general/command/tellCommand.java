
package net.craftstars.general.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class tellCommand extends GeneralCommand {

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "general.tell")) return true;
        if(args.length < 2) return Toolbox.USAGE;
        Player who = Toolbox.getPlayer(args[0], sender);
        if(who != null) {
            if(who.getName().equals(sender.getName())) {
                Messaging.send(sender, "&c;You can't message yourself!");
                return true;
            }
            Messaging.send(sender, "&gray;(whisper)   to <" + who.getName() + "> " + this.getMessage(args));
            Messaging.send(who, "&gray;(whisper) from <" + sender.getName() + "> " + this.getMessage(args));
        }
        return true;
    }

    private String getMessage(String[] args) {
        StringBuilder message = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            message.append(args[i]);
            message.append(" ");
        }
        return message.toString();
    }

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length < 2) return Toolbox.USAGE;
        Player who = Toolbox.getPlayer(args[0],sender);
        if(who != null) {
            Messaging.send(sender,"&gray;(whisper)   to <" + who.getName() + "> " + this.getMessage(args));
            Messaging.send(who, "(whisper) from [CONSOLE] " + this.getMessage(args));
        } else {
            Messaging.send(sender,"Couldn't find player " + args[0]);
        }
        return true;
    }
}
