package net.craftstars.general.items;

import net.craftstars.general.util.Toolbox;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;

final class TreeData extends ItemData {
	@Override
	public boolean validate(ItemID id, Material check) {
		if(id.getData() == null) return true;
		if(id.getData() > 2 || id.getData() < 0) return false;
		return true;
	}
	
	@Override
	public String getName(int data) {
		return Toolbox.formatItemName(TreeSpecies.getByData((byte) data).toString());
	}
	
	@Override
	public Integer fromName(String name) {
		TreeSpecies data = TreeSpecies.valueOf(name.toUpperCase());
		if(data == null) return null;
		return (int) data.getData();
	}
}