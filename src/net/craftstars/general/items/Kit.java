package net.craftstars.general.items;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.util.Toolbox;

public class Kit {
	public HashMap<ItemID, Integer> items;
	public int delay;
	private double savedCost;
	private String[] cost;
	private String name;
	
	@SuppressWarnings("hiding")
	public Kit(String name, HashMap<ItemID, Integer> item, int delay, double cost) {
		this.name = name;
		this.items = item;
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
			return items.equals( ((Kit) other).items);
		}
		return false;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean canGet(Player who) {
		if(Toolbox.hasPermission(who, "general.kit." + name.toLowerCase())) return true;
		return false;
	}
	
	public boolean canAfford(Player who) {
		if(Toolbox.canPay(who, 1, cost)) return true;
		return false;
	}
	
	private void calculateCost() {
		// First, determine method of costing; then, calculate actual cost.
		String method = General.plugin.config.getString("economy.give.kits");
		if(Toolbox.equalsOne(method, "cumulative", "discount")) {
			// Linked-list for constant-time add
			LinkedList<String> econNodes = new LinkedList<String>();
			for(ItemID item : items.keySet()) {
				int quantity = items.get(item);
				while(quantity-- > 0) econNodes.add("general.economy.give.item" + item.toString());
			}
			if(method.equalsIgnoreCase("discount"))
				econNodes.add("%" + General.plugin.config.getDouble("economy.give.discount", 80));
			cost = econNodes.toArray(new String[0]);
		} else {
			if(!method.equalsIgnoreCase("individual"))
				General.logger.warn("Invalid method for kit costing; falling back to default of 'individual'");
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
}