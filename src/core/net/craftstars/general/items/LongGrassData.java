package net.craftstars.general.items;

import org.bukkit.Material;

public class LongGrassData extends ItemData {
	@Override
	public boolean validate(ItemID id, Material check) {
		if(id.getData() == null) return true;
		if(id.getData() <= 2) return true;
		return false;
	}
}
