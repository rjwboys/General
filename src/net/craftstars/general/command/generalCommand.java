package net.craftstars.general.command;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.HelpHandler;
import net.craftstars.general.util.MessageOfTheDay;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class generalCommand extends CommandBase {

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length < 1) return Toolbox.SHOW_USAGE;
        if(args[0].equalsIgnoreCase("reload")) {
            doReload(sender);
            return true;
        } else if(args[0].equalsIgnoreCase("die")) {
            die(sender);
            return true;
        } else if(args[0].equalsIgnoreCase("help")) {
            if(args.length == 1) {
                HelpHandler.showHelp(sender, "console.help");
                return true;
            } else if(args.length == 2) {
                HelpHandler.showHelp(sender, args[1] + ".help");
                return true;
            }
        } else if(args[0].equalsIgnoreCase("motd")) {
            MessageOfTheDay.showMotD(sender);
            return true;
        } else if(args[0].equalsIgnoreCase("item")) {
            if(args.length < 3) {
                Messaging.send(sender, "&cNot enough arguments.");
                return Toolbox.SHOW_USAGE;
            }
            return itemEdit(sender, Arrays.copyOfRange(args, 1, args.length));
        }
        return Toolbox.SHOW_USAGE;
    }

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(args.length < 1) return Toolbox.SHOW_USAGE;
        if(args[0].equalsIgnoreCase("reload")) {
            if(Toolbox.lacksPermission(plugin, sender, "administrate the plugin", "general.admin")) return true;
            doReload(sender);
            return true;
        } else if(args[0].equalsIgnoreCase("help")) {
            if(args.length == 1) {
                HelpHandler.showHelp(sender, "player.help");
                return true;
            } else if(args.length == 2) {
                HelpHandler.showHelp(sender, args[1] + ".help");
                return true;
            }
        } else if(args[0].equalsIgnoreCase("motd")) {
            MessageOfTheDay.showMotD(sender);
            return true;
        } else if(args[0].equalsIgnoreCase("item")) {
            if(Toolbox.lacksPermission(plugin, sender, "administrate the plugin", "general.admin")) return true;
            if(args.length < 3) {
                Messaging.send(sender, "&cNot enough arguments.");
                return Toolbox.SHOW_USAGE;
            }
            return itemEdit(sender, Arrays.copyOfRange(args, 2, args.length));
        }
        return Toolbox.SHOW_USAGE;
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

    private boolean itemEdit(CommandSender sender, String[] args) {
        if(args.length < 2 || args.length > 3) {
            return Toolbox.SHOW_USAGE;
        } else if(args[0].equalsIgnoreCase("alias")) {
            switch(args.length) {
            case 2:
                if(args[1].charAt(0) == '-') {
                    Items.removeAlias(args[1].substring(1));
                    Messaging.send(sender, "Alias " + args[1].substring(1) + " removed.");
                } else {
                    Messaging.send(sender, "The alias " + args[1] + " refers to " + Items.getAlias(args[1]));
                }
                return true;
            case 3:
                ItemID id = Items.validate(args[2]);
                Items.addAlias(args[1], id);
                Messaging.send(sender, "Alias " + args[1] + " added for " + id);
                return true;
            }
        } else if(args[0].equalsIgnoreCase("variant")) {
            ItemID id = Items.validate(args[1]);
            if(id == null) {
                Messaging.send(sender, "&cNo such item.");
                return true;
            }
            switch(args.length) {
            case 2:
                Messaging.send(sender, "Variant names for " + id + ": " + Items.variantNames(id));
                return true;
            case 3:
                switch(args[2].charAt(0)) {
                default:
                    if(args[2].contains(","))
                        Items.setVariantNames(id, Arrays.asList(args[2].split(",")));
                    else Items.addVariantName(id, args[2]);
                break;
                case '+':
                    Items.addVariantName(id, args[2].substring(1));
                break;
                case '-':
                    Items.removeVariantName(id, args[2].substring(1));
                break;
                case '=':
                    Items.setVariantNames(id, Arrays.asList(args[2].substring(1).split(",")));
                break;
                }
                Messaging.send(sender, "Variant names for " + id + " are now: " + Items.variantNames(id));
                return true;
            }
        } else if(args[0].equalsIgnoreCase("name")) {
            ItemID id = Items.validate(args[1]);
            switch(args.length) {
            case 2:
                String name = Items.name(id);
                Messaging.send(sender, "The name of item ID " + id + " is " + name);
                return true;
            case 3:
                Items.setItemName(id, args[2].replace("_"," "));
                Messaging.send(sender, "Item ID " + id + " is now called " + args[2].replace("_"," "));
                return true;
            }
        } else if(args[0].equalsIgnoreCase("hook")) {
            String hook[] = args[1].split("[:/,.|]");
            switch(args.length) {
            case 2:
                Messaging.send(sender, "The hook " + hook[0] + ":" + hook[1] + " refers to " + Items.getHook(hook[0], hook[1]));
                return true;
            case 3:
                ItemID id = Items.validate(args[2]);
                Items.setHook(hook[0], hook[1], id);
                Messaging.send(sender, "The hook " + hook[0] + ":" + hook[1] + " now refers to " + id);
                return true;
            }
        }
        return Toolbox.SHOW_USAGE;
    }

}
