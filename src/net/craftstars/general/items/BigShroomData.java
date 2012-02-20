package net.craftstars.general.items;

import java.util.Arrays;

import org.apache.commons.lang.math.IntRange;

public class BigShroomData extends ItemData {
	protected BigShroomData() {}
	
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
		name = name.toLowerCase();
		// First check stem/interior
		if(listContains("shroom.stem", name, Arrays.asList("stem"))) return 10;
		if(listContains("shroom.interior", name, Arrays.asList("west"))) return 0;
		String prefix;
		// Next check top, since it's invalid without it
		int data = 0;
		prefix = listContainsPrefix("shroom.top", name, Arrays.asList("top"));
		if(!prefix.isEmpty()) {
			data = 5;
			name = name.replace(prefix, "");
		}
		if(data != 5) return super.fromName(name);
		// Then check north/south
		prefix = listContainsPrefix("shroom.north", name, Arrays.asList("north"));
		if(!prefix.isEmpty()) {
			data -= 3;
			name = name.replace(prefix, "");
		}
		prefix = listContainsPrefix("shroom.south", name, Arrays.asList("south"));
		if(!prefix.isEmpty()) {
			data += 3;
			name = name.replace(prefix, "");
		}
		// And finally east/west
		prefix = listContainsPrefix("shroom.west", name, Arrays.asList("west"));
		if(!prefix.isEmpty()) {
			data--;
			name = name.replace(prefix, "");
		}
		prefix = listContainsPrefix("shroom.east", name, Arrays.asList("east"));
		if(!prefix.isEmpty()) {
			data++;
			name = name.replace(prefix, "");
		}
		return data;
	}
}
