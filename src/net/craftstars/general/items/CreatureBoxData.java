package net.craftstars.general.items;

import net.craftstars.general.mobs.MobType;
import net.craftstars.general.util.Toolbox;

import org.bukkit.Bukkit;
import org.bukkit.Material;

public class CreatureBoxData extends ItemData {
	
	@Override
	public boolean validate(ItemID id, Material check) {
		if(id.getData() == null) return true;
		if(Bukkit.getServer().getPluginManager().getPlugin("creaturebox") == null) {
			ItemID tmp = id.clone();
			tmp.setData(null);
			id.setName(Items.name(tmp));
			return id.getData() == 0;
		}
		if(id.getData() > 14 || id.getData() < 0) return false;
		return true;
	}
	
	@Override
	public String getName(int data) {
		return Toolbox.formatItemName(MobType.byId(data).toString());
	}
	
	@Override
	public Integer fromName(String name) {
		MobType data = MobType.byName(name);
		if(data == null) return null;
		return data.getId();
	}
	
}
