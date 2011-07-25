
package net.craftstars.general.command.inven;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;

public class masstakeCommand extends CommandBase {
	public masstakeCommand(General instance) {
		super(instance);
	}
	
	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		if(args.length < (isPlayer ? 1 : 2)) return null;
		HashMap<String,Object> params = new HashMap<String,Object>();
		Player who = Toolbox.matchPlayer(args[args.length-1]);
		if(who == null) {
			if(isPlayer) who = (Player) sender;
			else {
				Messaging.invalidPlayer(sender, args[args.length-1]);
				return null;
			}
		}
		else args[args.length-1] = null;
		ArrayList<ItemID> items = new ArrayList<ItemID>();
		for(String item : args) {
			if(item != null){
				ItemID itemid = Items.validate(item);
				if(itemid != null && itemid.isIdValid() && itemid.isDataValid()) {
					items.add(itemid);
				}
			}
		}
		params.put("player", who);
		params.put("items", items);
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.take.mass"))
			return Messaging.lacksPermission(sender, "massively remove items from your inventory");
		Player who = (Player) args.get("player");
		if(!sender.equals(who) && Toolbox.lacksPermission(sender, "general.take.other"))
			return Messaging.lacksPermission(sender, "massively take items from someone else's inventory");
		@SuppressWarnings("unchecked")
		ArrayList<ItemID> items = (ArrayList<ItemID>) args.get("items");
		boolean sell = who.equals(sender);
		sell = sell && plugin.economy != null;
		sell = sell && plugin.config.getString("economy.give.take", "sell").equalsIgnoreCase("sell");
		StringBuilder itemsText = new StringBuilder();
		int amount = doTake(who, items, sell, itemsText);
		if(!sender.equals(who))
			Messaging.send(sender, "&2Took &f" + amount + "&2 of &f" + itemsText.toString()
				+ "&2 from &f" + who.getName());
		return true;
	}
	
	private int doTake(Player who, ArrayList<ItemID> items, boolean sell, StringBuilder itemsText) {
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
