package net.craftstars.general.items;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.server.World;
import net.minecraft.server.WorldMapCollection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;

public class MapData extends ItemData {
	@Override
	public boolean validate(ItemID id, Material check) {
		if(id.getData() == null) id.setData(0);
		// TODO: Rewrite to use Bukkit API
		World w = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();
		String mapName = "map_" + id.getData();
		Field mapMap;
		try {
			mapMap = WorldMapCollection.class.getDeclaredField("b");
			mapMap.setAccessible(true);
			@SuppressWarnings("rawtypes")
			Object map = ((Map) mapMap.get(w.worldMaps)).get(mapName);
			if(map == null) return false;
		} catch(SecurityException e) {}
		catch(NoSuchFieldException e) {}
		catch(IllegalArgumentException e) {}
		catch(IllegalAccessException e) {}
		return true;
	}
}
