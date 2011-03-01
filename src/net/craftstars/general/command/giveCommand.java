package net.craftstars.general.command;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.craftstars.general.General;
import net.craftstars.general.util.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class giveCommand extends GeneralCommand
{
	
	@Override
	public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel, String[] args)
	{
		// TODO: Rewrite this. [Plutonium239]
		if (args.length < 1)
		{
			Messaging.send(sender, "&cUsage: /give [item(:type)|player] [item(:type)|amount] (amount)");
			
			return true;
		}
		
		int itemId = 0;
		int[] tmp;
		int amount = 1;
		int dataType = -1;
		Player who = null;
		
		try {
			if (args[0].contains(":")) {
				String[] data = args[0].split(":");
	
				try {
					dataType = Integer.valueOf(data[1]);
				} catch (NumberFormatException e) {
					dataType = -1;
				}
	
				tmp = Items.validate(data[0]);
				itemId = tmp[0];
			} else {
				tmp = Items.validate(args[0]);
				itemId = tmp[0];
				dataType = tmp[1];
			}
	
			if (itemId == -1) {
				who = Toolbox.playerMatch(args[0]);
			}
		} catch (NumberFormatException e) {
			who = Toolbox.playerMatch(args[0]);
		}

		if ((itemId == 0 || itemId == -1) && who != null) {
			String i = args[1];
	
			if (i.contains(":")) {
				String[] data = i.split(":");
	
				try {
					dataType = Integer.valueOf(data[1]);
				} catch (NumberFormatException e) {
					dataType = -1;
				}
	
				i = data[0];
			}
	
			tmp = Items.validate(i);
			itemId = tmp[0];
	
			if (dataType == -1) {
				dataType = Items.validateGrabType(i);
			}
		}

		if (itemId == -1 || itemId == 0) {
			Messaging.send("&cInvalid item.");
			return true;
		}

		if (dataType != -1) {
			if (!Items.validateType(itemId, dataType)) {
				Messaging.send("&f" + dataType + "&c is not a valid data type for &f" + Items.name(itemId, -1) + "&c.");
				return true;
			}
		}

		if (args.length >= 2 && who == null) {
			try {
				amount = Integer.valueOf(args[1]);
			} catch (NumberFormatException e) {
				amount = 1;
			}
		} else if (args.length >= 3) {
			if (who != null) {
				try {
					amount = Integer.valueOf(args[2]);
				} catch (NumberFormatException e) {
					amount = 1;
				}
			} else {
				who = Toolbox.playerMatch(args[2]);
			}
		}

		if (amount == 0) { // give one stack
			if (itemId == 332 || itemId == 344) {
				amount = 16; // eggs and snowballs
			} else if (Items.isStackable(itemId)) {
				amount = 64;
			} else {
				amount = 1;
			}
		}

		if (who == null) {
			who = sender;
		}

		int slot = who.getInventory().firstEmpty();

		if (dataType != -1) {
			if (slot < 0) {
				who.getWorld().dropItem(who.getLocation(), new ItemStack(itemId, amount, ((byte) dataType)));
			} else {
				who.getInventory().addItem(new ItemStack(itemId, amount, ((byte) dataType)));
			}
		} else {
			if (slot < 0) {
				who.getWorld().dropItem(who.getLocation(), new ItemStack(itemId, amount));
			} else {
				who.getInventory().addItem(new ItemStack(itemId, amount));
			}
		}

		if (who.getName().equals(sender.getName())) {
			Messaging.send(who, "&2Enjoy! Giving &f" + amount + "&2 of &f" + Items.name(itemId, dataType) + "&2.");
		} else {
			Messaging.send(who, "&2Enjoy the gift! &f" + amount + "&2 of &f" + Items.name(itemId, dataType) + "&2!");
		}
		
		return true;
	}
	
}