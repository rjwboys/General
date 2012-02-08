package net.craftstars.general.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;

public final class Items {
	private static Configuration config;
	private static TreeMap<String, Item> aliases;
	private static TreeMap<Item, String> names;
	private Items() {}

	public static void save() {
		// TODO Auto-generated method stub
		
	}

	public static void setup() {
		// TODO Auto-generated method stub
		
	}
	
	public static void addAlias(String name, Item id) {
		aliases.put(name, id);
	}
	
	public static void removeAlias(String name) {
		aliases.remove(name);
	}
	
	public static Item getAlias(String name) {
		for(String x : aliases.keySet()) {
			if(x.equalsIgnoreCase(name)) return aliases.get(x);
		}
		return Item.create(name);
	}
	
	public static long getData(Item id, String name) {
		if(id.getData() != Long.MIN_VALUE) return Long.MIN_VALUE+1;
		ConfigurationSection variants = config.getConfigurationSection("variants.item" + id.getId());
		if(variants == null) return Long.MIN_VALUE;
		return variants.getLong(name,Long.MIN_VALUE);
	}
	
	public static List<String> getVariantNames(Item id) {
		if(id != null && id.getData() != -1) {
			List<String> list = config.getStringList("variants.item" + id.getId() + ".type" + id.getData());
			return list == null ? new ArrayList<String>() : list;
		}
		return null;
	}
	
	public static void addVariantName(Item id, String name) {
		if(id != null && id.getData() != -1) {
			List<String> variants = getVariantNames(id);
			variants.add(name);
			setVariantNames(id, variants);
		}
	}
	
	public static void removeVariantName(Item id, String name) {
		if(id != null && id.getData() != -1) {
			List<String> variants = getVariantNames(id);
			variants.remove(name);
			setVariantNames(id, variants);
		}
	}
	
	public static void setVariantNames(Item id, List<String> variants) {
		config.set("variants.item" + id.getId() + ".type" + id.getData(), variants);
	}

	public static void setGroupItems(String groupName, List<String> groupItems) {
		ArrayList<Integer> items = new ArrayList<Integer>();
		for(String item : groupItems) {
			try {
				Item thisItem = Item.find(item);
				items.add(thisItem.getId());
			} catch(InvalidItemException e) {}
		}
		Option.GROUP(groupName).set(items);
	}

	public static List<Integer> getGroupItems(String groupName) {
		return Option.GROUP(groupName).get();
	}

	public static void addGroupItem(String groupName, String item) {
		List<Integer> group = getGroupItems(groupName);
		try {
			Item thisItem = Item.find(item);
			group.add(thisItem.getId());
		} catch(InvalidItemException e) {}
		Option.GROUP(groupName).set(group);
	}

	public static void removeGroupItem(String groupName, String item) {
		List<Integer> group = getGroupItems(groupName);
		try {
			Item thisItem = Item.find(item);
			group.remove(thisItem.getId());
		} catch(InvalidItemException e) {}
		Option.GROUP(groupName).set(group);
	}
	
	public static String getItemName(Item longKey) {
		if(names.containsKey(longKey)) {
			return names.get(longKey);
		}
		
		// This is a redundant lookup if longKey already has no data, but that shouldn't create significant
		// overhead
		Item shortKey = Item.create(longKey.getId());
		if(names.containsKey(shortKey)) {
			return names.get(shortKey);
		}

		//Material material = Material.getMaterial(longKey.getId());
		//ItemData data = ItemData.getData(material);
		//String dataName = data.getName(longKey.getData());
		//if(!data.equals("0")) return Toolbox.formatItemName(dataName);
		
		for(Material item : Material.values()) {
			if(item.getId() == longKey.getId()) {
				return Toolbox.formatItemName(item.toString());
			}
		}
		
		return longKey.toString();
	}
	
	public static void setItemName(Item id, String name) {
		names.put(id, name);
	}

	public static long getMaxData(Item item) {
		return config.getLong("max.item" + item.getId(), 0);
	}
	
	public static void giveItem(Player who, Item item, int amount) {
		if(amount == 0) amount = Material.getMaterial(item.getId()).getMaxStackSize();
		PlayerInventory inv = who.getInventory();
		HashMap<Integer, ItemStack> excess = inv.addItem(item.toStack(amount, who.getLocation()));
		for(ItemStack leftover : excess.values())
			who.getWorld().dropItemNaturally(who.getLocation(), leftover);
	}
}
