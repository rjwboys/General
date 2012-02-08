package net.craftstars.general.items;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;

public abstract class Item implements Cloneable, Comparable<Item> {
	protected int id;
	protected long data; // It's long so that MIN_VALUE doesn't clash with a valid data value
	
	public static Item find(String name) throws InvalidItemException {
		// "([a-zA-Z0-9_'-]+)"
		String[] components = name.split("[-|/:;]");
		Item item = Items.getAlias(components[0]);
		if(item == null) throw new InvalidItemException(LanguageText.GIVE_BAD_ID);
		if(item.getData() == Long.MIN_VALUE || canEnchant(item.getMaterial()))
			item.parseData(components);
		return item;
	}
	
	public void parse_Data(String[] components) {
		if(components[1].matches("[0-9]+")) {
			data = Long.parseLong(components[1]);
			if(data > Items.getMaxData(this))
				throw new InvalidItemException(LanguageText.GIVE_BAD_DATA, "data", components[1],
					"item", Items.getItemName(this));
		}
		//long data = Items.getData(this, components[0]);
		//
	}

	private static boolean canEnchant(Material material) {
		return material.getMaxDurability() > 0;
	}

	public static Item create() {
		return create(0);
	}
	
	public static Item create(int id) {
		return create(id,Long.MIN_VALUE);
	}

	public static Item create(ItemStack item) {
		return create(item.getType(), item.getDurability());
	}
	
	public static Item create(MaterialData material) {
		return create(material.getItemType(), material.getData());
	}
	
	public static Item create(String item) {
		// This assumes the string is either a number or a Material constant
		if(item.matches("[0-9]+")) return create(Integer.parseInt(item));
		else return create(Material.getMaterial(item));
	}
	
	public static Item create(Material material) {
		return create(material, Long.MIN_VALUE);
	}

	public static Item create(int id, long data) {
		Material material = Material.getMaterial(id);
		return create(material, data);
	}

	public static Item create(Material material, long data) {
		if(material == null) return null;
		if(material.getMaxDurability() > 0) return new Tool(id,data);
		else switch(material) {
		default:
			return new Simple(id,data);
		case INK_SACK:
		case WOOL:
			return new Coloured(id,data);
		case COAL:
			return new Coal(id,data);
		case SAPLING:
		case LOG:
		case LEAVES:
			return new Tree(id,data);
		case STEP:
		case DOUBLE_STEP:
			return new Step(id,data);
		case MAP:
			return new Map(id,data);
		case POTION:
			return new Potion(id,data);
		case BOOK: // BookWorm support
			return new Book(id,data);
		case SMOOTH_BRICK:
			return new StoneBrick(id,data);
		case HUGE_MUSHROOM_1:
		case HUGE_MUSHROOM_2:
			return new Mushroom(id,data);
		case LONG_GRASS:
			return new Shrub(id,data);
		case MONSTER_EGG:
			return new Egg(id,data);
		}
	}
	
	public abstract ItemStack toStack(int amount, Location loc);
	public abstract String getPermission();
	public abstract long parseData(String[] components);
	public abstract boolean matches(ItemStack other);
	public abstract String getPersistentName();
	@Override public abstract String toString();
	@Override public abstract boolean equals(Object other);
	@Override public abstract int hashCode();
	@Override public abstract Item clone();
	
	public int getId() {
		return id;
	}
	
	public Material getMaterial() {
		return Material.getMaterial(id);
	}
	
	public long getData() {
		return data;
	}
	
	public void setData(long d) {
		data = d;
	}
	
	public String getName() {
		return Items.getItemName(this);
	}
	
	public static String getName(int id) {
		return create(id).getName();
	}
	
	public static String getName(int id, long data) {
		return create(id, data).getName();
	}

	public static String getName(ItemStack item) {
		return getName(item.getTypeId(), item.getDurability());
	}

	public static String getName(MaterialData item) {
		return getName(item.getItemTypeId(), item.getData());
	}

	public static String getName(Material item) {
		return getName(item.getId());
	}
	
	public boolean canGive(CommandSender who) {
		String itemNode = "general.give.item." + getPermission();
		boolean hasPermission = who.hasPermission(itemNode);
		if(!hasPermission)
			Messaging.lacksPermission(who, itemNode, LanguageText.LACK_GIVE_ITEM, "item", getName());
		return hasPermission;
	}
}
