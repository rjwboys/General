package net.craftstars.general.items;

import java.util.Map;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemID implements Cloneable, Comparable<ItemID> {
	private int ID;
	private ItemData dataType;
	private int data;
	private boolean dataMatters;
	
	static ItemID bare(int id, Integer data) {
		// Returns an ItemID without its dataType set, to avoid circular dependencies
		return new ItemID(true, id, data);
	}
	
	private ItemID(@SuppressWarnings("unused") boolean dummy, int id, Integer d) {
		this.ID = id;
		if(d == null) {
			this.data = 0;
			this.dataMatters = false;
		} else {
			this.data = d;
			this.dataMatters = true;
		}
	}
	
	public ItemID() {
		this(0);
	}
	
	public ItemID(int id) {
		this(id, null);
	}
	
	public ItemID(int id, Integer d) {
		this.ID = id;
		dataType = ItemData.getData(Material.getMaterial(id));
		if(d == null) {
			this.data = 0;
			this.dataMatters = false;
		} else {
			this.data = d;
			this.dataMatters = true;
		}
	}
	
	public ItemID(Material m) {
		this(m.getId());
	}
	
	public ItemID(ItemStack item) {
		this();
		if(item != null) {
			this.ID = item.getTypeId();
			this.data = item.getDurability();
			this.dataMatters = true;
			dataType = ItemData.getData(Material.getMaterial(ID));
		}
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
		} else if(!dataType.validate(d)) {
			throw new InvalidItemException(LanguageText.GIVE_BAD_DATA, "data", getVariant(), "item", getName(null));
		} else {
			data = d;
			dataMatters = true;
		}
		return this;
	}

	private static final String[] romnum = new String[] {"O", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
	public String getName(Map<Enchantment,Integer> enchantments) {
		StringBuilder name = new StringBuilder();
		if(dataType.isNameCustom()) name.append(dataType.getDisplayName());
		else name.append(Items.name(this));
		if(enchantments == null || enchantments.isEmpty()) return name.toString();
		boolean first = true;
		for(Enchantment ench : enchantments.keySet()) {
			name.append(first ? " of " : " and ");
			first = false;
			name.append(Items.name(ench));
			name.append(' ');
			name.append(romnum[enchantments.get(ench)]);
		}
		return name.toString();
	}
	
	public ItemID setName(String newName) {
		dataType.setDisplayName(newName);
		return this;
	}
	
	public String getVariant() {
		return dataType.getName(data);
	}
	
	@Override
	public ItemID clone() {
		ItemID clone;
		try {
			clone = (ItemID)super.clone();
		} catch(CloneNotSupportedException e) {
			return null;
		}
		if(clone.dataType != null)
			clone.dataType = clone.dataType.clone();
		return clone;
	}
	
	public ItemStack getStack(int amount, Player who) {
		data = dataType.init(data, who);
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
		String itemNode = Material.getMaterial(ID).toString();
		itemNode = "general.give.item." + itemNode.toLowerCase().replace('_', '-');
		boolean hasPermission = who.hasPermission(itemNode);
		if(!hasPermission)
			Messaging.lacksPermission(who, itemNode, LanguageText.LACK_GIVE_ITEM, "item", getName(null));
		return hasPermission;
	}

	public void validateId() {
		Material check = Material.getMaterial(getId());
		if(check == null) throw new InvalidItemException(LanguageText.GIVE_BAD_ID);
	}

	public void validateData() {
		if(!dataType.validate(data))
			throw new InvalidItemException(LanguageText.GIVE_BAD_DATA, "data", getVariant(), "item", getName(null));
	}
	
	public ItemData getDataType() {
		return dataType;
	}
}
