
package net.craftstars.general.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.craftstars.general.General;
import net.craftstars.general.util.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class giveCommand extends GeneralCommand {

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        // TODO: Rewrite this. [Plutonium239]
        Messaging.save(sender);
        General.logger.debug("Handling command /give... checking permissions.");
        if(!plugin.permissions.hasPermission(sender, "general.give")) {
            Messaging.send(sender, "&rose;You don't have permission to do that.");
            return true;
        }
        General.logger.debug("Handling command /give... checking arguments.");

        // if(args.length < 1) {
        // Messaging.send(sender,
        // "&rose;Usage: /give [item(:type)|player] [item(:type)|amount] (amount)");
        // return true;
        // }

        if(args.length < 1 || args[0].equalsIgnoreCase("help")) return false;

        Player who = sender;
        Items.ItemID item = null;
        int amount = 1;

        switch(args.length) {
        case 1:
            item = Items.validate(args[0]);
        break;
        case 2:
            who = Toolbox.playerMatch(args[0]);
            if(who == null) {
                who = sender;
                item = Items.validate(args[0]);
                try {
                    amount = Integer.valueOf(args[2]);
                } catch(NumberFormatException x) {
                    Messaging.send(sender, "&rose;The amount must be an integer.");
                    return true;
                }
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
            Messaging.send(sender,
                    "&rose;Usage: /give [item(:type)|player] [item(:type)|amount] (amount)");
            return true;
        }

        // int itemId = 0;
        // int[] tmp;
        // int amount = 1;
        // int dataType = -1;
        // Player who = null;
        //
        // try {
        // if(args[0].contains(":")) {
        // String[] data = args[0].split(":");
        //
        // try {
        // dataType = Integer.valueOf(data[1]);
        // } catch(NumberFormatException e) {
        // dataType = -1;
        // }
        //
        // tmp = Items.validate(data[0]);
        // itemId = tmp[0];
        // } else {
        // tmp = Items.validate(args[0]);
        // itemId = tmp[0];
        // dataType = tmp[1];
        // }
        //
        // if(itemId == -1) {
        // who = Toolbox.playerMatch(args[0]);
        // }
        // } catch(NumberFormatException e) {
        // who = Toolbox.playerMatch(args[0]);
        // }
        //
        // if( (itemId == 0 || itemId == -1) && who != null) {
        // String i = args[1];
        //
        // if(i.contains(":")) {
        // String[] data = i.split(":");
        //
        // try {
        // dataType = Integer.valueOf(data[1]);
        // } catch(NumberFormatException e) {
        // dataType = -1;
        // }
        //
        // i = data[0];
        // }
        //
        // tmp = Items.validate(i);
        // itemId = tmp[0];
        //
        // if(dataType == -1) {
        // dataType = Items.validateGrabType(i);
        // }
        // }
        General.logger.debug("Handling command /give... checking validity.");

        if(item.ID == -1) {
            General.logger.debug("Handling command /give... invalid item " + Integer.toString(item.ID)
                    + ".");
            Messaging.send("&rose;Invalid item.");
            return true;
        }

        if(item.data == -1) {
            General.logger.debug("Handling command /give... invalid data type.");
            Messaging.send("&f" + Items.lastDataError + "&rose; is not a valid data type for &f"
                    + Items.name(item.ID, 0) + "&rose;.");
            return true;
        }

        // if(args.length >= 2 && who == null) {
        // try {
        // amount = Integer.valueOf(args[1]);
        // } catch(NumberFormatException e) {
        // amount = 1;
        // }
        // } else if(args.length >= 3) {
        // if(who != null) {
        // try {
        // amount = Integer.valueOf(args[2]);
        // } catch(NumberFormatException e) {
        // amount = 1;
        // }
        // } else {
        // who = Toolbox.playerMatch(args[2]);
        // }
        // }
        General.logger.debug("Handling command /give... checking for max stack.");

        if(amount == 0) { // give one stack
            amount = Material.getMaterial(item.ID).getMaxStackSize();
        }
        General.logger.debug("Handling command /give... initiating give.");

        doGive(who, item, amount, who.getName().equals(sender.getName()));

        return true;
    }

    private void doGive(Player who, Items.ItemID item, int amount, boolean isGift) {
        int slot = who.getInventory().firstEmpty();
        General.logger.debug("Handling command /give... checking for free slot.");

        if(slot < 0) {
            who.getWorld().dropItem(who.getLocation(),
                    new ItemStack(item.ID, amount, ((byte) item.data)));
        } else {
            who.getInventory().addItem(new ItemStack(item.ID, amount, ((byte) item.data)));
        }
        General.logger.debug("Handling command /give... sending acknowledgement.");

        if(isGift) {
            Messaging.send(who, "&2Enjoy! Giving &f" + amount + "&2 of &f"
                    + Items.name(item.ID, item.data) + "&2.");
        } else {
            Messaging.send(who, "&2Enjoy the gift! &f" + amount + "&2 of &f"
                    + Items.name(item.ID, item.data) + "&2!");
        }
    }

}
