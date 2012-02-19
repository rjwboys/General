package net.craftstars.general.items;

import org.bukkit.CoalType;

final public class CoalData extends ItemData {
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
	public int fromName(String name) {
		CoalType data = CoalType.valueOf(name.toUpperCase());
		if(data == null) return super.fromName(name);
		return data.getData();
	}
}