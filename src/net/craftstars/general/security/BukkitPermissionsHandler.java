package net.craftstars.general.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.craftstars.general.General;
import net.craftstars.general.items.Kits;
import net.craftstars.general.items.Kit;
import net.craftstars.general.mobs.MobAlignment;
import net.craftstars.general.mobs.MobData;
import net.craftstars.general.mobs.MobType;
import net.craftstars.general.mobs.SlimeSize.NamedSize;
import net.craftstars.general.teleport.DestinationType;
import net.craftstars.general.teleport.TargetType;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;

public class BukkitPermissionsHandler implements PermissionsHandler {
	
	public BukkitPermissionsHandler() {
		// Here we set up the complicated container permissions
		Configuration config = General.plugin.config;
		PluginManager pm = Bukkit.getServer().getPluginManager();
		Permission perm;
		Map<String, Boolean> permsMap, permsMap2;
		/* ***** Permissions related to /give ***** */
		// general.give.group.*
		permsMap = new HashMap<String,Boolean>();
		for(String group : config.getKeys("give.groups")) {
			perm = new Permission("general.give.group." + group,
				"Gives access to the following items: " + config.getStringList("give.groups." + group, null));
			pm.addPermission(perm);
			permsMap.put("general.give.group." + group, true);
		}
		perm = new Permission("general.give.group.*", "Gives access to all item whitelist groups.", permsMap);
		pm.addPermission(perm);
		/* ***** Permissions related to /kit ***** */
		permsMap = new HashMap<String,Boolean>();
		for(Kit kit : Kits.kits.values()) {
			perm = new Permission("general.kit." + kit.getName(), "Gives access to the '" + kit.getName() + "' kit.");
			pm.addPermission(perm);
			permsMap.put("general.kit." + kit.getName(), true);
		}
		permsMap.put("general.kit", true);
		perm = new Permission("general.kit.*", "Gives access to all item whitelist groups.", permsMap);
		/* ***** Permissions related to /mobspawn ***** */
		// gemeral.mobspawn.friendly/hostile/neutral/<mob>/all
		// TODO: Get rid of the double loop here, it's not needed; just maintain multiple maps!
		permsMap2 = new HashMap<String,Boolean>();
		permsMap2.put("general.mobspawn", true);
		for(MobAlignment attitude : MobAlignment.values()) {
			permsMap2.put("general.mobspawn." + attitude.toString().toLowerCase(), true);
			permsMap = new HashMap<String,Boolean>();
			permsMap.put("general.mobspawn", true);
			for(MobType mob : MobType.values()) {
				if(mob.getAlignment() == attitude) {
					perm = new Permission("general.mobspawn." + mob.toString().toLowerCase().replace('_', '-'),
						"Gives permission to spawn " + mob.getPluralName() + ".");
					pm.addPermission(perm);
					permsMap.put("general.mobspawn." + mob.toString().toLowerCase().replace('_', '-'), true);
				}
			}
			perm = new Permission("general.mobspawn." + attitude.toString().toLowerCase(),
				"Gives permission to spawn " + attitude.toString().toLowerCase() + " mobs.", permsMap);
			pm.addPermission(perm);
		}
		perm = new Permission("general.mobspawn.all",
			"Gives permission to spawn any type of mob, but only the basic variant of each.", permsMap2);
		pm.addPermission(perm);
		// general.mobspawn.sheep.coloured.* and general.mobspawn.sheep.colored.<colour>
		permsMap = new HashMap<String,Boolean>();
		permsMap.put("general.mobspawn", true);
		for(DyeColor colour : DyeColor.values()) {
			if(colour == DyeColor.WHITE) continue;
			String colourNode = colour.toString().toLowerCase().replace('_', '-');
			perm = new Permission("general.mobspawn.sheep.coloured." + colourNode,
				"Gives permission to spawn " + colourNode + " sheep.");
			permsMap2 = new HashMap<String,Boolean>();
			permsMap2.put("general.mobspawn.sheep.coloured." + colourNode, true);
			perm = new Permission("general.mobspawn.sheep.colored." + colourNode,
				"Gives permission to spawn " + colourNode + " sheep.", permsMap2);
			pm.addPermission(perm);
			permsMap.put("general.mobspawn.sheep.coloured." + colourNode, true);
		}
		permsMap.put("general.mobspawn.sheep", true);
		perm = new Permission("general.sheep.coloured.*", "Lets you spawn any colour of sheep.", permsMap);
		pm.addPermission(perm);
		// general.mobspawn.slime.*
		permsMap = new HashMap<String,Boolean>();
		permsMap.put("general.mobspawn", true);
		for(NamedSize size : NamedSize.values())
			permsMap.put("general.mobspawn.slime." + size.toString().toLowerCase(), true);
		perm = new Permission("general.mobspawn.slime.*", "Lets you spawn any size of slime.", permsMap);
		pm.addPermission(perm);
		// general.mobspawn.variants
		permsMap = new HashMap<String,Boolean>();
		permsMap.put("general.mobspawn", true);
		for(MobType mob : MobType.values()) {
			if(mob.getNewData() == MobData.none) continue;
			String mobName = mob.toString().toLowerCase().replace('_', '-');
			perm = pm.getPermission("general.mobspawn." + mobName + ".*");
			if(perm == null) continue;
			permsMap2 = perm.getChildren();
			for(String node : permsMap2.keySet()) {
				if(node.equals("general.mobspawn." + mobName)) continue;
				permsMap.put(node, true);
			}
		}
		perm = new Permission("general.mobspawn.variants",
			"Gives access to all mob variants, but only for mobs you already have separate access to.", permsMap);
		pm.addPermission(perm);
		/* ***** Permissions related to /teleport and /setspawn ***** */
		// This setup is to avoid looping twice through the possible targets;
		// it's for destinations just afterwards
		List<String> destinationBases = new ArrayList<String>();
		destinationBases.addAll(Arrays.asList("general.teleport", "general.setspawn", "general.spawn.set"));
		// This is setup for the teleport.basic permissions
		List<String> basicDestinations = General.plugin.config.getStringList("teleport-basics", null);
		// general.teleport.any
		permsMap = new HashMap<String,Boolean>();
		permsMap.put("general.teleport", true);
		for(TargetType targ : TargetType.values()) {
			String base = "general.teleport." + targ.toString().toLowerCase();
			permsMap.put(base, true);
			permsMap2 = new HashMap<String,Boolean>();
			permsMap2.put(base, true);
			permsMap2.put(base + ".to.*", true);
			permsMap2.put(base + ".into.*", true);
			permsMap2.put(base + ".from.*", true);
			perm = new Permission(base + ".*",
				"Gives permission to teleport " + targ.getName() + " to anywhere at all.", permsMap2);
			// for general.teleport.<target>.to|into|from.*
			destinationBases.add(base);
		}
		permsMap.put("general.teleport.mass", true);
		perm = new Permission("general.teleport.any", "Gives permission to teleport anything.", permsMap);
		pm.addPermission(perm);
		// Destination-based permissions
		for(String base : destinationBases) {
			String permDesc, basePermDesc;
			if(base.contains("spawn"))
				basePermDesc = "set the spawn";
			else {
				basePermDesc = "teleport ";
				if(!base.endsWith("teleport")) {
					String target = base.substring(base.lastIndexOf('.') + 1).toUpperCase();
					basePermDesc += TargetType.valueOf(target).getName() + " ";
				}
			}
			// <base>.to.*
			permsMap = new HashMap<String,Boolean>();
			permsMap.put(base, true);
			for(DestinationType dest : DestinationType.values())
				permsMap.put(base + ".to." + dest.toString().toLowerCase(), true);
			permDesc = basePermDesc + " to";
			perm = new Permission(base + ".to.*",
				"Gives permission to " + permDesc + " any type of destination.", permsMap);
			pm.addPermission(perm);
			// <base>.into.*, <base>.from.*
			permsMap = new HashMap<String,Boolean>();
			permsMap2 = new HashMap<String,Boolean>();
			for(World world : Bukkit.getServer().getWorlds()) {
				permsMap.put(base + ".into." + world.getName(), true);
				permsMap2.put(base + ".from." + world.getName(), true);
			}
			if(base.contains("spawn"))
				permDesc = "remotely " + basePermDesc + " of";
			else permDesc = basePermDesc + " to";
			perm = new Permission(base + ".into.*", "Gives permission to " + permDesc + " any world.", permsMap);
			pm.addPermission(perm);
			if(base.contains("spawn"))
				permDesc = "remotely " + basePermDesc + " from";
			else permDesc = basePermDesc + "out of";
			perm = new Permission(base + ".from.*", "Gives permission to " + permDesc + " any world.", permsMap2);
			pm.addPermission(perm);
			// <base>.basic
			permsMap = new HashMap<String,Boolean>();
			permsMap.put("general.teleport", true);
			if(base.equals("general.teleport")) // general.teleport.basic also includes general.teleport.self
				permsMap.put("general.teleport.self", true);
			for(String dest : basicDestinations)
				permsMap.put(base + ".to." + dest.toLowerCase(), true);
			perm = new Permission(base + ".basic", "Gives basic abilities to " + basePermDesc + ".", permsMap);
			pm.addPermission(perm);
		}
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
