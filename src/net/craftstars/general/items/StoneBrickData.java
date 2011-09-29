package net.craftstars.general.items;

import org.bukkit.Material;

public class StoneBrickData extends ItemData {
	@Override
	public boolean validate(ItemID id, Material check) {
		if(id.getData() == null) return true;
		if(id.getData() <= 2) return true;
		return false;
	}
	
	@Override
	public String getName(int data) {
		switch(data) {
		case 0: return "normal";
		case 1: return "mossy";
		case 2: return "crumbling";
		}
		return null;
	}
	
	@Override
	public Integer fromName(String name) {
		if(name == null || name.isEmpty()) return null;
		else if(name.equalsIgnoreCase("mossy")) return 1;
		else if(name.equalsIgnoreCase("crumbling")) return 2;
		else return 0;
	}
}
