
package net.craftstars.general.command.inven;

import java.util.Map;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class takeCommand extends CommandBase {
    private Player who;
    private ItemID item;
    private int amount;

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length < 1 || args[0].equalsIgnoreCase("help")) return Toolbox.SHOW_USAGE;

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
            return Toolbox.SHOW_USAGE;
        }

        if(item == null || !item.isIdValid()) {
            Messaging.send(sender, "&rose;Invalid item.");
            return true;
        }

        if(!item.isDataValid()) {
            Messaging.send(sender, "&f" + item.getVariant()
                    + "&rose; is not a valid data type for &f" + Items.name(item) + "&rose;.");
            return true;
        }

        doTake();
        Messaging.send(sender, "&2Took &f" + amount + "&2 of &f"
                + Items.name(item) + "&2 from &f" + who.getName() + "&2!");
        return true;
    }

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "remove items from your inventory", "general.take")) return true;
        if(args.length < 1 || args[0].equalsIgnoreCase("help")) return Toolbox.SHOW_USAGE;

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
            return Toolbox.SHOW_USAGE;
        }
        
        if(!sender.equals(who) && Toolbox.lacksPermission(plugin, sender, "take items from someone else's inventory", "general.take.other"))
            return true;

        if(item == null || !item.isIdValid()) {
            Messaging.send(sender, "&rose;Invalid item.");
            return true;
        }

        if(!item.isDataValid()) {
            Messaging.send(sender, "&f" + item.getVariant()
                    + "&rose; is not a valid data type for &f" + Items.name(item) + "&rose;.");
            return true;
        }

        doTake();
        if(!sender.getName().equalsIgnoreCase(who.getName()))
            Messaging.send(sender, "&2Took &f" + amount + "&2 of &f" + Items.name(item)
                    + "&2 from &f" + who.getName());
        return true;
    }

    private void doTake() {
        int removed = 0;
        //if(amount <= 0) {
        //    who.getInventory().remove(item.getId());
        //} else {
            PlayerInventory i = who.getInventory();
            Map<Integer, ? extends ItemStack> items = i.all(item.getId());
            for(int x : items.keySet()) {
                ItemStack stk = items.get(x);
                int n, d;
                n = stk.getAmount();
                d = stk.getDurability();
                if(!dataEquiv(item, d)) continue;
                //if(!Items.isDamageable(item.getId()) && item.getData() != d) continue;
                if(amount > 0 && n > amount) {
                    stk.setAmount(n - amount);
                    removed += amount;
                    amount = 0;
                    break;
                } else if(n <= amount) {
                    amount -= n;
                    removed += n;
                    i.setItem(x, null);
                } else if(amount <= 0)
                    i.setItem(x, null);
            }
        //}
        Messaging.send(who, "&f" + (removed == 0 ? "All" : removed) + "&2 of &f" + Items.name(item)
                + "&2 was taken from you.");
    }

    private boolean dataEquiv(ItemID id, int data) {
        if(Items.isDamageable(id.getId())) return true;
        if(id.getData() == null) return true;
        return data == id.getData();
    }
}
