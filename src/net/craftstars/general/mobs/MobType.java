
package net.craftstars.general.mobs;

import java.util.HashMap;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public enum MobType {
	CHICKEN(null, MobAlignment.FRIENDLY, CreatureType.CHICKEN, 1, "Chicken", "Chickens", "Duck"),
	COW(null, MobAlignment.FRIENDLY, CreatureType.COW, 2, "Cow", "Cows"),
	CREEPER(CreeperState.class, MobAlignment.ENEMY, CreatureType.CREEPER, 5, "Creeper", "Creepers"),
	GHAST(null, MobAlignment.ENEMY, CreatureType.GHAST, 6, "Ghast", "Ghasts", "NetherSquid"),
	GIANT_ZOMBIE(null, MobAlignment.ENEMY, CreatureType.GIANT, 13, "Giant", "Giants", "GiantZombie", "ZombieGiant"),
	HUMAN(null, MobAlignment.ENEMY, CreatureType.MONSTER, 12, "Human", "Humans", "Monster", "Bandit"),
	PIG(PigState.class, MobAlignment.FRIENDLY, CreatureType.PIG, 0, "Pig", "Pigs"),
	PIG_ZOMBIE(PigZombieAttitude.class, MobAlignment.NEUTRAL, CreatureType.PIG_ZOMBIE, 7, "Zombie Pigman",
		"Zombie Pigmen", "PigZombie"),
	SHEEP(SheepState.class, MobAlignment.FRIENDLY, CreatureType.SHEEP, 3, "Sheep", "Sheep"),
	SKELETON(null, MobAlignment.ENEMY, CreatureType.SKELETON, 8, "Skeleton", "Skeletons", "Skeleton"),
	SLIME(SlimeSize.class, MobAlignment.ENEMY, CreatureType.SLIME, 11, "Slime", "Slimes", "GelatinousCube",
		"Goo", "Gooey"),
	SPIDER(null, MobAlignment.ENEMY, CreatureType.SPIDER, 9, "Spider", "Spiders", "Spider"),
	SQUID(null, MobAlignment.FRIENDLY, CreatureType.SQUID, 4, "Squid", "Squid", "Squid"),
	WOLF(WolfAttitude.class, MobAlignment.NEUTRAL, CreatureType.WOLF, 14, "Wolf", "Wolves", "Dog", "Dogs"),
	ZOMBIE(null, MobAlignment.ENEMY, CreatureType.ZOMBIE, 10, "Zombie", "Zombies");
	private MobAlignment alignment;
	private CreatureType ctype;
	private String[] aliases;
	private int id;
	private String singular, plural;
	private static HashMap<String, MobType> namesToEnumMapping = new HashMap<String, MobType>();
	private static HashMap<Integer, MobType> idToEnumMapping = new HashMap<Integer, MobType>();
	private Class<? extends MobData> data;
	
	private MobType(Class<? extends MobData> clz, MobAlignment align, CreatureType type, int cboxId, String title, String titles, String... names) {
		this.data = clz;
		this.alignment = align;
		this.ctype = type;
		this.aliases = names;
		this.singular = title;
		this.plural = titles;
		this.id = cboxId;
	}
	
	public LivingEntity spawn(Player byWhom, Location where) {
		if(hasPermission(byWhom)) {
			World world = byWhom.getWorld();
			return world.spawnCreature(where, ctype);
		}
		Messaging.send(byWhom, "&cYou do not have permissions to spawn " + plural + ".");
		return null;
	}
	
	public void setData(LivingEntity mob, Player setter, MobData info) {
		if(info == null) return;
		if(info.hasPermission(setter))
			info.setForMob(mob);
		else info.lacksPermission(setter);
	}
	
	public boolean hasPermission(Player byWhom) {
		if(Toolbox.hasPermission(byWhom, "general.mobspawn.all"))
			return true;
		else if(alignment.hasPermission(byWhom))
			return true;
		else {
			String x = this.toString().toLowerCase().replace("_", "-");
			return Toolbox.hasPermission(byWhom, "general.mobspawn." + x);
		}
	}
	
	static {
		for(MobType t : values()) {
			for(String name : t.aliases) {
				namesToEnumMapping.put(name.toLowerCase(), t);
			}
			idToEnumMapping.put(t.id, t);
			namesToEnumMapping.put(t.singular.toLowerCase(), t);
			namesToEnumMapping.put(t.plural.toLowerCase(), t);
		}
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
		if(info == null) try {
			info = data == null ? MobData.none : data.newInstance();
		} catch(InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String baseNode = "economy.mobspawn.";
		baseNode += toString().toLowerCase().replace('_', '-');
		return getCostClassHelper(info, baseNode);
	}

	private String getCostClassHelper(MobData info, String baseNode) {
		Configuration config = General.plugin.config;
		// Case 1: There's no entry for it, thus it costs nothing.
		if(!Toolbox.nodeExists(config, baseNode)) return "";
		// Case 2: There's an entry for it, and it's a number. Then that's the cost.
		if(config.getProperty(baseNode) instanceof Number) return baseNode;
		// Case 3: This entry has sub-entries for when the mob is riding something.
		if(Toolbox.nodeExists(config, baseNode + ".free")) return baseNode + ".free";
		// Case 4: This entry has sub-entries for various data values.
		baseNode = info.getCostNode(baseNode);
		if(!Toolbox.nodeExists(config, baseNode)) return "";
		// Case 5: There's an entry for it, and it's a number. Then that's the cost.
		if(config.getProperty(baseNode) instanceof Number) return baseNode;
		// Case 6: A combination of cases 3 and 4.
		if(Toolbox.nodeExists(config, baseNode + ".free")) return baseNode + ".free";
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
			return MobData.none;
		} catch(IllegalAccessException e) {
			return MobData.none;
		} catch(NullPointerException e) {
			return MobData.none;
		}
	}
}
