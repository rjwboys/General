
package net.craftstars.general.command.inven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.InvalidItemException;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Option;
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
		} else args = dropLastArg(args);
		ArrayList<ItemID> items = new ArrayList<ItemID>();
		ArrayList<String> bad = new ArrayList<String>();
		for(String item : args) {
			if(item != null){
				ItemID itemid;
				try {
					itemid = Items.validate(item);
					items.add(itemid);
				} catch(InvalidItemException e) {
					bad.add(item);
				}
			}
		}
		params.put("player", who);
		params.put("items", items);
		params.put("bad", bad);
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(!sender.hasPermission("general.take.mass"))
			return Messaging.lacksPermission(sender, "general.take.mass");
		Player who = (Player) args.get("player");
		if(!sender.equals(who) && !sender.hasPermission("general.take.other"))
			return Messaging.lacksPermission(sender, "general.take.other");
		@SuppressWarnings("unchecked")
		ArrayList<ItemID> items = (ArrayList<ItemID>) args.get("items");
		boolean sell = who.equals(sender);
		sell = sell && Option.NO_ECONOMY.get();
		sell = sell && Option.ECONOMY_TAKE_SELL.get().equalsIgnoreCase("sell");
		StringBuilder itemsText = new StringBuilder();
		int amount = doTake(who, items, sell, itemsText);
		if(!sender.equals(who))
			Messaging.send(sender, LanguageText.MASSTAKE_THEFT.value("items", itemsText.toString(),
				"player", who.getName(), "amount", amount));
		@SuppressWarnings("unchecked")
		ArrayList<String> bad = (ArrayList<String>)args.get("bad");
		if(!bad.isEmpty()) Messaging.send(sender, LanguageText.LIST_BAD_ITEMS.value("items", bad.toString()));
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
					if(sell) revenue += EconomyManager.sellItem(id, amount);
					i.setItem(j, null);
				}
		}
		ArrayList<String> display = new ArrayList<String>();
		for(ItemID item : items) display.add(item.getName());
		itemsText.append(Toolbox.join(display.toArray(new String[0]), LanguageText.ITEMS_JOINER.value()));
		Messaging.send(who, LanguageText.MASSTAKE_TOOK.value("items", itemsText.toString(), "amount", removed));
		if(sell) {
			EconomyManager.giveMoney(who, revenue);
			Messaging.earned(who, revenue);
		}
		return removed;
	}
}
