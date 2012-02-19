package net.craftstars.general.items;

import org.bukkit.Material;
import org.bukkit.DyeColor;

import net.craftstars.general.util.range.IntRange;

final public class ColourData extends ItemData {
	@Override
	public boolean validate(int data) {
		if(new IntRange(0,15).contains(data)) return true;
		return super.validate(data);
	}
	
	@Override
	public String getName(int data) {
		byte data2 = (byte) (material == Material.INK_SACK ? 15 - data : data);
		return DyeColor.getByData(data2).toString();
	}
	
	@Override
	public int fromName(String name) {
		DyeColor data = DyeColor.valueOf(name.toUpperCase());
		if(data == null) return 0;
		return material == Material.INK_SACK ? 15 - data.getData() : data.getData();
	}
}