package net.craftstars.general.items;

import org.bukkit.material.SmoothBrick;

import net.craftstars.general.General;
import net.craftstars.general.util.range.IntRange;

public class StoneBrickData extends ItemData {
	protected StoneBrickData() {}
	
	@Override
	public boolean validate(int data) {
		if(new IntRange(0,2).contains(data)) return true;
		return super.validate(data);
	}
	
	@Override
	public String getName(int data) {
		return new SmoothBrick(data).getMaterial().toString();
	}
	
	@Override
	protected int parseData(String name) {
		if(name == null || name.isEmpty() || name.matches("[0-9]+")) return super.parseData(name);
		int id = listContainsId("brick", name, new IntRange(0, 2));
		if(id >= 0) return id;
		ItemID data = Items.validate(name + "/0");
		if(data == null) return super.parseData(name);
		SmoothBrick brick = new SmoothBrick(data.getMaterial());
		General.logger.debug("Stone Brick with material " + brick.getMaterial() + " and date " + brick.getData());
		if(brick.getMaterial() != data.getMaterial()) return super.parseData(name);
		return brick.getData();
	}
}
