
package net.craftstars.general.mobs;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.craftstars.general.General;
import net.craftstars.general.option.Option;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Toolbox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;

public enum MobType {
	BLAZE(NoData.class, MobAlignment.ENEMY, EntityType.BLAZE, 61),
	CAVE_SPIDER(NoData.class, MobAlignment.ENEMY, EntityType.CAVE_SPIDER, 59),
	CHICKEN(AnimalData.class, MobAlignment.FRIENDLY, EntityType.CHICKEN, 93),
	COW(AnimalData.class, MobAlignment.FRIENDLY, EntityType.COW, 92),
	CREEPER(CreeperState.class, MobAlignment.ENEMY, EntityType.CREEPER, 50),
	ENDER_DRAGON(NoData.class, MobAlignment.ENEMY, EntityType.ENDER_DRAGON, 63),
	ENDERMAN(EnderBlock.class, MobAlignment.NEUTRAL, EntityType.ENDERMAN, 58),
	GHAST(NoData.class, MobAlignment.ENEMY, EntityType.GHAST, 56),
	GIANT_ZOMBIE(NoData.class, MobAlignment.ENEMY, EntityType.GIANT, 53),
	MAGMA_CUBE(SlimeSize.class, MobAlignment.ENEMY, EntityType.MAGMA_CUBE, 62),
	MUSHROOM_COW(AnimalData.class, MobAlignment.FRIENDLY, EntityType.MUSHROOM_COW, 96),
	PIG(PigState.class, MobAlignment.FRIENDLY, EntityType.PIG, 90),
	PIG_ZOMBIE(PigZombieAttitude.class, MobAlignment.NEUTRAL, EntityType.PIG_ZOMBIE, 57),
	SHEEP(SheepState.class, MobAlignment.FRIENDLY, EntityType.SHEEP, 91),
	SILVERFISH(NoData.class, MobAlignment.ENEMY, EntityType.SILVERFISH, 60),
	SKELETON(NoData.class, MobAlignment.ENEMY, EntityType.SKELETON, 51),
	SLIME(SlimeSize.class, MobAlignment.ENEMY, EntityType.SLIME, 55),
	SNOWMAN(NoData.class, MobAlignment.FRIENDLY, EntityType.SNOWMAN, 97),
	SPIDER(NoData.class, MobAlignment.ENEMY, EntityType.SPIDER, 52),
	SQUID(NoData.class, MobAlignment.FRIENDLY, EntityType.SQUID, 94),
	VILLAGER(VillagerRole.class, MobAlignment.FRIENDLY, EntityType.VILLAGER, 120),
	WOLF(WolfAttitude.class, MobAlignment.NEUTRAL, EntityType.WOLF, 95),
	ZOMBIE(NoData.class, MobAlignment.ENEMY, EntityType.ZOMBIE, 54);
	private MobAlignment alignment;
	private EntityType ctype;
	private String[] aliases;
	private int id;
	private String singular, plural;
	private static HashMap<String, MobType> namesToEnumMapping = new HashMap<String, MobType>();
	private static HashMap<Integer, MobType> idToEnumMapping = new HashMap<Integer, MobType>();
	private static EnumMap<EntityType, MobType> ctToEnumMapping = new EnumMap<EntityType, MobType>(EntityType.class);
	private Class<? extends MobData> data;
	private static FileConfiguration yml;
	private static File configFile;
	
	public static void setup() {
		try {
			configFile = new File(General.plugin.getDataFolder(), "mobs.yml");
			if(!configFile.exists()) General.createDefaultConfig(configFile);
			yml = new YamlConfiguration();
			yml.load(configFile);
		} catch(Exception ex) {
			General.logger.warn(LanguageText.LOG_CONFIG_ERROR.value("file", "mobs.yml"), ex);
		}
		idToEnumMapping.clear();
		namesToEnumMapping.clear();
		for(MobType mob : values()) {
			namesToEnumMapping.put(mob.name().toLowerCase(), mob);
			List<?> names = yml.getList("mobs.mob" + mob.id);
			if(names == null) continue;
			boolean gotBase = false;
			ArrayList<String> aliases = new ArrayList<String>();
			for(Object name : names) {
				String singular, plural;
				if(name instanceof String) singular = plural = (String) name;
				else if(name instanceof Map) {
					Map<?,?> map = (Map<?,?>) name;
					if(map.size() != 1) {
						warnMalformed(mob.id, name);
						continue;
					}
					Object[] keys = map.keySet().toArray(), values = map.values().toArray();
					if(keys[0] instanceof String) singular = (String) keys[0];
					else {
						warnMalformed(mob.id, name);
						continue;
					}
					if(values[0] instanceof String) plural = (String) values[0];
					else {
						warnMalformed(mob.id, name);
						continue;
					}
				} else {
					warnMalformed(mob.id, name);
					continue;
				}
				aliases.add(singular);
				// This should work since they're both assigned from name casted to String
				if(singular != plural) aliases.add(plural);
				if(!gotBase) {
					gotBase = true;
					mob.singular = Toolbox.formatItemName(singular);
					mob.plural = Toolbox.formatItemName(plural);
				}
				if(mob.data != null) {
					Method setup;
					try {
						setup = mob.data.getMethod("setup");
						setup.invoke(null);
					} catch(SecurityException e) {}
					catch(NoSuchMethodException e) {}
					catch(IllegalArgumentException e) {}
					catch(IllegalAccessException e) {}
					catch(InvocationTargetException e) {}
				}
			}
			mob.aliases = aliases.toArray(new String[0]);
			for(String name : mob.aliases) {
				namesToEnumMapping.put(name.toLowerCase(), mob);
			}
			idToEnumMapping.put(mob.id, mob);
			ctToEnumMapping.put(mob.ctype, mob);
			namesToEnumMapping.put(mob.singular.toLowerCase(), mob);
			namesToEnumMapping.put(mob.plural.toLowerCase(), mob);
		}
	}

	private static void warnMalformed(int id, Object mob) {
		General.logger.warn(LanguageText.LOG_MOB_BAD.value("mob", id, "name", mob.toString()));
	}
	
	private MobType(Class<? extends MobData> clz, MobAlignment align, EntityType type, int cboxId) {
		this.data = clz;
		this.alignment = align;
		this.ctype = type;
		this.id = cboxId;
	}
	
	public LivingEntity spawn(CommandSender sender, Location where, MobData withData) {
		if(withData.hasPermission(sender)) {
			World world = where.getWorld();
			LivingEntity entity = world.spawnCreature(where, ctype);
			withData.setForMob(entity);
			return entity;
		}
		withData.lacksPermission(sender);
		Messaging.lacksPermission(sender, getPermission(), LanguageText.LACK_MOBSPAWN_MOB,
			"mob", singular, "mobs", plural);
		return null;
	}

	public String getPermission() {
		return "general.mobspawn." + this.toString().toLowerCase().replace("_", "-");
	}
	
	public static MobType byName(String name) {
		return namesToEnumMapping.get(name.toLowerCase());
	}
	
	public static MobType byId(int id) {
		return idToEnumMapping.get(id);
	}
	
	public static MobType fromBukkitType(EntityType type) {
		return ctToEnumMapping.get(type);
	}
	
	public static MobType fromEntity(LivingEntity entity) {
		return fromBukkitType(entity.getType());
	}
	
	public static MobType getMob(String string) {
		try {
			int id = Integer.valueOf(string);
			return byId(id);
		} catch(NumberFormatException e) {
			return byName(string);
		}
	}
	
	public String getCostClass(MobData info) {
		if(info == null) info = getNewData();
		String baseNode = "economy.mobspawn.";
		baseNode += toString().toLowerCase().replace('_', '-');
		return getCostClassHelper(info, baseNode);
	}

	private String getCostClassHelper(MobData info, String baseNode) {
		// Case 1: There's no entry for it, thus it costs nothing.
		if(!Option.nodeExists(baseNode)) return "";
		// Case 2: There's an entry for it, and it's a number. Then that's the cost.
		if(Option.getProperty(baseNode) instanceof Number) return baseNode;
		// Case 3: This entry has sub-entries for when the mob is riding something.
		if(Option.nodeExists(baseNode + ".free")) return baseNode + ".free";
		// Case 4: This entry has sub-entries for various data values.
		baseNode = info.getCostNode(baseNode);
		if(!Option.nodeExists(baseNode)) return "";
		// Case 5: There's an entry for it, and it's a number. Then that's the cost.
		if(Option.getProperty(baseNode) instanceof Number) return baseNode;
		// Case 6: A combination of cases 3 and 4.
		if(Option.nodeExists(baseNode + ".free")) return baseNode + ".free";
		// Case 7: There's no entry for it after all, thus it costs nothing.
		return "";
	}
	
	public String getMountedCostClass(String baseNode, MobData info) {
		// The baseNode refers to the rider, and this is called on the mount
		if(baseNode.contains(".free"))
			baseNode = baseNode.replace(".free", ".riding.");
		else baseNode += ".riding.";
		baseNode += toString().toLowerCase().replace('_', '-');
		return getCostClassHelper(info, baseNode);
	}
	
	public String getName() {
		return singular;
	}
	
	public String getPluralName() {
		return plural;
	}
	
	public MobData getNewData() {
		try {
			return data.newInstance();
		} catch(InstantiationException e) {
			try {
				return data.getConstructor(MobType.class).newInstance(this);
			} catch(IllegalArgumentException x) {
				return new NoData(this);
			} catch(SecurityException x) {
				return new NoData(this);
			} catch(InstantiationException x) {
				return new NoData(this);
			} catch(IllegalAccessException x) {
				return new NoData(this);
			} catch(InvocationTargetException x) {
				return new NoData(this);
			} catch(NoSuchMethodException x) {
				return new NoData(this);
			}
		} catch(IllegalAccessException e) {
			return new NoData(this);
		} catch(NullPointerException e) {
			return new NoData(this);
		}
	}

	public int getId() {
		return id;
	}
	
	public MobAlignment getAlignment() {
		return alignment;
	}
	
	public String[] getDataList(String key) {
		if(key.isEmpty()) return getDataList("mob" + id, key);
		else return getDataList("mob" + id + "." + key, key);
	}
	
	public static String[] getDataList(String key, String dflt) {
		String node = "data." + key;
		List<String> list = yml.getStringList(node);
		if(list == null) return new String[]{dflt};
		return list.toArray(new String[0]);
	}

	public static List<MobType> byAlignment(MobAlignment align) {
		List<MobType> list = new ArrayList<MobType>();
		for(MobType mob : values()) {
			if(mob.getAlignment() == align) list.add(mob);
		}
		return list;
	}
}
