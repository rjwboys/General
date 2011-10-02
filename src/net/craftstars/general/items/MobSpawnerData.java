package net.craftstars.general.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import net.craftstars.general.mobs.MobType;

public class MobSpawnerData extends ItemData {
	private static List<Integer> nonmobs = Arrays.asList(2,9,10,11,20,40,41);
	@Override
	public boolean validate(ItemID id, Material check) {
		MobType mob = MobType.byId(id.getData());
		if(mob != null) return true;
		return nonmobs.contains(id.getData());
	}
}
