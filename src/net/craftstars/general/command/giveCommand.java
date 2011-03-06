
package net.craftstars.general.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class giveCommand extends CommandBase {
    private Player who;
    private Items.ItemID item;
    private int amount;

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "general.give")) return true;
        if(args.length < 1 || args[0].equalsIgnoreCase("help")) return Toolbox.USAGE;

        who = sender;
        item = null;
        amount = 1;

        switch(args.length) {
        case 1: // /give <item>[:<data>]
            if(args[0].equalsIgnoreCase("help")) {
                showHelp(sender);
                return true;
            }
            item = Items.validate(args[0]);
        break;
        case 2: // /give <player> <item>[:<data>] OR /give <item>[:<data>] <amount>
            // TODO: Consider '/give stone 1' - what if there is a player called stone? Is the ambiguity resolvable?
            who = Toolbox.playerMatch(args[0]);
            if(who == null) {
                who = sender;
                item = Items.validate(args[0]);
                try {
                    amount = Integer.valueOf(args[1]);
                } catch(NumberFormatException x) {
                    Messaging.send(sender, "&rose;The amount must be an integer.");
                    return true;
                }
            } else {
                item = Items.validate(args[1]);
            }
        break;
        case 3: // /give <player> <item>[:<data>] <amount>
            who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;
            item = Items.validate(args[1]);
            try {
                amount = Integer.valueOf(args[2]);
            } catch(NumberFormatException x) {
                Messaging.send(sender, "&rose;The amount must be an integer.");
                return true;
            }
        break;
        default:
            return Toolbox.USAGE;
        }

        if(item.ID == -1) {
            Messaging.send(sender, "&rose;Invalid item.");
            return true;
        }

        if(item.data == -1) {
            Messaging.send(sender, "&f" + Items.lastDataError
                    + "&rose; is not a valid data type for &f" + Items.name(item.ID, 0) + "&rose;.");
            return true;
        }

        if(amount < 0 && Toolbox.lacksPermission(plugin, sender, "general.give.infinite")) return true;
        // Make sure this player is allowed this particular item
        if(!canGetItem(sender)) {
            Messaging.send(sender, "&2You're not allowed to get &f" + Items.name(item.ID, item.data) + "&2.");
            return true;
        }

        boolean isGift = !who.getName().equals(sender.getName());
        doGive(isGift);
        if(isGift) {
            Messaging.send(sender, "&2Gave &f" + amount + "&2 of &f"
                    + Items.name(item.ID, item.data) + "&2 to &f" + who.getName() + "&2!");
        }

        return true;
    }

    private boolean canGetItem(Player sender) {
        ConfigurationNode permissions = General.plugin.config.getNode("give");
        List<String> groups = permissions.getKeys("groups");
        for(String group : groups) {
            List<Integer> items = permissions.getIntList("groups." + group, null);
            if(items.isEmpty()) continue;
            if(items.contains(item.ID)) {
                return General.plugin.permissions.hasPermission(sender, "general.give.group." + group);
            }
        }
        return true;
    }

    private void doGive(boolean isGift) {
        if(amount == 0) { // give one stack
            amount = Items.maxStackSize(item.ID);
        }

        int slot = who.getInventory().firstEmpty();

        if(slot < 0) {
            who.getWorld().dropItem(who.getLocation(),
                    new ItemStack(item.ID, amount, ((byte) item.data)));
        } else {
            who.getInventory().addItem(new ItemStack(item.ID, amount, (byte) item.data));
        }

        if(isGift) {
            Messaging.send(who, "&2Enjoy the gift! &f" + amount + "&2 of &f"
                    + Items.name(item.ID, item.data) + "&2!");
        } else {
            Messaging.send(who, "&2Enjoy! Giving &f" + amount + "&2 of &f"
                    + Items.name(item.ID, item.data) + "&2.");
        }
    }

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length < 1 || args[0].equalsIgnoreCase("help")) return Toolbox.USAGE;

        who = null;
        item = null;
        amount = 1;

        switch(args.length) {
        case 1:
            if(!args[0].equalsIgnoreCase("help")) return Toolbox.USAGE;
            showHelp(sender);
            return true;
        case 2:
            who = Toolbox.playerMatch(args[0]);
            if(who == null) {
                return Toolbox.USAGE;
            } else {
                item = Items.validate(args[1]);
            }
        break;
        case 3:
            who = Toolbox.playerMatch(args[0]);
            item = Items.validate(args[1]);
            try {
                amount = Integer.valueOf(args[2]);
            } catch(NumberFormatException x) {
                Messaging.send(sender, "&rose;The amount must be an integer.");
                return true;
            }
        break;
        default:
            return Toolbox.USAGE;
        }

        if(item.ID == -1) {
            Messaging.send(sender, "&rose;Invalid item.");
            return true;
        }

        if(item.data == -1) {
            Messaging.send(sender, "&f" + Items.lastDataError
                    + "&rose; is not a valid data type for &f" + Items.name(item.ID, 0) + "&rose;.");
            return true;
        }

        doGive(true);

        return true;
    }

    private void showHelp(CommandSender sender) {
        Messaging.send(sender, "&c/give &7[item]&c (&7[amount]&c)&f : Gives something to you.");
        Messaging.send(sender, "&c/give &7[player] [item]&c (&7[amount]&c)&f : Gives something to someone else.");
        Messaging.send(sender, "&fAn amount of &7-1&f is an infinite stack; &70&f is one full stack.");
        Messaging.send(sender, "&fThe &7[item]&f and &7[variant]&f both may be either a number or a name.");
        Messaging.send(sender, "&fExample: &c/give Notch wool:red 5&f : Gives a stack of five red wool to Notch.");
    }
}
