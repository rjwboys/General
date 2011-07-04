package net.craftstars.general.security;

import java.util.HashMap;

import net.craftstars.general.General;
import net.craftstars.general.mobs.MobAlignment;
import net.craftstars.general.mobs.MobType;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;

public class BukkitPermissionsHandler implements PermissionsHandler {
	
	BukkitPermissionsHandler() {
		// Here we set up the complicated container permissions
		Configuration config = General.plugin.config;
		PluginManager pm = Bukkit.getServer().getPluginManager();
		Permission perm;
		HashMap<String,Boolean> permsMap;
		// general.give.group.*
		permsMap = new HashMap<String,Boolean>();
		for(String group : config.getKeys("give.groups"))
			permsMap.put("general.give.group." + group, true);
		perm = new Permission("general.give.group.*", permsMap);
		pm.addPermission(perm);
		// general.give.item.*
		permsMap = new HashMap<String,Boolean>();
		for(Material item : Material.values())
			permsMap.put("general.give.item." + item.toString().toLowerCase().replace('_', '-'), true);
		perm = new Permission("general.give.item.*", permsMap);
		pm.addPermission(perm);
		// gemeral.mobspawn.friendly/hostile/neutral
		for(MobAlignment alignment : MobAlignment.values()) {
			permsMap = new HashMap<String,Boolean>();
			for(MobType mob : MobType.values())
				if(mob.getAlignment() == alignment)
					permsMap.put("general.mobspawn." + mob.toString().toLowerCase().replace('_', '-'), true);
			perm = new Permission("general.mobspawn." + alignment.toString().toLowerCase(), permsMap);
			pm.addPermission(perm);
		}
		// general.mobspawn.sheep.coloured.* and general.mobspawn.sheep.colored.<colour>
		permsMap = new HashMap<String,Boolean>();
		for(DyeColor colour : DyeColor.values()) {
			if(colour == DyeColor.WHITE) continue;
			HashMap<String,Boolean> colorVersion = new HashMap<String,Boolean>();
			String colourNode = colour.toString().toLowerCase().replace('_', '-');
			colorVersion.put("general.mobspawn.sheep.coloured." + colourNode, true);
			pm.addPermission(new Permission("general.mobspawn.sheep.colored." + colourNode, colorVersion));
			permsMap.put("general.mobspawn.sheep.coloured." + colourNode, true);
		}
		perm = new Permission("general.sheep.coloured.*", permsMap);
		pm.addPermission(perm);
	}
	
	@Override
	public boolean hasPermission(Player who, String what) {
		return who.hasPermission(what);
	}
	
	@Override
	public boolean wasLoaded() {
		return true;
	}
	
	@Override
	public boolean inGroup(Player who, String which) {
		if(which == ".isop") return who.isOp();
		return false;
	}
	
	@Override
	public String getVersion() {
		return "built-in";
	}
	
	@Override
	public String getName() {
		return "Bukkit";
	}
	
}
