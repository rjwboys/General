package net.craftstars.general.items;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import net.craftstars.general.util.range.IntRange;

public class MapData extends ItemData {
	@Override
	public boolean validate(int data) {
		if(new IntRange(90000,90005).contains(data)) return true; // new maps
		MapView map = Bukkit.getMap((short)data);
		return map != null;
	}
	
	@Override
	public int fromName(String name) {
		if(name.startsWith("z")) {
			name = name.substring(1);
			return super.fromName(name) + 90000;
		}
		return super.fromName(name);
	}
	
	@Override
	public String getName(int data) {
		if(data > 90000) return "z" + (data - 90000);
		return super.getName(data);
	}
	
	@Override
	public int init(int data, Player who) {
		if(data < 90000) return super.init(data, who);
		// Create a new map with the given zoom scale!
		MapView map = Bukkit.createMap(who.getWorld());
		map.setCenterX(who.getLocation().getBlockX());
		map.setCenterZ(who.getLocation().getBlockZ());
		switch(data) {
		case 90001: map.setScale(Scale.CLOSEST); break;
		case 90002: map.setScale(Scale.CLOSE); break;
		case 90003: map.setScale(Scale.NORMAL); break;
		case 90004: map.setScale(Scale.FAR); break;
		case 90005: map.setScale(Scale.FARTHEST); break;
		}
		return map.getId();
	}
}
