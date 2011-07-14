package net.craftstars.general.items;

import org.bukkit.Material;
import org.bukkit.material.Step;

final class StepData extends ItemData {
	@Override
	public boolean validate(ItemID id, Material check) {
		if(id.getData() == null) return true;
		if(id.getData() > 3 || id.getData() < 0) return false;
		return true;
	}
	
	@Override
	public String getName(int data) {
		return new Step(data).getMaterial().toString();
	}
	
	@Override
	public Integer fromName(String name) {
		Material data = Material.getMaterial(name.toUpperCase());
		if(data == null) return null;
		Step step = new Step(data);
		if(step.getMaterial() != data) return null;
		return (int) step.getData();
	}
}