package net.craftstars.general.items;

import net.craftstars.general.util.Toolbox;

import org.bukkit.Material;
import org.bukkit.DyeColor;

final class ColourData extends ItemData {
	@Override
	public boolean validate(ItemID id, Material check) {
		if(id.getData() == null) return true;
		if(id.getData() > 15 || id.getData() < 0) return false;
		return true;
	}
	
	@Override
	public String getName(int data) {
		return Toolbox.formatItemName(DyeColor.getByData((byte) data).toString());
	}
	
	@Override
	public Integer fromName(String name) {
		DyeColor data = DyeColor.valueOf(name.toUpperCase());
		if(data == null) return null;
		return (int) data.getData();
	}
}