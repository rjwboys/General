package net.craftstars.general.items;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

import net.craftstars.general.General;
import net.craftstars.general.option.Options;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Toolbox;

public class Kit implements Iterable<Kit.Entry> {
	private Map<Key, Integer> items;
	public int delay;
	private double savedCost;
	private String[] cost;
	private String name;
	
	@SuppressWarnings("hiding")
	public Kit(String name, int delay, double cost) {
		this.name = name;
		this.items = new LinkedHashMap<Key, Integer>();
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
			for(Key entry : items.keySet()) {
				int quantity = items.get(entry);
				while(quantity-- > 0) econNodes.add("general.economy.give.item" + entry.item.toString());
				if(entry.ench != null) {
					ItemData enchant = ItemData.enchanting(entry.item.getMaterial());
					for(Enchantment ench : entry.ench.keySet()) {
						quantity = items.get(entry);
						while(quantity-- > 0)
							econNodes.add("general.economy.give.enchant" + enchant.getName(ench.getId()).toString());
					}
				}
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
	public Iterator<Entry> iterator() {
		return new KitIterator();
	}

	public void add(ItemID item, int amount, Map<Enchantment,Integer> ench) {
		Key key = new Key(item, ench);
		int current = 0;
		if(items.containsKey(key)) current = items.get(key);
		amount += current;
		if(amount <= 0) items.remove(key);
		else items.put(key, amount);
	}

	public int get(ItemID item) {
		return get(new Key(item,null));
	}

	public int get(ItemID item, Map<Enchantment,Integer> enchantments) {
		return get(new Key(item,enchantments));
	}

	public int get(Key entry) {
		return items.get(entry);
	}

	public boolean contains(ItemID item) {
		return items.containsKey(item);
	}
	
	public Set<Key> keySet() {
		return items.keySet();
	}
	
	public class Entry implements Map.Entry<ItemID,Map<Enchantment,Integer>> {
		ItemID key;
		Map<Enchantment,Integer> val;
		
		public Entry(Key pair) {
			key = pair.item;
			val = pair.ench;
		}

		@Override
		public ItemID getKey() {
			return key;
		}

		@Override
		public Map<Enchantment,Integer> getValue() {
			return val;
		}

		@Override
		public Map<Enchantment,Integer> setValue(Map<Enchantment,Integer> value) {
			// TODO: Implement this!
			throw new UnsupportedOperationException("Can't set kit item enchantments yet!");
		}
	}
	
	public class KitIterator implements Iterator<Entry> {
		private Iterator<Map.Entry<Key,Integer>> iter = items.entrySet().iterator();
		private Key current;
		private int leftToDispense = 0;
		@Override
		public boolean hasNext() {
			return leftToDispense > 0 || iter.hasNext();
		}

		@Override
		public Entry next() {
			if(leftToDispense <= 0) {
				Map.Entry<Key,Integer> next = iter.next();
				General.logger.debug("Kit amount: " + next.getValue());
				leftToDispense = next.getValue();
				current = next.getKey();
			}
			leftToDispense--;
			return new Entry(current);
		}

		@Override
		public void remove() {
			// TODO: Implement this!
			throw new UnsupportedOperationException("Can't remove items from kits yet.");
		}
	}
	
	public class Key {
		public ItemID item;
		public Map<Enchantment,Integer> ench;
		
		@SuppressWarnings("hiding")
		public Key(ItemID item, Map<Enchantment,Integer> ench) {
			this.item = item;
			this.ench = ench;
		}

		@Override
		public int hashCode() {
			return item.hashCode() + (ench == null ? 0 : ench.hashCode());
		}
	}
}