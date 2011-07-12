package net.craftstars.general.items;

import org.bukkit.Material;

public class ToolDamage extends ItemData {
	@Override
	public boolean validate(ItemID id, Material check) {
		if(id.getData() != null && id.getData() != 0) {
			boolean isInvalid = true;
			if(isDamageable(id.getId())) isInvalid = false;
			if(check.getData() != null) isInvalid = false;
			if(isInvalid)
				return false;
			// TODO: Get rid of hack
			// --- begin hacky workaround for incorrect getMaxDurability
			else if(id.getId() == 359)
				return id.getData() <= 238;
			else if(id.getId() == 346)
				return id.getData() < 64; 
			// --- begin hacky workaround for incorrect getMaxDurability
			else if(id.getData() > check.getMaxDurability()) return false;
		}
		return true;
	}
	
	public static boolean isDamageable(int id) {
		// TODO: Get rid of hack
		// --- begin hacky workaround for incorrect getMaxDurability
		if(id == 359) return true;
		// --- end hacky workaround for incorrect getMaxDurability
		return Material.getMaterial(id).getMaxDurability() != -1;
	}
}