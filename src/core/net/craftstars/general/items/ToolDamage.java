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
			else if(id.getData() > check.getMaxDurability()) return false;
		}
		return true;
	}
	
	public static boolean isDamageable(int id) {
		return Material.getMaterial(id).getMaxDurability() != -1;
	}
}