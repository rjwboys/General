package net.craftstars.general.items;

import org.bukkit.Material;
import org.bukkit.material.Step;

final class StepData extends ItemData {
	@Override
	public boolean validate(ItemID id, Material check) {
		if(id.getData() == null) return true;
		if(id.getData() > 5 || id.getData() < 0) return false;
		return true;
	}
	
	@Override
	public String getName(int data) {
		return new Step(data).getMaterial().toString();
	}
	
	@Override
	public Integer fromName(String name) {
		ItemID data = Items.validate(name + "/0");
		if(data == null) return null;
		// TODO: Hacky workaround for missing support
		if(data.getMaterial() == Material.BRICK) return 4;
		if(data.getMaterial() == Material.SMOOTH_BRICK) return 5;
		// End hacky workaround
		Step step = new Step(data.getMaterial());
		if(step.getMaterial() != data.getMaterial()) return null;
		return (int) step.getData();
	}
}