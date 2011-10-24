package net.craftstars.general.items;

import org.bukkit.Material;

public abstract class ItemData {
	public final static ItemData DAMAGE = new ToolDamage();
	public final static ItemData COLOUR = new ColourData();
	public final static ItemData TREE = new TreeData();
	public final static ItemData COAL = new CoalData();
	public final static ItemData STEP = new StepData();
	public final static ItemData MAP = new MapData();
	public final static ItemData STONE_BRICK = new StoneBrickData();
	public final static ItemData SPAWNER = new MobSpawnerData();
	public final static ItemData BOOK = new BookWormData();
	public final static ItemData SHRUB = new LongGrassData();
	public final static ItemData MUSHROOM = new BigShroomData();
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
		case MOB_SPAWNER:
			return SPAWNER;
		case MAP:
			return MAP;
		case BOOK: // BookWorm support
			return BOOK;
		case SMOOTH_BRICK:
			return STONE_BRICK;
		case HUGE_MUSHROOM_1:
		case HUGE_MUSHROOM_2:
			return MUSHROOM;
		case LONG_GRASS:
			return SHRUB;
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
