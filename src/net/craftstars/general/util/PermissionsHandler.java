package net.craftstars.general.util;

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
import net.craftstars.general.util.Option;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class PermissionsHandler extends WorldListener {
	public PermissionsHandler() {
		// Here we set up the complicated container permissions
		for(PermissionSet set : PermissionSet.values()) set.build();
		Bukkit.getServer().getPluginManager().registerEvent(Type.WORLD_LOAD, this, Priority.Monitor, General.plugin);
		Bukkit.getServer().getPluginManager().registerEvent(Type.WORLD_UNLOAD, this, Priority.Monitor, General.plugin);
	}
	
	public boolean hasPermission(Player who, String what) {
		return who.hasPermission(what);
	}
	
	public boolean wasLoaded() {
		return true;
	}
	
	public boolean inGroup(Player who, String which) {
		return hasPermission(who, "group." + which.toLowerCase());
	}
	
	@Override
	public void onWorldLoad(WorldLoadEvent event) {
		List<String> destinationBases = new ArrayList<String>();
		destinationBases.addAll(Arrays.asList("general.teleport", "general.setspawn", "general.spawn.set"));
		destinationBases.addAll(PermissionSet.teleportBases);
		for(String base : destinationBases) {
			Permission perm = Bukkit.getServer().getPluginManager().getPermission(base + ".into.*");
			Map<String, Boolean> worlds = perm.getChildren();
			worlds.put(event.getWorld().getName(), true);
			perm.recalculatePermissibles();
		}
	}
	
	@Override
	public void onWorldUnload(WorldUnloadEvent event) {
		List<String> destinationBases = new ArrayList<String>();
		destinationBases.addAll(Arrays.asList("general.teleport", "general.setspawn", "general.spawn.set"));
		destinationBases.addAll(PermissionSet.teleportBases);
		for(String base : destinationBases) {
			Permission perm = Bukkit.getServer().getPluginManager().getPermission(base + ".into.*");
			Map<String, Boolean> worlds = perm.getChildren();
			worlds.remove(event.getWorld().getName());
			perm.recalculatePermissibles();
		}
	}

	@SuppressWarnings("unused")
	private enum PermissionSet {
		GIVE {
			@Override public void build() {
				// general.give.group.<group>, general.give.group.*
				HashMap<String, Boolean> allGroups = new HashMap<String,Boolean>();
				for(String group : General.plugin.config.getKeys("give.groups")) {
					register("general.give.group." + group,
						"Gives access to the following items: " + Option.GROUP(group).get());
					allGroups.put("general.give.group." + group, true);
				}
				register("general.give.group.*", "Gives access to all item whitelist groups.", allGroups);
			}
		},
		KIT {
			@Override public void build() {
				// general.kit.<kit>, general.kit.*
				HashMap<String, Boolean> allKits = new HashMap<String,Boolean>();
				for(Kit kit : Kits.kits.values()) {
					register("general.kit." + kit.getName(), "Gives access to the '" + kit.getName() + "' kit.");
					allKits.put("general.kit." + kit.getName(), true);
				}
				allKits.put("general.kit", true);
				register("general.kit.*", "Gives access to all kits.", allKits);
			}
		},
		MOBSPAWN {
			@Override public void build() {
				// general.mobspawn.<mob>, general.mobspawn.all, general.mobspawn.<alignment>
				HashMap<String, Boolean> allMobs = new HashMap<String,Boolean>();
				HashMap<MobAlignment,HashMap<String,Boolean>> index = new HashMap<MobAlignment,HashMap<String,Boolean>>();
				for(MobAlignment attitude : MobAlignment.values()) {
					HashMap<String, Boolean> map = new HashMap<String,Boolean>();
					map.put("general.mobspawn", true);
					index.put(attitude, map);
				}
				allMobs.put("general.mobspawn", true);
				for(MobType mob : MobType.values()) {
					allMobs.put("general.mobspawn." + mob.getAlignment().toString().toLowerCase(), true);
					register("general.mobspawn." + mob.toString().toLowerCase().replace('_', '-'),
						"Gives permission to spawn " + mob.getPluralName() + ".");
					HashMap<String,Boolean> addTo = index.get(mob.getAlignment());
					addTo.put("general.mobspawn." + mob.toString().toLowerCase().replace('_', '-'), true);
				}
				for(MobAlignment attitude : MobAlignment.values()) {
					HashMap<String,Boolean> useMap = index.get(attitude);
					register("general.mobspawn." + attitude.toString().toLowerCase(),
						"Gives permission to spawn " + attitude.toString().toLowerCase() + " mobs.", useMap);
				}
				register("general.mobspawn.all",
					"Gives permission to spawn any type of mob, but only the basic variant of each.", allMobs);
			}
		},
		SHEEP {
			@Override public void build() {
				// general.mobspawn.sheep.coloured.*, general.mobspawn.sheep.colo[u]red.<colour>
				HashMap<String, Boolean> allColours = new HashMap<String,Boolean>();
				allColours.put("general.mobspawn", true);
				for(DyeColor colour : DyeColor.values()) {
					if(colour == DyeColor.WHITE) continue;
					String colourNode = colour.toString().toLowerCase().replace('_', '-');
					register("general.mobspawn.sheep.coloured." + colourNode,
						"Gives permission to spawn " + colourNode + " sheep.");
					HashMap<String, Boolean> colorSynonym = new HashMap<String,Boolean>();
					colorSynonym.put("general.mobspawn.sheep.coloured." + colourNode, true);
					register("general.mobspawn.sheep.colored." + colourNode,
						"Gives permission to spawn " + colourNode + " sheep.", colorSynonym);
					allColours.put("general.mobspawn.sheep.coloured." + colourNode, true);
				}
				allColours.put("general.mobspawn.sheep", true);
				register("general.mobspawn.sheep.coloured.*", "Lets you spawn any colour of sheep.", allColours);
			}
		},
		SLIME {
			@Override public void build() {
				// general.mobspawn.slime.*
				HashMap<String, Boolean> allSizes = new HashMap<String,Boolean>();
				allSizes.put("general.mobspawn", true);
				for(NamedSize size : NamedSize.values())
					allSizes.put("general.mobspawn.slime." + size.toString().toLowerCase(), true);
				register("general.mobspawn.slime.*", "Lets you spawn any size of slime.", allSizes);
			}
		},
		MOBDATA {
			@Override public void build() {
				HashMap<String, Boolean> permsMap = new HashMap<String,Boolean>();
				permsMap.put("general.mobspawn", true);
				for(MobType mob : MobType.values()) {
					MobData data = mob.getNewData();
					if(data == MobData.none) continue;
					for(String dataName : data.getValues()) {
						if(dataName.equals(data.getBasic())) continue;
						permsMap.put("general.mobspawn." + dataName + ".*", true);
					}
				}
				register("general.mobspawn.variants",
					"Gives access to all mob variants, but only for mobs you already have separate access to.", permsMap);
			}
		},
		TELEPORT {
			@Override public void build() {
				teleportBases = new ArrayList<String>();
				// general.teleport.any, general.teleport.any.instant, general.teleport.<target>.*
				HashMap<String, Boolean> allTargets = new HashMap<String,Boolean>();
				allTargets.put("general.teleport", true);
				HashMap<String, Boolean> allInstants = new HashMap<String,Boolean>();
				allInstants.put("general.teleport", true);
				for(TargetType targ : TargetType.values()) {
					String base = "general.teleport." + targ.toString().toLowerCase();
					allTargets.put(base, true);
					allInstants.put(base + ".instant", true);
					HashMap<String, Boolean> allDests = new HashMap<String,Boolean>();
					allDests.put("general.teleport", true);
					allDests.put(base, true);
					allDests.put(base + ".to.*", true);
					allDests.put(base + ".into.*", true);
					allDests.put(base + ".from", true);
					register(base + ".*", "Gives permission to teleport " + targ.getName() + " to anywhere at all.",
						allDests);
					register(base + ".from", "Gives permission to teleport to from the current world to another one.");
					// for general.teleport.<target>.to|into|from.*
					teleportBases.add(base);
				}
				allTargets.put("general.teleport.mass", true);
				register("general.teleport.any", "Gives permission to teleport anything.", allTargets);
				register("general.teleport.any.instant", "Gives permission to instantly teleport anything.", allInstants);
			}
		},
		DESTINATION {
			@Override public void build() {
				// This setup is to avoid looping twice through the possible targets
				List<String> destinationBases = new ArrayList<String>();
				destinationBases.addAll(Arrays.asList("general.teleport", "general.setspawn", "general.spawn.set"));
				destinationBases.addAll(PermissionSet.teleportBases);
				// This is setup for the teleport.basic permissions
				List<String> basicDestinations = Option.TELEPORT_BASICS.get();
				// Destination-based permissions
				for(String base : destinationBases) {
					String permDesc, basePermDesc;
					boolean isTeleport = false;
					if(base.contains("spawn"))
						basePermDesc = "set the spawn";
					else {
						isTeleport = true;
						basePermDesc = "teleport ";
						if(!base.endsWith("teleport")) {
							String target = base.substring(base.lastIndexOf('.') + 1).toUpperCase();
							basePermDesc += TargetType.valueOf(target).getName() + " ";
						}
					}
					// <base>.to.*
					HashMap<String, Boolean> to = new HashMap<String,Boolean>();
					to.put(base, true);
					HashMap<String, Boolean> instants = null;
					instants = new HashMap<String,Boolean>();
					instants.put(base, true);
					for(DestinationType dest : DestinationType.values()) {
						to.put(base + ".to." + dest.toString().toLowerCase(), true);
						instants.put(base + ".to." + dest.toString().toLowerCase() + ".instant", true);
					}
					permDesc = basePermDesc + " to";
					register(base + ".to.*", "Gives permission to " + permDesc + " any type of destination.", to);
					if(isTeleport) register(base + ".to.*.instant",
						"Gives permission to instantly" + permDesc + " any type of destination.", instants);
					// <base>.into.*, <base>.from.*
					HashMap<String, Boolean> into = new HashMap<String,Boolean>();
					for(World world : Bukkit.getServer().getWorlds()) into.put(base + ".into." + world.getName(), true);
					if(base.contains("spawn"))
						permDesc = "remotely " + basePermDesc + " of";
					else permDesc = basePermDesc + " to";
					register(base + ".into.*", "Gives permission to " + permDesc + " any world.", into);
					// <base>.basic
					HashMap<String, Boolean> basics = new HashMap<String,Boolean>();
					basics.put("general.teleport", true);
					if(base.equals("general.teleport")) // general.teleport.basic also includes general.teleport.self
						basics.put("general.teleport.self", true);
					for(String dest : basicDestinations)
						basics.put(base + ".to." + dest.toLowerCase(), true);
					register(base + ".basic", "Gives basic abilities to " + basePermDesc + ".", basics);
				}
			}
		},
		;
		protected static ArrayList<String> teleportBases;
		public abstract void build();
		protected void register(String name) {
			register(name, null, null, null);
		}
		protected void register(String name, String desc) {
			register(name, desc, null, null);
		}
		protected void register(String name, PermissionDefault def) {
			register(name, null, def, null);
		}
		protected void register(String name, boolean def) {
			register(name, null, def, null);
		}
		protected void register(String name, String desc, PermissionDefault def) {
			register(name, desc, def, null);
		}
		protected void register(String name, String desc, boolean def) {
			register(name, desc, def, null);
		}
		protected void register(String name, Map<String,Boolean> children) {
			register(name, null, null, children);
		}
		protected void register(String name, String desc, Map<String,Boolean> children) {
			register(name, desc, null, children);
		}
		protected void register(String name, PermissionDefault def, Map<String,Boolean> children) {
			register(name, null, def, children);
		}
		protected void register(String name, boolean def, Map<String,Boolean> children) {
			register(name, null, def, children);
		}
		protected void register(String name, String desc, boolean def, Map<String,Boolean> children) {
			register(name, desc, def ? PermissionDefault.TRUE : PermissionDefault.FALSE, children);
		}
		protected void register(String name, String desc, PermissionDefault def, Map<String,Boolean> children) {
			Permission perm = new Permission(name, desc, def, children);
			Bukkit.getServer().getPluginManager().addPermission(perm);
		}
	}
}
