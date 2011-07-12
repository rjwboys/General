package net.craftstars.general.items;

import org.bukkit.Material;

public abstract class ItemData {
	public final static ItemData DAMAGE = new ToolDamage();
	public final static ItemData COLOUR = new ItemData() {
		@Override
		public boolean validate(ItemID id, Material check) {
			if(id.getData() == null) return true;
			if(id.getData() > 15 || id.getData() < 0) return false;
			return true;
		}
	};
	public final static ItemData TREE = new ItemData() {
		@Override
		public boolean validate(ItemID id, Material check) {
			if(id.getData() == null) return true;
			if(id.getData() > 2 || id.getData() < 0) return false;
			return true;
		}
	};
	public final static ItemData COAL = new ItemData() {
		@Override
		public boolean validate(ItemID id, Material check) {
			if(id.getData() == null) return true;
			if(id.getData() > 1 || id.getData() < 0) return false;
			return true;
		}
	};
	public final static ItemData STEP = new ItemData() {
		@Override
		public boolean validate(ItemID id, Material check) {
			// TODO Auto-generated method stub
			return false;
		}
	};
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
}
