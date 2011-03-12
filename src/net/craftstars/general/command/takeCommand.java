
package net.craftstars.general.command;

import java.util.Map;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class takeCommand extends CommandBase {
    private Player who;
    private Items.ItemID item;
    private int amount;

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length < 1 || args[0].equalsIgnoreCase("help")) return Toolbox.USAGE;

        who = null;
        item = null;
        amount = 0;

        switch(args.length) {
        case 2: // take <item>[:<data>] <player>
            who = Toolbox.getPlayer(args[1],sender);
            if(who == null) return true;
            item = Items.validate(args[0]);
        break;
        case 3: // take <player> <item>[:<data>] <amount>
            who = Toolbox.getPlayer(args[2], sender);
            if(who == null) return true;
            item = Items.validate(args[0]);
            try {
                amount = Integer.valueOf(args[1]);
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

        doTake();
        Messaging.send(sender, "&2Took &f" + amount + "&2 of &f"
                + Items.name(item.ID, item.data) + "&2 from &f" + who.getName() + "&2!");
        return true;
    }

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "general.take")) return true;
        if(args.length < 1 || args[0].equalsIgnoreCase("help")) return Toolbox.USAGE;

        who = sender;
        item = null;
        amount = 0;

        switch(args.length) {
        case 1: // /take <item>[:<data>]
            item = Items.validate(args[0]);
        break;
        case 2: // /take <item>[:<data>] <amount> OR /give <item>[:<data>] <player>
            item = Items.validate(args[0]);
            try {
                who = sender;
                amount = Integer.valueOf(args[1]);
            } catch(NumberFormatException x) {
                who = Toolbox.playerMatch(args[1]);
                Messaging.send(sender, "&rose;The amount must be an integer.");
                Messaging.send(sender, "&rose;There is no player named &f" + args[1] + "&rose;.");
                return true;
            }
        break;
        case 3: // /take <item>[:<data>] <amount> <player>
            who = Toolbox.getPlayer(args[2], sender);
            if(who == null) return true;
            item = Items.validate(args[0]);
            try {
                amount = Integer.valueOf(args[1]);
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

        doTake();
        if(!sender.getName().equalsIgnoreCase(who.getName()))
            Messaging.send(sender, "&2Took &f" + amount + "&2 of &f" + Items.name(item.ID, item.data)
                    + "&2 from &f" + who.getName());
        return true;
    }

    private void doTake() {
        int removed = 0;
        if(amount <= 0) {
            who.getInventory().remove(item.ID);
        } else {
            PlayerInventory i = who.getInventory();
            Map<Integer, ? extends ItemStack> items = i.all(item.ID);
            for(int x : items.keySet()) {
                ItemStack stk = items.get(x);
                int n, d;
                n = stk.getAmount();
                try {
                    d = stk.getData().getData();
                } catch(NullPointerException ex) {
                    d = stk.getDurability();
                }
                if(!Items.isDamageable(item.ID) && item.data != d) continue;
                if(n > amount) {
                    stk.setAmount(n - amount);
                    removed += amount;
                    amount = 0;
                    break;
                } else if(n <= amount) {
                    amount -= n;
                    removed += n;
                    i.setItem(x, null);
                }
                if(amount <= 0) break;
            }
        }
        Messaging.send(who, "&f" + (removed == 0 ? "All" : removed) + "&2 of &f" + Items.name(item.ID, item.data)
                + "&2 was taken from you.");
    }
}
