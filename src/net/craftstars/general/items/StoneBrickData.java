package net.craftstars.general.items;

import java.util.List;

public class StoneBrickData extends ItemData {
	@Override
	public boolean validate(int data) {
		if(data <= 2) return true;
		return super.validate(data);
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
	public int fromName(String name) {
		if(name == null || name.isEmpty()) return 0;
		name = name.toLowerCase();
		for(int i = 0; i <= 2; i++) {
			List<String> list = Items.variantNames("brick", i);
			if(list == null || list.isEmpty()) continue;
			list = allToLower(list);
			if(list.contains(name)) return i;
		}
		return super.fromName(name);
	}
}
