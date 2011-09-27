package net.craftstars.general.items;

import org.bukkit.Material;

import net.craftstars.general.mobs.MobType;

public class MobSpawnerData extends ItemData {
	@Override
	public boolean validate(ItemID id, Material check) {
		MobType mob = MobType.byId(id.getData());
		return mob != null;
	}
}
