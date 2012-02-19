package net.craftstars.general.items;

import org.bukkit.GrassSpecies;

public class LongGrassData extends ItemData {
	@Override
	public boolean validate(int data) {
		if(data <= 2) return true;
		return super.validate(data);
	}
	
	@Override
	public String getName(int data) {
		return GrassSpecies.getByData((byte) data).toString();
	}
	
	@Override
	public int fromName(String name) {
		GrassSpecies data = GrassSpecies.valueOf(name.toUpperCase());
		if(data == null) return 0;
		return data.getData();
	}
}
