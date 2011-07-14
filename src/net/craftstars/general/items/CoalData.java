package net.craftstars.general.items;

import org.bukkit.CoalType;
import org.bukkit.Material;

final class CoalData extends ItemData {
	@Override
	public boolean validate(ItemID id, Material check) {
		if(id.getData() == null) return true;
		if(id.getData() > 1 || id.getData() < 0) return false;
		return true;
	}
	
	@Override
	public String getName(int data) {
		return CoalType.getByData((byte) data).toString();
	}
	
	@Override
	public Integer fromName(String name) {
		CoalType data = CoalType.valueOf(name.toUpperCase());
		if(data == null) return null;
		return (int) data.getData();
	}
}