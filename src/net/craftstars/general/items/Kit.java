package net.craftstars.general.items;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bukkit.command.CommandSender;

import net.craftstars.general.General;
import net.craftstars.general.option.Options;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Toolbox;

public class Kit implements Iterable<ItemID> {
	private Map<ItemID, Integer> items;
	public int delay;
	private double savedCost;
	private String[] cost;
	private String name;
	
	@SuppressWarnings("hiding")
	public Kit(String name, int delay, double cost) {
		this.name = name;
		this.items = new LinkedHashMap<ItemID, Integer>();
		this.delay = delay;
		this.savedCost = cost;
		calculateCost();
	}
	
	@Override
	public int hashCode() {
		return items.hashCode() * delay;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof Kit) {
			return items.equals(((Kit) other).items);
		}
		return false;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean canGet(CommandSender who) {
		String node = getPermission();
		if(who.hasPermission(node)) return true;
		Messaging.lacksPermission(who, node, LanguageText.LACK_KIT_NAME, "kit", name);
		return false;
	}

	public String getPermission() {
		return "general.kit." + name.toLowerCase();
	}
	
	public boolean canAfford(CommandSender who) {
		if(EconomyManager.canPay(who, 1, cost)) return true;
		return false;
	}
	
	private void calculateCost() {
		// First, determine method of costing; then, calculate actual cost.
		String method = Options.KIT_METHOD.get();
		if(Toolbox.equalsOne(method, "cumulative", "discount")) {
			// Linked-list for constant-time add
			LinkedList<String> econNodes = new LinkedList<String>();
			for(ItemID item : items.keySet()) {
				int quantity = items.get(item);
				while(quantity-- > 0) econNodes.add("general.economy.give.item" + item.toString());
			}
			if(method.equalsIgnoreCase("discount"))
				econNodes.add("%" + Options.KIT_DISCOUNT.get());
			cost = econNodes.toArray(new String[0]);
		} else {
			if(!method.equalsIgnoreCase("individual"))
				General.logger.warn(LanguageText.LOG_KIT_BAD_METHOD.value());
			cost = new String[] {"$" + savedCost};
		}
	}

	public double getCost() {
		return savedCost;
	}
	
	@SuppressWarnings("hiding")
	public void setSavedCost(double cost) {
		savedCost = cost;
	}

	@Override
	public Iterator<ItemID> iterator() {
		return items.keySet().iterator();
	}

	public void add(ItemID item, int amount) {
		int current = 0;
		if(items.containsKey(item)) current = items.get(item);
		amount += current;
		if(amount <= 0) items.remove(item);
		else items.put(item, amount);
	}

	public int get(ItemID item) {
		return items.get(item);
	}

	public boolean contains(ItemID item) {
		return items.containsKey(item);
	}
}