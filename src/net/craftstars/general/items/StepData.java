package net.craftstars.general.items;

import org.bukkit.material.Step;

import net.craftstars.general.util.range.IntRange;

public class StepData extends ItemData {
	protected StepData() {}
	
	@Override
	public boolean validate(int data) {
		if(new IntRange(0, 5).contains(data)) return true;
		return super.validate(data);
	}
	
	@Override
	public String getName(int data) {
		return new Step(data).getMaterial().toString();
	}
	
	@Override
	public int fromName(String name) {
		int id = listContainsId("step", name, new IntRange(0,5));
		if(id >= 0) return id;
		ItemID data = Items.validate(name + "/0");
		if(data == null) return super.fromName(name);
		Step step = new Step(data.getMaterial());
		if(step.getMaterial() != data.getMaterial()) return super.fromName(name);
		return step.getData();
	}
}