package net.craftstars.general.items;

import org.bukkit.GrassSpecies;

import net.craftstars.general.util.Toolbox;
import net.craftstars.general.util.range.IntRange;

public class LongGrassData extends ItemData {
	protected LongGrassData() {}
	
	@Override
	public boolean validate(int data) {
		if(new IntRange(0,2).contains(data)) return true;
		return super.validate(data);
	}
	
	@Override
	public String getName(int data) {
		return GrassSpecies.getByData((byte) data).toString();
	}
	
	@Override
	protected int parseData(String name) {
		int id = listContainsId("grass", name, new IntRange(0,2));
		if(id < 0) {
			GrassSpecies data = Toolbox.enumValue(GrassSpecies.class, name.toUpperCase());
			if(data == null) return super.parseData(name);
			else return data.getData();
		}
		return id;
	}
}
