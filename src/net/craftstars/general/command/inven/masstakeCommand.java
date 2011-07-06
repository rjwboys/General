
package net.craftstars.general.command.inven;

import java.util.ArrayList;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;

public class masstakeCommand extends CommandBase {
	private Player who;
	private boolean sell;
	private ArrayList<ItemID> items = new ArrayList<ItemID>();
	private StringBuilder itemsText = new StringBuilder();
	
	public masstakeCommand(General instance) {
		super(instance);
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		if(args.length < 2) return SHOW_USAGE;
		
		who = Toolbox.matchPlayer(args[args.length-1]);
		if(who == null) return Messaging.invalidPlayer(sender, args[args.length-1]);
		else args[args.length-1] = null;
		for (String item : args) {
			if(item!=null){
				ItemID itemid = Items.validate(item);
				if(itemid != null && itemid.isIdValid() && itemid.isDataValid()) {
					this.items.add(itemid);
				}
			}
		}
		
		int amount = doTake();
		Messaging.send(sender, "&2Took &f" + amount + "&2 of &fvarious items&2 from &f" + who.getName()
				+ "&2!");
		return true;
	}
	
	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.take.mass"))
			return Messaging.lacksPermission(sender, "massively remove items from your inventory");
		if(args.length < 1) return SHOW_USAGE;
		
		who = Toolbox.matchPlayer(args[args.length-1]);
		if(who == null) who = sender;
		else args[args.length-1] = null;
		for (String item : args) {
			if(item!=null){
				ItemID itemid = Items.validate(item);
				if(itemid != null && itemid.isIdValid() && itemid.isDataValid()) {
					this.items.add(itemid);
				}
			}
		}
		
		if(!sender.equals(who) && (Toolbox.lacksPermission(sender, "general.take.other") || Toolbox.lacksPermission(sender, "general.take.mass")))
			return Messaging.lacksPermission(sender, "massively take items from someone else's inventory");
		
		sell = who.equals(sender);
		int amount = doTake();
		if(!sender.getName().equalsIgnoreCase(who.getName()))
			Messaging.send(sender, "&2Took &f" + amount + "&2 of &f" + itemsText.toString()
				+ "&2 from &f" + who.getName());
		return true;
	}
	
	private int doTake() {
		sell = sell && plugin.economy != null;
		sell = sell && plugin.config.getString("economy.give.take", "sell").equalsIgnoreCase("sell");
		int removed = 0;
		double revenue = 0.0;
		PlayerInventory i = who.getInventory();
		ItemStack[] invenItems = i.getContents();
		for(int j = 0; j < invenItems.length; j++) {
			if(invenItems[j] == null) continue;
			int d = invenItems[j].getDurability();
			for(ItemID id : items)
				if(id.getId() == invenItems[j].getTypeId() && Items.dataEquiv(id, d)) {
					int amount = i.getItem(j).getAmount();
					removed += amount;
					if(sell) revenue += Toolbox.sellItem(id, amount);
					i.setItem(j, null);
				}
		}
		for(ItemID item : items) {
			itemsText.append(item.getName());
			itemsText.append("&2, &f");
		}
		int lastComma = itemsText.lastIndexOf("&2, &f");
		if(lastComma >= 0 && lastComma < itemsText.length())
			itemsText.delete(lastComma, itemsText.length());
		Messaging.send(who, "&f" + (removed == 0 ? "All" : removed) + "&2 of &f" + itemsText.toString()
			+ "&2 was taken from you.");
		if(sell) Messaging.earned(who, revenue);
		return removed;
	}
}
