package net.craftstars.general.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.craftstars.general.General;
import net.craftstars.general.items.Kits;
import net.craftstars.general.items.Kit;
import net.craftstars.general.mobs.MobAlignment;
import net.craftstars.general.mobs.MobData;
import net.craftstars.general.mobs.MobType;
import net.craftstars.general.teleport.DestinationType;
import net.craftstars.general.teleport.TargetType;
import net.craftstars.general.util.Option;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

public class PermissionManager extends WorldListener {
	private static PermissionManager me = new PermissionManager();
	private PermissionManager() {}
	
	public static void setup() {
		// Here we set up the complicated container permissions
		PermissionSet.init();
		for(PermissionSet set : PermissionSet.values()) set.build();
		PermissionSet.finish();
		Bukkit.getPluginManager().registerEvent(Type.WORLD_LOAD, me, Priority.Monitor, General.plugin);
		Bukkit.getPluginManager().registerEvent(Type.WORLD_UNLOAD, me, Priority.Monitor, General.plugin);
	}
	
	public static void refreshItemGroups() {
		Permission perm = Bukkit.getPluginManager().getPermission("general.give.groupless");
		perm.setDefault(Option.OTHERS4ALL.get() ? PermissionDefault.TRUE : PermissionDefault.FALSE);
	}
	
	@Override
	public void onWorldLoad(WorldLoadEvent event) {
		// TODO: Is there a more graceful way?
		nukeTeleportPermissions();
		PermissionSet.TARGET_DEST.build();
	}
	
	@Override
	public void onWorldUnload(WorldUnloadEvent event) {
		// TODO: There is a more graceful way. Do it.
		nukeTeleportPermissions();
		PermissionSet.TARGET_DEST.build();
	}

	private void nukeTeleportPermissions() {
		PluginManager pm = Bukkit.getPluginManager();
		Set<Permission> perms = pm.getPermissions();
		for(Permission p : perms) {
			if(p.getName().startsWith("general.teleport") || p.getName().startsWith("general.setspawn"))
				pm.removePermission(p);
		}
	}

	@SuppressWarnings("unused")
	private enum PermissionSet {
		GIVE {
			@Override public void build() {
				// general.give.group.<group>
				Map<String,Map<String,Boolean>> groups = new HashMap<String,Map<String,Boolean>>();
				// general.give.groupless
				Map<String,Boolean> groupless = new HashMap<String,Boolean>();
				// Two nested loops; first Material, then groups
				List<String> groupNames = Option.ITEM_GROUPS.get();
				for(Material material : Material.values()) {
					String itemName = material.toString().toLowerCase().replace('_', '-');
					String itemPerm = "general.give.item." + itemName;
					// general.give.item.<item-name>
					register(itemPerm, "Gives permission to give " + itemName);
					boolean gotGroup = false;
					for(String group : groupNames) {
						List<Integer> groupItems = Option.GROUP(group).get();
						if(groupItems.contains(material.getId())) {
							gotGroup = true;
							if(!groups.containsKey(group)) groups.put(group, new HashMap<String,Boolean>());
							groups.get(group).put(itemPerm, true);
						}
					}
					if(!gotGroup) {
						groupless.put(itemPerm, true);
					}
				}
				// general.give.groups
				Map<String,Boolean> allGroups = new HashMap<String,Boolean>();
				for(String group : groups.keySet()) {
					String permission = "general.give.group." + group;
					register(permission, "Gives permission to give items in the " + group +
						" group.", groups.get(group));
					allGroups.put(permission, true);
				}
				register("general.give.groupless", "Gives permission to give items not assigned to a group.",
					Option.OTHERS4ALL.get(), groupless);
				register("general.give.groups", "Gives permission to give items from any item group.", allGroups);
			}
		},
		KIT {
			@Override public void build() {
				// general.kit.<kit>, general.kit.*
				Map<String, Boolean> allKits = new HashMap<String,Boolean>();
				Map<String, Boolean> instants = new HashMap<String,Boolean>();
				for(Kit kit : Kits.all()) {
					String perm = "general.kit." + kit.getName();
					register(perm, "Gives access to the '" + kit.getName() + "' kit.");
					allKits.put(perm, true);
					Map<String,Boolean> instant = new HashMap<String,Boolean>();
					String instantPerm = perm + ".instant";
					instant.put(instantPerm, true);
					register(instantPerm, "Gives instant access to the '" + kit.getName() + "' kit.", instant);
					instants.put(instantPerm, true);
				}
				instants.put("general.kit", true);
				register("general.kit", "Gives access to all kits.", allKits);
				register("general.kit-now", "Gives instant access to all kits.", instants);
			}
		},
		MOBSPAWN {
			@Override public void build() {
				// Three nested loops: alignment, mob, data
				// general.mobspawn
				Map<String,Boolean> all = new HashMap<String,Boolean>();
				// general.mobspawn.basic
				Map<String,Boolean> basics = new HashMap<String,Boolean>();
				for(MobAlignment align : MobAlignment.values()) {
					// general.mobspawn.<alignment>
					Map<String,Boolean> alignments = new HashMap<String,Boolean>();
					// general.mobspawn.<alignment>.basic
					Map<String,Boolean> alignBasics = new HashMap<String,Boolean>();
					for(MobType mob : MobType.byAlignment(align)) {
						// general.mobspawn.<mob>
						Map<String,Boolean> variants = new HashMap<String,Boolean>();
						MobData basicData = mob.getNewData();
						for(String data : basicData.getValues()) {
							// general.mobspawn.<mob>.<data>
							String perm = mob.getPermission() + "." + data;
							register(perm, "Gives permission to spawn " + mob.getPluralName() + " of the " + data +
								" variant.");
							variants.put(perm, true);
							all.put(perm, true);
							if(data.equals(basicData.getBasic())) {
								alignBasics.put(perm, true);
								basics.put(perm, true);
							}
						}
						register(mob.getPermission(), "Gives permission to spawn any type of " + mob.getName(), variants);
						alignments.put(mob.getPermission(), true);
					}
					String alignName = align.toString().toLowerCase();
					String alignPerm = "general.mobspawn." + alignName;
					register(alignPerm, "Gives permission to spawn any type of" + alignName + " mobs.", alignments);
					register(alignPerm + ".basic", "Gives permission to spawn the basic type of any " + alignName +
						" mobs.", alignBasics);
				}
				register("general.mobspawn.basic", "Gives permission to spawn the basic type of any mob.", basics);
				register("general.mobspawn", "Gives permission to spawn any type of mob.", all);
			}
		},
		TELEPORT_BASIC {
			@Override
			public void build() {
				Map<String, Boolean> basics = new HashMap<String, Boolean>();
				for(String node : Option.TELEPORT_BASICS.get())
					basics.put("general.teleport.self.to." + node, true);
				register("general.teleport.basic","Gives basic teleport permissions.",basics);
			}
		},
		TARGET_DEST {
			class Base implements Cloneable {
				private String[] bases = {"general.teleport.?","general.teleport.?.instant","general.setspawn.?"};
				private String[] descrs = {"teleport", "instantly teleport", "set the spawn of"};
				public Base subst(String replacement) {
					Base clone = clone();
					String replace = replacement.isEmpty() ? ".?" : "?";
					for(int i = 0; i < clone.bases.length; i++)
						clone.bases[i] = clone.bases[i].replace(replace, replacement);
					return clone;
				}
				@Override
				public Base clone() {
					Base clone = new Base();
					clone.bases = bases.clone();
					clone.descrs = descrs.clone();
					return clone;
				}
				public Base append(String string) {
					Base clone = clone();
					for(int i = 0; i < clone.bases.length; i++)
						clone.descrs[i] += string;
					return clone;
				}
				public void register() {
					for(int i = 0; i < bases.length; i++)
						PermissionSet.TARGET_DEST.register(bases[i], "Gives permission to " + descrs[i]);
				}
				public void register(Set<Base> children) {
					for(int i = 0; i < bases.length; i++) {
						Map<String, Boolean> map = new HashMap<String, Boolean>();
						for(Base child : children) map.put(child.bases[i], true);
						if(i == 1) map.put(bases[0], true);
						PermissionSet.TARGET_DEST.register(bases[i], "Gives permission to " + descrs[i], map);
					}
				}
			}
			@Override public void build() {
				// Three nested loops; first targets, then worlds, and finally destinations.
				Base base = new Base();
				Set<Base> allChildren = set();
				Map<DestinationType, Set<Base>> destinationsChildren = this.<Set<Base>>destmap();
				Map<World, Set<Base>> worldsChildren = new HashMap<World,Set<Base>>();
				Map<DestinationType, Map<World, Set<Base>>> worldsDestChildren = this.<Map<World,Set<Base>>>destmap();
				for(TargetType targ : TargetType.values()) {
					Base targBase = base.subst(targ.toString().toLowerCase() + ".?");
					targBase = targBase.append(" " + targ.getName());
					Set<Base> targetsChildren = set();
					Map<DestinationType, Set<Base>> targetsDestChildren = this.<Set<Base>>destmap();
					for(World world : Bukkit.getWorlds()) {
						Base worldBase = targBase.subst("into." + world.getName() + ".?");
						worldBase = worldBase.append(" into " + world.getName());
						Set<Base> targetsWorldsChildren = set();
						for(DestinationType dest : DestinationType.values()) {
							// general.<cmd>.<target>.into.<world>.to.<dest>
							Base destBase = worldBase.subst("to." + dest.toString().toLowerCase());
							destBase = destBase.append(" at " + dest.getName(false));
							destBase.register();
							// Other permissions
							targetsWorldsChildren.add(destBase);
							if(!targetsDestChildren.containsKey(dest))
								targetsDestChildren.put(dest, set());
							targetsDestChildren.get(dest).add(destBase);
							if(!destinationsChildren.containsKey(dest))
								destinationsChildren.put(dest, set());
							destinationsChildren.get(dest).add(destBase);
							if(!worldsDestChildren.containsKey(dest))
								worldsDestChildren.put(dest, new HashMap<World, Set<Base>>());
							if(!worldsDestChildren.get(dest).containsKey(world))
								worldsDestChildren.get(dest).put(world, set());
							worldsDestChildren.get(dest).get(world).add(destBase);
						}
						// general.<cmd>.<target>.into.<world>
						Base targetWorldBase = worldBase.subst("");
						targetWorldBase.register(targetsWorldsChildren);
						// Other permissions
						targetsChildren.add(targetWorldBase);
						if(!worldsChildren.containsKey(world))
							worldsChildren.put(world, set());
						worldsChildren.get(world).add(targetWorldBase);
						
					}
					// general.<cmd>.<target>
					Base targetsBase = targBase.subst("");
					targetsBase.register(targetsChildren);
					// general.<cmd>.<target>.to.<dest>
					for(Entry<DestinationType,Set<Base>> entry : targetsDestChildren.entrySet()) {
						Base targetsDestBase = targBase.subst("to." + entry.getKey().toString().toLowerCase());
						targetsDestBase.register(entry.getValue());
					}
					// Other permissions
					allChildren.add(targetsBase);
					
				}
				// general.<cmd>
				Base allBase = base.subst("");
				allBase.register(allChildren);
				// general.<cmd>.to.<dest>
				for(Entry<DestinationType,Set<Base>> entry : destinationsChildren.entrySet()) {
					Base destinationsBase = base.subst("to." + entry.getKey().toString().toLowerCase());
					destinationsBase.register(entry.getValue());
				}
				// general.<cmd>.into.<world>
				for(Entry<World,Set<Base>> entry : worldsChildren.entrySet()) {
					Base worldsBase = base.subst("into." + entry.getKey().getName().toLowerCase());
					worldsBase.register(entry.getValue());
				}
				// general.<cmd>.into.<world>.to.<dest>
				for(Entry<DestinationType,Map<World,Set<Base>>> entry : worldsDestChildren.entrySet()) {
					for(Entry<World,Set<Base>> subentry : entry.getValue().entrySet()) {
						Base worldsDestBase = base.subst("into." + subentry.getKey().getName().toLowerCase() + ".?");
						worldsDestBase = worldsDestBase.subst("to." + entry.getKey().toString().toLowerCase());
						worldsDestBase.register(subentry.getValue());
					}
				}
			}
			private Set<Base> set() {
				return new HashSet<Base>();
			}
			private <V> EnumMap<DestinationType,V> destmap() {
				return new EnumMap<DestinationType,V>(DestinationType.class);
			}
		},
		MOTD {
			class Filter implements FilenameFilter {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".motd");
				}
			}
			Filter filter = new Filter();
			@Override
			public void build() {
				File dir = General.plugin.getDataFolder();
				for(String filename : dir.list(filter)) {
					if(filename.equals("general.motd")) continue;
					filename = filename.replace(".motd", "");
					register("general.motd." + filename, "Shows the " + filename + " MOTD instead of the general " +
						"one when you join.", PermissionDefault.FALSE);
				}
			}
		},
		;
		public abstract void build();
		private static PrintWriter file = null;
		
		public static void init() {
			if(!Option.EXPORT_PERMISSIONS.get()) return;
			try {
				file = new PrintWriter(new File(General.plugin.getDataFolder(), "allpermissions.txt"));
			} catch(FileNotFoundException e) {
				file = null;
			}
		}
		public static void finish() {
			if(file != null) {
				List<Permission> ymlPerms = new ArrayList<Permission>(General.plugin.getDescription().getPermissions());
				Collections.sort(ymlPerms, new PermissionsCompare());
				for(Permission perm : ymlPerms) file.println(perm.getName());
				file.close();
			}
		}
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
			// Welcome to Rome!
			Permission perm = new Permission(name, desc, def, children);
			Bukkit.getPluginManager().addPermission(perm);
			if(file != null) file.println(name);
		}
	}
	private static class PermissionsCompare implements Comparator<Permission> {
		@Override
		public int compare(Permission lhs, Permission rhs) {
			return lhs.getName().compareTo(rhs.getName());
		}
	}
}
