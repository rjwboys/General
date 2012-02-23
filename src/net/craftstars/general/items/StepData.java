package net.craftstars.general.items;

import org.bukkit.material.Step;

import net.craftstars.general.General;
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
	protected int parseData(String name) {
		if(name == null || name.isEmpty() || name.matches("[0-9]+")) return super.parseData(name);
		int id = listContainsId("step", name, new IntRange(0,5));
		if(id >= 0) return id;
		ItemID data = Items.validate(name + "/0");
		if(data == null) return super.parseData(name);
		Step step = new Step(data.getMaterial());
		General.logger.debug("Step with material " + step.getMaterial() + " and date " + step.getData());
		if(step.getMaterial() != data.getMaterial()) return super.parseData(name);
		return step.getData();
	}
}