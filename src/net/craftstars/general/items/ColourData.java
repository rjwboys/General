package net.craftstars.general.items;

import org.bukkit.Material;
import org.bukkit.DyeColor;

import net.craftstars.general.util.range.IntRange;

final public class ColourData extends ItemData {
	protected ColourData() {}
	
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
		int id = listContainsId("colour", name, new IntRange(0,15));
		if(id < 0) {
			DyeColor data = DyeColor.valueOf(name.toUpperCase());
			if(data == null) id = 0;
			else id = data.getData();
		}
		return material == Material.INK_SACK ? 15 - id : id;
	}
}