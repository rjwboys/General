package net.craftstars.general.items;

import org.bukkit.entity.CreatureType;

import net.craftstars.general.mobs.MobType;

public class EggData extends ItemData {
	protected EggData() {}
	
	@Override
	public boolean validate(int data) {
		MobType mob = MobType.byId(data);
		if(mob != null) return true;
		CreatureType creature = CreatureType.fromId(data);
		if(creature != null) return true;
		return super.validate(data);
	}

	@Override
	public String getName(int data) {
		MobType mob = MobType.byId(data);
		if(mob != null) return mob.getName();
		return super.getName(data);
	}

	@Override
	protected int parseData(String data) {
		MobType mob = MobType.byName(data);
		if(mob != null) return mob.getId();
		CreatureType creature = CreatureType.fromName(data);
		if(creature != null) return MobType.fromBukkitType(creature).getId();
		return super.parseData(data);
	}
}
