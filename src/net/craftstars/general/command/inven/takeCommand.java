
package net.craftstars.general.command.inven;

import java.util.Map;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class takeCommand extends CommandBase {
	private Player who;
	private ItemID item;
	private int amount;
	private boolean sell;
	
	public takeCommand(General instance) {
		super(instance);
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		if(args.length < 1 || args[0].equalsIgnoreCase("help")) return SHOW_USAGE;
		
		who = null;
		item = null;
		amount = 0;
		
		switch(args.length) {
		case 2: // take <item>[:<data>] <player>
			who = Toolbox.matchPlayer(args[1]);
			if(who == null) return Messaging.invalidPlayer(sender, args[1]);
			item = Items.validate(args[0]);
		break;
		case 3: // take <item>[:<data>] <amount> <player>
			who = Toolbox.matchPlayer(args[2]);
			if(who == null) return Messaging.invalidPlayer(sender, args[2]);
			item = Items.validate(args[0]);
			try {
				amount = Integer.valueOf(args[1]);
			} catch(NumberFormatException x) {
				Messaging.send(sender, "&rose;The amount must be an integer.");
				return true;
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
			Messaging.send(sender, "&f" + item.getVariant() + "&rose; is not a valid data type for &f" +
				item.getName() + "&rose;.");
			return true;
		}
		
		amount = doTake();
		Messaging.send(sender, "&2Took &f" + amount + "&2 of &f" + item.getName() + "&2 from &f" + who.getName()
				+ "&2!");
		return true;
	}
	
	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.take"))
			return Messaging.lacksPermission(sender, "remove items from your inventory");
		if(args.length < 1 || args[0].equalsIgnoreCase("help")) return SHOW_USAGE;
		
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
				who = Toolbox.matchPlayer(args[1]);
				if(who == null) {
					Messaging.send(sender, "&rose;The amount must be an integer.");
					Messaging.invalidPlayer(sender, args[1]);
					return true;
				}
			}
		break;
		case 3: // /take <item>[:<data>] <amount> <player>
			who = Toolbox.matchPlayer(args[2]);
			if(who == null) return Messaging.invalidPlayer(sender, args[2]);
			item = Items.validate(args[0]);
			try {
				amount = Integer.valueOf(args[1]);
			} catch(NumberFormatException x) {
				Messaging.send(sender, "&rose;The amount must be an integer.");
				return true;
			}
		break;
		default:
			return SHOW_USAGE;
		}
		
		if(!sender.equals(who) && Toolbox.lacksPermission(sender, "general.take.other"))
			return Messaging.lacksPermission(sender, "take items from someone else's inventory");
		
		if(item == null || !item.isIdValid()) {
			Messaging.send(sender, "&rose;Invalid item.");
			return true;
		}
		
		if(!item.isDataValid()) {
			Messaging.send(sender, "&f" + item.getVariant() + "&rose; is not a valid data type for &f" + 
				item.getName() + "&rose;.");
			return true;
		}

		sell = who.equals(sender);
		amount = doTake();
		if(!sender.getName().equalsIgnoreCase(who.getName()))
			Messaging.send(sender, "&2Took &f" + amount + "&2 of &f" + item.getName() + "&2 from &f" + who.getName());
		return true;
	}
	
	private int doTake() {
		sell = sell && plugin.economy != null;
		sell = sell && plugin.config.getString("economy.give.take", "sell").equalsIgnoreCase("sell");
		int removed = 0;
		PlayerInventory i = who.getInventory();
		Map<Integer, ? extends ItemStack> items = i.all(item.getId());
		for(int x : items.keySet()) {
			ItemStack stk = items.get(x);
			int n, d;
			n = stk.getAmount();
			d = stk.getDurability();
			if(!Items.dataEquiv(item, d)) continue;
			if(amount > 0 && n > amount) {
				stk.setAmount(n - amount);
				removed += amount;
				amount = 0;
				break;
			} else if(n <= amount) {
				amount -= n;
				removed += n;
				i.setItem(x, null);
			} else if(amount <= 0) i.setItem(x, null);
		}
		Messaging.send(who, "&f" + (removed == 0 ? "All" : removed) + "&2 of &f" + item.getName()
			+ "&2 was taken from you.");
		if(sell) {
			double revenue = Toolbox.sellItem(item, removed);
			Messaging.earned(who, revenue);
		}
		return removed;
	}
}
