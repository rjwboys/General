package net.craftstars.general.items;

import org.bukkit.Material;

public abstract class ItemData {
	public final static ItemData DAMAGE = new ToolDamage();
	public final static ItemData COLOUR = new ColourData();
	public final static ItemData TREE = new TreeData();
	public final static ItemData COAL = new CoalData();
	public final static ItemData STEP = new StepData();
	public final static ItemData MAP = new MapData();
	public final static ItemData SPAWNER = new CreatureBoxData();
	public final static ItemData BOOK = new BookWormData();
	public abstract boolean validate(ItemID id, Material check);
	
	public static ItemData getData(Material data) {
		switch(data) {
		default:
			return DAMAGE;
		case INK_SACK:
		case WOOL:
			return COLOUR;
		case COAL:
			return COAL;
		case SAPLING:
		case LOG:
		case LEAVES:
			return TREE;
		case STEP:
		case DOUBLE_STEP:
			return STEP;
		case MOB_SPAWNER: // CreatureBox support
			return SPAWNER;
		case MAP:
			return MAP;
		case BOOK: // BookWorm support
			return BOOK;
		}
	}

	public String getName(int data) {
		return Integer.toString(data);
	}
	
	public Integer fromName(String name) {
		try {
			return Integer.valueOf(name);
		} catch(NumberFormatException e) {
			return null;
		}
	}
}
