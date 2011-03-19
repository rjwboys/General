package net.craftstars.general.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.MessageOfTheDay;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class generalCommand extends CommandBase {

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length < 1) return Toolbox.USAGE;
        if(args[0].equalsIgnoreCase("reload")) {
            doReload(sender);
            return true;
        } else if(args[0].equalsIgnoreCase("die")) {
            die(sender);
            return true;
        } else if(args[0].equalsIgnoreCase("help")) {
            if(args.length == 1) {
                MessageOfTheDay.showHelp(sender, "console.help");
                return true;
            } else if(args.length == 2) {
                MessageOfTheDay.showHelp(sender, args[1] + ".help");
                return true;
            }
        } else if(args[0].equalsIgnoreCase("motd")) {
            MessageOfTheDay.showMotD(sender);
            return true;
        }
        return Toolbox.USAGE;
    }

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(args.length < 1) return Toolbox.USAGE;
        if(args[0].equalsIgnoreCase("reload")) {
            if(Toolbox.lacksPermission(plugin, sender, "administrate the plugin", "general.admin")) return true;
            doReload(sender);
            return true;
        } else if(args[0].equalsIgnoreCase("help")) {
            if(args.length == 1) {
                MessageOfTheDay.showHelp(sender, "player.help");
                return true;
            } else if(args.length == 2) {
                MessageOfTheDay.showHelp(sender, args[1] + ".help");
                return true;
            }
        } else if(args[0].equalsIgnoreCase("motd")) {
            MessageOfTheDay.showMotD(sender);
            return true;
        }
        return Toolbox.USAGE;
    }

    private void doReload(CommandSender sender) {
        General.plugin.getPluginLoader().disablePlugin(General.plugin);
        General.plugin.getPluginLoader().enablePlugin(General.plugin);
        Messaging.send(sender, "&5General reloaded.");
    }

    private void die(CommandSender sender) {
        General.plugin.getPluginLoader().disablePlugin(General.plugin);
        Messaging.send(sender, "&5General unloaded.");
    }

}
