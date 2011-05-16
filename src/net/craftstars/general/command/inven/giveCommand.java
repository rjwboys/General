
package net.craftstars.general.command.inven;

import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class giveCommand extends CommandBase {
	private Player who;
	private ItemID item;
	private int amount;
	
	public giveCommand(General instance) {
		super(instance);
	}
	
	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.give"))
			return Messaging.lacksPermission(sender, "give items");
		if(args.length < 1 || args[0].equalsIgnoreCase("help")) return SHOW_USAGE;
		
		who = sender;
		item = null;
		amount = 1;
		
		switch(args.length) {
		case 1: // /give <item>[:<data>]
			item = Items.validate(args[0]);
		break;
		case 2: // /give <item>[:<data>] <amount> OR /give <item>[:<data>] <player>
			item = Items.validate(args[0]);
			try {
				who = sender;
				amount = Integer.valueOf(args[1]);
			} catch(NumberFormatException x) {
				who = Toolbox.matchPlayer(args[1]);
				if(who == null) {
					Messaging.send(sender, "&rose;The amount must be an integer.");
					Messaging.send(sender, "&rose;There is no player named &f" + args[1] + "&rose;.");
					return true;
				}
			}
		break;
		case 3: // /give <item>[:<data>] <amount> <player> OR /give <player> <item>[:<data>]
				// <amount>
			try {
				amount = Integer.valueOf(args[2]);
				who = Toolbox.matchPlayer(args[0]);
				if(who == null) return Messaging.invalidPlayer(sender, args[0]);
				item = Items.validate(args[1]);
			} catch(NumberFormatException ex) {
				who = Toolbox.matchPlayer(args[2]);
				if(who == null) return Messaging.invalidPlayer(sender, args[2]);
				item = Items.validate(args[0]);
				try {
					amount = Integer.valueOf(args[1]);
				} catch(NumberFormatException x) {
					Messaging.send(sender, "&rose;The amount must be an integer.");
					return true;
				}
			}
		break;
		default:
			return SHOW_USAGE;
		}
		
		if(item == null || !item.isIdValid()) {
			Messaging.send(sender, "&rose;Invalid item.");
			return true;
		}
		
		if(!item.isDataValid()) {
			Messaging.send(sender,
					"&f" + item.getVariant() + "&rose; is not a valid data type for &f" + Items.name(item)
							+ "&rose;.");
			return true;
		}
		
		int maxAmount = General.plugin.config.getInt("give.mass", 64);
		if(amount < 0 && Toolbox.lacksPermission(sender, "general.give.infinite"))
			return Messaging.lacksPermission(sender, "give infinite stacks of items");
		if(amount > maxAmount && Toolbox.lacksPermission(sender, "general.give.mass"))
			return Messaging.lacksPermission(sender, "give masses of items");
		// Make sure this player is allowed this particular item
		if(!item.canGive(sender)) {
			Messaging.send(sender, "&2You're not allowed to get &f" + Items.name(item) + "&2.");
			return true;
		}
		
		boolean isGift = !who.getName().equals(sender.getName());
		doGive(isGift);
		if(isGift) {
			Messaging.send(sender, "&2Gave &f" + (amount < 0 ? "infinite" : amount) + "&2 of &f" + Items.name(item)
					+ "&2 to &f" + who.getName() + "&2!");
		}
		
		return true;
	}
	
	private void doGive(boolean isGift) {
		if(amount == 0) { // give one stack
			amount = Items.maxStackSize(item.getId());
		}
		
		int slot = who.getInventory().firstEmpty();
		
		if(slot < 0) {
			who.getWorld().dropItem(who.getLocation(), item.getStack(amount));
		} else {
			who.getInventory().addItem(item.getStack(amount));
		}
		
		if(isGift) {
			Messaging.send(who,
					"&2Enjoy the gift! &f" + (amount < 0 ? "infinite" : amount) + "&2 of &f" + Items.name(item)
							+ "&2!");
		} else {
			Messaging.send(who,
					"&2Enjoy! Giving &f" + (amount < 0 ? "infinite" : amount) + "&2 of &f" + Items.name(item)
							+ "&2.");
		}
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		if(args.length < 1 || args[0].equalsIgnoreCase("help")) return SHOW_USAGE;
		
		who = null;
		item = null;
		amount = 1;
		
		switch(args.length) {
		case 2: // give <item>[:<data>] <player>
			who = Toolbox.matchPlayer(args[1]);
			if(who == null) return Messaging.invalidPlayer(sender, args[1]);
			item = Items.validate(args[0]);
		break;
		case 3: // give <item>[:<data>] <amount> <player> OR give <player> <item>[:<data>] <amount>
			try {
				amount = Integer.valueOf(args[2]);
				who = Toolbox.matchPlayer(args[0]);
				if(who == null) return Messaging.invalidPlayer(sender, args[0]);
				item = Items.validate(args[1]);
			} catch(NumberFormatException ex) {
				who = Toolbox.matchPlayer(args[2]);
				if(who == null) return Messaging.invalidPlayer(sender, args[2]);
				item = Items.validate(args[0]);
				try {
					amount = Integer.valueOf(args[1]);
				} catch(NumberFormatException x) {
					Messaging.send(sender, "&rose;The amount must be an integer.");
					return true;
				}
			}
		break;
		default:
			return SHOW_USAGE;
		}
		
		if(item == null || !item.isIdValid()) {
			Messaging.send(sender, "&rose;Invalid item.");
			return true;
		}
		
		if(!item.isDataValid()) {
			Messaging.send(sender,
					"&f" + item.getVariant() + "&rose; is not a valid data type for &f" + Items.name(item)
							+ "&rose;.");
			return true;
		}
		
		doGive(true);
		Messaging.send(sender, "&2Gave &f" + (amount < 0 ? "infinite" : amount) + "&2 of &f" + Items.name(item)
				+ "&2 to &f" + who.getName() + "&2!");
		
		return true;
	}
}
