
package net.craftstars.general.mobs;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.craftstars.general.General;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.config.Configuration;

public enum MobType {
	CAVE_SPIDER(null, MobAlignment.ENEMY, CreatureType.CAVE_SPIDER, 59),
	CHICKEN(null, MobAlignment.FRIENDLY, CreatureType.CHICKEN, 93),
	COW(null, MobAlignment.FRIENDLY, CreatureType.COW, 92),
	CREEPER(CreeperState.class, MobAlignment.ENEMY, CreatureType.CREEPER, 50),
	ENDERMAN(EnderBlock.class, MobAlignment.NEUTRAL, CreatureType.ENDERMAN, 58),
	GHAST(null, MobAlignment.ENEMY, CreatureType.GHAST, 56),
	GIANT_ZOMBIE(null, MobAlignment.ENEMY, CreatureType.GIANT, 53),
	PIG(PigState.class, MobAlignment.FRIENDLY, CreatureType.PIG, 90),
	PIG_ZOMBIE(PigZombieAttitude.class, MobAlignment.NEUTRAL, CreatureType.PIG_ZOMBIE, 57),
	SHEEP(SheepState.class, MobAlignment.FRIENDLY, CreatureType.SHEEP, 91),
	SILVERFISH(null, MobAlignment.ENEMY, CreatureType.SILVERFISH, 60),
	SKELETON(null, MobAlignment.ENEMY, CreatureType.SKELETON, 51),
	SLIME(SlimeSize.class, MobAlignment.ENEMY, CreatureType.SLIME, 55),
	SPIDER(null, MobAlignment.ENEMY, CreatureType.SPIDER, 52),
	SQUID(null, MobAlignment.FRIENDLY, CreatureType.SQUID, 94),
	WOLF(WolfAttitude.class, MobAlignment.NEUTRAL, CreatureType.WOLF, 95),
	ZOMBIE(null, MobAlignment.ENEMY, CreatureType.ZOMBIE, 54);
	private MobAlignment alignment;
	private CreatureType ctype;
	private String[] aliases;
	private int id;
	private String singular, plural;
	private static HashMap<String, MobType> namesToEnumMapping = new HashMap<String, MobType>();
	private static HashMap<Integer, MobType> idToEnumMapping = new HashMap<Integer, MobType>();
	private Class<? extends MobData> data;
	private static Configuration yml;
	
	public static void setup() {
		try {
			File dataFolder = General.plugin.getDataFolder();
			if(!dataFolder.exists()) dataFolder.mkdirs();
			File configFile = new File(dataFolder, "mobs.yml");
			
			if(!configFile.exists()) General.plugin.createDefaultConfig(configFile);
			yml = new Configuration(configFile);
			yml.load();
		} catch(Exception ex) {
			General.logger.warn(LanguageText.LOG_CONFIG_ERROR.value("file", "mobs.yml"), ex);
		}
		idToEnumMapping.clear();
		namesToEnumMapping.clear();
		for(MobType mob : values()) {
			@SuppressWarnings("unchecked")
			List<Object> names = (List<Object>) yml.getProperty("mobs.mob" + mob.id);
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
			namesToEnumMapping.put(mob.singular.toLowerCase(), mob);
			namesToEnumMapping.put(mob.plural.toLowerCase(), mob);
		}
	}

	private static void warnMalformed(int id, Object mob) {
		General.logger.warn(LanguageText.LOG_MOB_BAD.value("mob", id, "name", mob.toString()));
	}
	
	private MobType(Class<? extends MobData> clz, MobAlignment align, CreatureType type, int cboxId) {
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
		if(General.config.getProperty(baseNode) instanceof Number) return baseNode;
		// Case 3: This entry has sub-entries for when the mob is riding something.
		if(Option.nodeExists(baseNode + ".free")) return baseNode + ".free";
		// Case 4: This entry has sub-entries for various data values.
		baseNode = info.getCostNode(baseNode);
		if(!Option.nodeExists(baseNode)) return "";
		// Case 5: There's an entry for it, and it's a number. Then that's the cost.
		if(General.config.getProperty(baseNode) instanceof Number) return baseNode;
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
			return new NoData(this);
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
		String node = "data.mob" + id;
		if(!key.isEmpty()) node += "." + key;
		return yml.getStringList(node, null).toArray(new String[0]);
	}
	
	public static List<MobType> byAlignment(MobAlignment align) {
		List<MobType> list = new ArrayList<MobType>();
		for(MobType mob : values()) {
			if(mob.getAlignment() == align) list.add(mob);
		}
		return list;
	}
}
