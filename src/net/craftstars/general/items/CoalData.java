package net.craftstars.general.items;

import org.bukkit.CoalType;

import net.craftstars.general.util.Toolbox;
import net.craftstars.general.util.range.IntRange;

final public class CoalData extends ItemData {
	protected CoalData() {}
	
	@Override
	public boolean validate(int data) {
		if(data == 0 || data == 1) return true;
		return super.validate(data);
	}
	
	@Override
	public String getName(int data) {
		CoalType coal = CoalType.getByData((byte) data);
		if(coal == null) return super.getName(data);
		return coal.toString();
	}
	
	@Override
	protected int parseData(String name) {
		int id = listContainsId("coal", name, new IntRange(0, 1));
		if(id >= 0) return id;
		CoalType data = Toolbox.enumValue(CoalType.class, name.toUpperCase());
		if(data == null) return super.parseData(name);
		return data.getData();
	}
}