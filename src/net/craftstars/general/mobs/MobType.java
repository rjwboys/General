
package net.craftstars.general.mobs;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;
import net.minecraft.server.EntityPig;
import net.minecraft.server.EntityPigZombie;
import net.minecraft.server.EntityWolf;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPig;
import org.bukkit.craftbukkit.entity.CraftPigZombie;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.DyeColor;

public enum MobType {
	CHICKEN(MobAlignment.FRIENDLY, CreatureType.CHICKEN, 1, "Chicken", "Chickens", "Chicken", "Duck"),
	COW(MobAlignment.FRIENDLY, CreatureType.COW, 2, "Cow", "Cows", "Cow"),
	CREEPER(MobAlignment.ENEMY, CreatureType.CREEPER, 5, "Creeper", "Creepers", "Creeper") {
		@Override
		public boolean setData(LivingEntity mob, Player setter, String data) {
			if(! (mob instanceof Creeper)) return false;
			if(Toolbox.equalsOne(data, "powered", "power", "zapped", "zap", "on", "high")) {
				if(Toolbox.lacksPermission(setter, "general.mobspawn.creeper.powered", "general.mobspawn.variants"))
					return !Messaging.lacksPermission(setter, "spawn powered creepers");
				Creeper creep = (Creeper) mob;
				creep.setPowered(true);
			}
			if(Toolbox.equalsOne(data, "weak", "off", "low")) return true;
			return false;
		}
	},
	GHAST(MobAlignment.ENEMY, CreatureType.GHAST, 6, "Ghast", "Ghasts", "Ghast", "NetherSquid"),
	GIANT_ZOMBIE(MobAlignment.ENEMY, CreatureType.GIANT, 13, "Giant", "Giants", "GiantZombie", "ZombieGiant", "Giant"),
	HUMAN(MobAlignment.ENEMY, CreatureType.MONSTER, 12, "Human", "Humans", "Human", "Monster", "Bandit"),
	PIG(MobAlignment.FRIENDLY, CreatureType.PIG, 0, "Pig", "Pigs", "Pig") {
		@Override
		public boolean setData(LivingEntity mob, Player setter, String data) {
			if(! (mob instanceof Pig)) return false;
			Pig swine = (Pig) mob;
			if(Toolbox.equalsOne(data, "tame", "saddle", "saddled")) {
				if(Toolbox.lacksPermission(setter, "general.mobspawn.pig.saddled", "general.mobspawn.variants"))
					return !Messaging.lacksPermission(setter, "spawn saddled pigs");
				swine.setSaddle(true);
				return true;
			} else if(Toolbox.equalsOne(data, "wild", "unsaddled")) return true;
			return false;
		}
	},
	PIG_ZOMBIE(MobAlignment.NEUTRAL, CreatureType.PIG_ZOMBIE, 7, "Zombie Pigman", "Zombie Pigmen", "PigZombie",
			"ZombiePigman") {
		@Override
		public boolean setData(LivingEntity mob, Player setter, String data) {
			if(! (mob instanceof PigZombie)) return false;
			if(Toolbox.equalsOne(data, "calm", "passive")) return true;
			if(!Toolbox.equalsOne(data, "angry", "mad", "aggressive", "hostile")) return false;
			if(Toolbox.lacksPermission(setter, "general.mobspawn.pig-zombie.angry", "general.mobspawn.neutral.angry",
					"general.mobspawn.variants"))
				return !Messaging.lacksPermission(setter, "spawn angry zombie pigmen");
			PigZombie zom = (PigZombie) mob;
			// Begin section of accessing internal Minecraft code
			// TODO: Rewrite using only Bukkit API
			CraftPigZombie cpz = (CraftPigZombie) zom;
			EntityPigZombie epz = (EntityPigZombie) cpz.getHandle();
			try {
				Field anger = EntityPigZombie.class.getDeclaredField("angerLevel");
				anger.setAccessible(true);
				anger.setInt(epz, 400);
			} catch(SecurityException e) {}
			catch(NoSuchFieldException e) {}
			catch(IllegalArgumentException e) {}
			catch(IllegalAccessException e) {}
			// End section of accessing internal Minecraft code
			return true;
		}
	},
	SHEEP(MobAlignment.FRIENDLY, CreatureType.SHEEP, 3, "Sheep", "Sheep", "Sheep") {
		@Override
		public boolean setData(LivingEntity mob, Player setter, String data) {
			if(! (mob instanceof Sheep)) return false;
			Sheep sheep = (Sheep) mob;
			if(Toolbox.equalsOne(data, "sheared", "nude", "naked", "bald")) {
				sheep.setSheared(true);
			} else {
				if(Toolbox.lacksPermission(setter, "general.mobspawn.sheep.coloured", "general.mobspawn.sheep.colored",
						"general.mobspawn.variants"))
					return !Messaging.lacksPermission(setter, "spawn coloured sheep");
				ItemID wool = Items.validate("35:" + data);
				if(wool == null || !wool.isValid()) return false;
				DyeColor clr = DyeColor.getByData((byte) (int) wool.getData());
				sheep.setColor(clr);
			}
			return true;
		}
	},
	SKELETON(MobAlignment.ENEMY, CreatureType.SKELETON, 8, "Skeleton", "Skeletons", "Skeleton"),
	SLIME(MobAlignment.ENEMY, CreatureType.SLIME, 11, "Slime", "Slimes", "Slime", "GelatinousCube", "Goo", "Gooey") {
		@Override
		public boolean setData(LivingEntity mob, Player setter, String data) {
			if(! (mob instanceof Slime)) return false;
			Slime goo = (Slime) mob;
			try {
				int size = Integer.valueOf(data);
				goo.setSize(size);
				return true;
			} catch(NumberFormatException e) {
				SlimeSize size = SlimeSize.fromName(data);
				if(size == null) return false;
				goo.setSize(size.getSize());
				return true;
			}
		}
	},
	SPIDER(MobAlignment.ENEMY, CreatureType.SPIDER, 9, "Spider", "Spiders", "Spider"),
	SQUID(MobAlignment.FRIENDLY, CreatureType.SQUID, 4, "Squid", "Squid", "Squid"),
	WOLF(MobAlignment.NEUTRAL, CreatureType.WOLF, 14, "Wolf", "Wolves", "Wolf", "Dog") {
		@Override
		public boolean setData(LivingEntity mob, Player setter, String data) {
			if(! (mob instanceof Wolf)) return false;
			if(Toolbox.equalsOne(data, "wild", "calm", "passive")) return true;
			Wolf dog = (Wolf) mob;
			if(Toolbox.equalsOne(data, "angry", "mad", "hostile", "aggressive")) {
				if(Toolbox.lacksPermission(setter, "general.mobspawn.wolf.angry", "general.mobspawn.neutral.angry",
						"general.mobspawn.variants"))
					return !Messaging.lacksPermission(setter, "spawn angry wolves");
				dog.setAngry(true);
			} else if(Toolbox.equalsOne(data, "tame", "pet")) {
				if(Toolbox.lacksPermission(setter, "general.mobspawn.wolf.tamed", "general.mobspawn.variants"))
					return !Messaging.lacksPermission(setter, "spawn tamed wolves");
				String owner = setter.getName();
				dog.setTamed(true);
				dog.setOwner(Bukkit.getServer().getPlayer(owner));
			} else {
				if(Toolbox.lacksPermission(setter, "general.mobspawn.wolf.tamed", "general.mobspawn.variants"))
					return !Messaging.lacksPermission(setter, "spawn tamed wolves");
				dog.setTamed(true);
				dog.setOwner(Bukkit.getServer().getPlayer(data));
			}
			// End section of accessing internal Minecraft code
			return true;
		}
	},
	ZOMBIE(MobAlignment.ENEMY, CreatureType.ZOMBIE, 10, "Zombie", "Zombies", "Zombie");
	private MobAlignment alignment;
	private CreatureType ctype;
	private String[] aliases;
	private int id;
	private String singular, plural;
	private static HashMap<String, MobType> namesToEnumMapping = new HashMap<String, MobType>();
	private static HashMap<Integer, MobType> idToEnumMapping = new HashMap<Integer, MobType>();
	
	private MobType(MobAlignment align, CreatureType type, int cboxId, String title, String titles, String... names) {
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
	
	public boolean setData(LivingEntity mob, Player setter, String data) {
		return false;
	}
	
	public boolean hasPermission(Player byWhom) {
		if(byWhom.isOp())
			return true;
		else if(Toolbox.hasPermission(byWhom, "general.mobspawn.all"))
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
}
