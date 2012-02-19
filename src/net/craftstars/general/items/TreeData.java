package net.craftstars.general.items;

import org.bukkit.TreeSpecies;

import net.craftstars.general.util.Toolbox;
import net.craftstars.general.util.range.IntRange;

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
		int id = listContainsId("tree", name, new IntRange(0,3));
		if(id < 0) {
			TreeSpecies data = Toolbox.enumValue(TreeSpecies.class, name.toUpperCase());
			if(data == null) return 0;
			else return data.getData();
		}
		return id;
	}
}