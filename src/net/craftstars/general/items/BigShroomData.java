package net.craftstars.general.items;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.math.IntRange;

public class BigShroomData extends ItemData {
	@Override
	public boolean validate(int data) {
		if(new IntRange(0, 10).containsDouble(data)) return true;
		return super.validate(data);
	}

	@Override
	public String getName(int data) {
		if(data == 10) return "stem";
		else if(data == 0) return "interior";
		StringBuilder name = new StringBuilder("top");
		if(data < 4) name.append("north");
		else if(data > 6) name.append("south");
		data %= 3;
		if(data == 1) name.append("west");
		else if(data == 0) name.append("east");
		return name.toString();
	}

	@Override
	public int fromName(String name) {
		List<String> list;
		name = name.toLowerCase();
		// First check stem/interior
		list = Items.variantNames("shroom.stem");
		if(list == null || list.isEmpty()) list = Arrays.asList("stem");
		list = allToLower(list);
		if(list.contains(name)) return 10;
		list = Items.variantNames("shroom.interior");
		if(list == null || list.isEmpty()) list = Arrays.asList("west");
		list = allToLower(list);
		if(list.contains(name)) return 0;
		// Next check top, since it's invalid without it
		list = Items.variantNames("shroom.top");
		if(list == null || list.isEmpty()) list = Arrays.asList("top");
		list = allToLower(list);
		int data = 0;
		for(String prefix : list) {
			if(name.startsWith(prefix)) {
				data = 5;
				name = name.replace(prefix, "");
				break;
			}
		}
		if(data != 5) return super.fromName(name);
		// Then check north/south
		list = Items.variantNames("shroom.north");
		if(list == null || list.isEmpty()) list = Arrays.asList("north");
		list = allToLower(list);
		for(String prefix : list) {
			if(name.startsWith(prefix)) {
				data -= 3;
				name = name.replace(prefix, "");
				break;
			}
		}
		list = Items.variantNames("shroom.south");
		if(list == null || list.isEmpty()) list = Arrays.asList("south");
		list = allToLower(list);
		for(String prefix : list) {
			if(name.startsWith(prefix)) {
				data += 3;
				name = name.replace(prefix, "");
				break;
			}
		}
		// And finally east/west
		list = Items.variantNames("shroom.west");
		if(list == null || list.isEmpty()) list = Arrays.asList("west");
		list = allToLower(list);
		for(String prefix : list) {
			if(name.startsWith(prefix)) {
				data--;
				name = name.replace(prefix, "");
				break;
			}
		}
		list = Items.variantNames("shroom.east");
		if(list == null || list.isEmpty()) list = Arrays.asList("east");
		list = allToLower(list);
		for(String prefix : list) {
			if(name.startsWith(prefix)) {
				data++;
				name = name.replace(prefix, "");
				break;
			}
		}
		return data;
	}
}
