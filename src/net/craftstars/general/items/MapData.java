package net.craftstars.general.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.map.MapView;

public class MapData extends ItemData {
	@Override
	public boolean validate(ItemID id, Material check) {
		if(id.getData() == null) id.setData(0);
		MapView map = Bukkit.getMap(id.getData().shortValue());
		if(map == null) return false;
		return true;
	}
}
