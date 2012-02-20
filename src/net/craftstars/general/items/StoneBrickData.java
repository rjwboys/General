package net.craftstars.general.items;

import org.bukkit.material.SmoothBrick;

import net.craftstars.general.util.range.IntRange;

public class StoneBrickData extends ItemData {
	protected StoneBrickData() {}
	
	@Override
	public boolean validate(int data) {
		if(data <= 2) return true;
		return super.validate(data);
	}
	
	@Override
	public String getName(int data) {
		return new SmoothBrick(data).getMaterial().toString();
	}
	
	@Override
	public int fromName(String name) {
		if(name == null || name.isEmpty()) return 0;
		int id = listContainsId("brick", name, new IntRange(0, 2));
		if(id >= 0) return id;
		ItemID data = Items.validate(name + "/0");
		if(data == null) return 0;
		SmoothBrick brick = new SmoothBrick(data.getMaterial());
		if(brick.getMaterial() != data.getMaterial()) return super.fromName(name);
		return brick.getData();
	}
}
