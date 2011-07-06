package net.craftstars.general.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.craftstars.general.General;
import net.craftstars.general.mobs.MobAlignment;
import net.craftstars.general.mobs.MobData;
import net.craftstars.general.mobs.MobType;
import net.craftstars.general.mobs.SlimeSize.NamedSize;
import net.craftstars.general.teleport.DestinationType;
import net.craftstars.general.teleport.TargetType;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
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
		Map<String, Boolean> permsMap, permsMap2;
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
		// gemeral.mobspawn.friendly/hostile/neutral, general.mobspawn.all
		perm = pm.getPermission("general.mobspawn.all");
		permsMap2 = perm.getChildren();
		for(MobAlignment attitude : MobAlignment.values()) {
			permsMap2.put("general.mobspawn." + attitude.toString().toLowerCase(), true);
			permsMap = new HashMap<String,Boolean>();
			for(MobType mob : MobType.values())
				if(mob.getAlignment() == attitude)
					permsMap.put("general.mobspawn." + mob.toString().toLowerCase().replace('_', '-'), true);
			perm = new Permission("general.mobspawn." + attitude.toString().toLowerCase(), permsMap);
			pm.addPermission(perm);
		}
		// general.mobspawn.sheep.coloured.* and general.mobspawn.sheep.colored.<colour>
		permsMap = new HashMap<String,Boolean>();
		for(DyeColor colour : DyeColor.values()) {
			if(colour == DyeColor.WHITE) continue;
			permsMap2 = new HashMap<String,Boolean>();
			String colourNode = colour.toString().toLowerCase().replace('_', '-');
			permsMap2.put("general.mobspawn.sheep.coloured." + colourNode, true);
			pm.addPermission(new Permission("general.mobspawn.sheep.colored." + colourNode, permsMap2));
			permsMap.put("general.mobspawn.sheep.coloured." + colourNode, true);
		}
		perm = new Permission("general.sheep.coloured.*", permsMap);
		pm.addPermission(perm);
		// general.mobspawn.variants
		permsMap = new HashMap<String,Boolean>();
		for(MobType mob : MobType.values())
			if(mob.getNewData() != MobData.none)
				permsMap.put("general.mobspawn." + mob.toString().toLowerCase().replace('_', '-') + ".*", true);
		perm = new Permission("general.mobspawn.variants", permsMap);
		pm.addPermission(perm);
		// general.mobspawn.slime.*
		perm = pm.getPermission("general.mobspawn.slime.*");
		permsMap = perm.getChildren();
		for(NamedSize size : NamedSize.values())
			permsMap.put("general.mobspawn.slime." + size.toString().toLowerCase(), true);
		// This setup is to avoid looping twice through the possible targets;
		// it's for destinations just afterwards
		int i = 0, n = 3;
		String[] basicDestBases = new String[] {"general.teleport", "general.setspawn", "general.spawn.set"};
		String[] destinationBases = new String[TargetType.values().length + basicDestBases.length];
		// This is setup for the teleport.basic permissions
		List<String> basicDestinations = General.plugin.config.getStringList("teleport-basics", null);
		// general.teleport.any
		perm = pm.getPermission("general.teleport.any");
		permsMap = perm.getChildren();
		for(TargetType targ : TargetType.values()) {
			String base = "general.teleport." + targ.toString().toLowerCase();
			permsMap.put(base, true);
			// for general.teleport.<target>.to|into|from.*
			destinationBases[i] = base;
			i++;
		}
		// Destination-based permissions
		System.arraycopy(basicDestBases, 0, destinationBases, i, basicDestBases.length);
		for(String base : destinationBases) {
			// general.teleport.to.*
			permsMap = new HashMap<String,Boolean>();
			for(DestinationType dest : DestinationType.values())
				permsMap.put(base + ".to." + dest.toString().toLowerCase(), true);
			perm = new Permission(base + ".to.*", permsMap);
			pm.addPermission(perm);
			// general.teleport.into.*, general.teleport.from.*
			permsMap = new HashMap<String,Boolean>();
			permsMap2 = new HashMap<String,Boolean>();
			for(World world : Bukkit.getServer().getWorlds()) {
				permsMap.put(base + ".into." + world.getName(), true);
				permsMap2.put(base + ".from." + world.getName(), true);
			}
			perm = new Permission(base + ".into.*", permsMap);
			pm.addPermission(perm);
			perm = new Permission(base + ".from.*", permsMap2);
			pm.addPermission(perm);
			// general.teleport.basic
			permsMap = new HashMap<String,Boolean>();
			for(String dest : basicDestinations)
				permsMap.put(base + ".to." + dest.toLowerCase(), true);
			perm = new Permission(base + ".basic", permsMap);
			pm.addPermission(perm);
		}
		// general.teleport.basic also includes general.teleport.self
		perm = pm.getPermission("general.teleport.basic");
		permsMap = perm.getChildren();
		permsMap.put("general.teleport.self", true);
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
