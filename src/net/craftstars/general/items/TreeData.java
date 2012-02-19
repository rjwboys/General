package net.craftstars.general.items;

import org.bukkit.TreeSpecies;

final public class TreeData extends ItemData {
	@Override
	public boolean validate(int data) {
		if(data > 2 || data < 0) return false;
		return super.validate(data);
	}
	
	@Override
	public String getName(int data) {
		return TreeSpecies.getByData((byte) data).toString();
	}
	
	@Override
	public int fromName(String name) {
		TreeSpecies data = TreeSpecies.valueOf(name.toUpperCase());
		if(data == null) return 0;
		return data.getData();
	}
}