/**
 * 
 */

package net.craftstars.general.items;

import java.util.ArrayList;
import java.util.List;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

public class ItemID implements Cloneable, Comparable<ItemID> {
	private int ID;
	private int data;
	private boolean dataMatters, isValid, nameDefaulted;
	private String itemName = null, dataName = null;
	
	public ItemID() {
		this(0);
	}
	
	public ItemID(int id) {
		this(id, null);
	}
	
	public ItemID(int id, Integer d) {
		this.ID = id;
		if(d == null) {
			this.data = 0;
			this.dataMatters = false;
		} else {
			this.data = d;
			this.dataMatters = true;
		}
		this.isValid = true;
		this.nameDefaulted = true;
	}
	
	public ItemID(ItemID item) {
		this.ID = item.ID;
		this.data = item.data;
		this.dataMatters = item.dataMatters;
		this.isValid = item.isValid;
		this.itemName = item.itemName;
		this.dataName = item.dataName;
		this.nameDefaulted = item.nameDefaulted;
	}
	
	public ItemID(Material m) {
		this(m.getId());
	}
	
	public ItemID(ItemStack item) {
		this.ID = item.getTypeId();
		this.data = item.getDurability();
		this.dataMatters = true;
		this.isValid = true;
		this.itemName = Items.name(this);
		this.dataName = "";
		this.nameDefaulted = true;
	}
	
	@Override
	public String toString() {
		if(dataMatters) return Integer.toString(ID) + "/" + Integer.toString(data);
		return Integer.toString(ID);
	}
	
	public int getId() {
		return ID;
	}
	
	public Integer getData() {
		if(dataMatters) return data;
		return null;
	}
	
	public ItemID setData(Integer d) {
		if(d == null) {
			data = 0;
			dataMatters = false;
		} else {
			data = d;
			dataMatters = true;
		}
		return this;
	}
	
	public boolean isValid() {
		return isIdValid() && isDataValid();
	}
	
	public boolean isIdValid() {
		if(!isValid) return dataMatters;
		return true;
	}
	
	public boolean isDataValid() {
		if(!isValid) return !dataMatters;
		return true;
	}
	
	public ItemID invalidate(boolean dataOnly) {
		isValid = false;
		dataMatters = dataOnly;
		return this;
	}
	
	public String getName() {
		if(itemName == null) return Integer.toString(ID);
		return itemName;
	}
	
	public ItemID setName() {
		return setName(false);
	}
	
	public ItemID setName(boolean isDefault) {
		if(nameDefaulted || itemName == null) {
			itemName = Items.name(this);
			nameDefaulted = isDefault;
		}
		return this;
	}
	
	public ItemID setName(String newName) {
		return setName(newName, false);
	}
	
	public ItemID setName(String newName, boolean isDefault) {
		itemName = newName;
		nameDefaulted = isDefault;
		return this;
	}
	
	public String getVariant() {
		if(dataName == null) return Integer.toString(data);
		return dataName;
	}
	
	public ItemID setVariant(String newVar) {
		dataName = newVar;
		return this;
	}
	
	@Override
	public ItemID clone() {
		return new ItemID(this);
	}
	
	public ItemStack getStack(int amount) {
		if(!isValid) return null;
		if(dataMatters) return new ItemStack(ID, amount, (short) data);
		return new ItemStack(ID, amount);
	}
	
	public Material getMaterial() {
		return Material.getMaterial(ID);
	}
	
	@Override
	public int compareTo(ItemID other) {
		if(ID < other.ID)
			return -1;
		else if(ID > other.ID)
			return 1;
		else {
			if(dataMatters == other.dataMatters)
				return dataMatters ? new Integer(data).compareTo(other.data) : 0;
			else if(!other.dataMatters)
				return -1;
			else return 1;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ItemID) {
			ItemID other = (ItemID) obj;
			return (ID == other.ID && data == other.data && dataMatters == other.dataMatters);
		}
		return false;
	}
	
	@Override
	// Considering currently valid values, this should return a unique hash for each possible ItemID.
	public int hashCode() {
		int hash = data;
		hash |= ID << 12; // 1562, the damage data of a diamond tool, is 11 bits long
		if(!dataMatters) hash = ~hash;
		return hash;
	}
	
	public boolean canGive(CommandSender who) {
		ArrayList<String> permissions = new ArrayList<String>();
		permissions.add("general.give.any");
		String itemNode = Material.getMaterial(ID).toString();
		itemNode = "general.give.item." + itemNode.toLowerCase().replace('_', '-');
		permissions.add(itemNode);
		ConfigurationNode config = General.plugin.config.getNode("give");
		if(config == null) return true;
		List<String> groups = config.getKeys("groups");
		if(groups == null) return true;
		for(String group : groups) {
			List<Integer> items = config.getIntList("groups." + group, null);
			if(items.isEmpty()) continue;
			if(items.contains(ID)) {
				permissions.add("general.give.group." + group);
			}
		}
		boolean othersForAll = config.getBoolean("others-for-all", true);
		if(permissions.size() <= 2 && !othersForAll)
			permissions.add("general.give.groupless");
		String[] permNodes = new String[permissions.size()];
		permNodes = permissions.toArray(permNodes);
		boolean hasPermission = Toolbox.hasPermission(who, permNodes);
		if(!hasPermission && othersForAll && permissions.size() <= 2) hasPermission = true;
		if(!hasPermission) Messaging.lacksPermission(who, "give " + getName());
		return hasPermission;
	}
}
