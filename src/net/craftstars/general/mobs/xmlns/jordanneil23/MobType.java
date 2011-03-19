package net.craftstars.general.mobs.xmlns.jordanneil23;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.Toolbox;
import net.minecraft.server.EntityGhast;
import net.minecraft.server.EntityGiantZombie;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.EntitySheep;
import net.minecraft.server.EntitySlime;
import net.minecraft.server.EntityTypes;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftGhast;
import org.bukkit.craftbukkit.entity.CraftGiant;
import org.bukkit.craftbukkit.entity.CraftMonster;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;

public enum MobType {
    
    /**
     * Mob types for /spawnmob
     * @author jordanneil23
     * @author xmlns
     */
    //And edited by jordaneil23
    CHICKEN(Enemies.FRIENDLY, CreatureType.CHICKEN),
    COW(Enemies.FRIENDLY, CreatureType.COW),
    CREEPER(Enemies.ENEMY, CreatureType.CREEPER),
    GHAST(Enemies.ENEMY, CreatureType.GHAST) {
        @Override // TODO: Eliminate the need for this override.
        public LivingEntity spawn(Player byWhom, General plugin, Location loc) throws MobException {
            try {
                WorldServer world = ((CraftWorld) byWhom.getWorld()).getHandle();
                EntityGhast entity = (EntityGhast) EntityTypes.a("Ghast", world);
                CraftGhast mob = new CraftGhast((CraftServer) plugin.getServer(), entity);
                mob.teleportTo(loc);
                world.a(mob.getHandle());
                return mob;
            } catch (Exception e) {
                General.logger.error("Unable to spawn mob Ghast; internal error.");
                throw new MobException(e);
            }
        }
    },
    PIG(Enemies.FRIENDLY, CreatureType.PIG),
    PIG_ZOMBIE(Enemies.NEUTRAL, CreatureType.PIG_ZOMBIE, "ZombiePigman"),
    SKELETON(Enemies.ENEMY, CreatureType.SKELETON),
    SPIDER(Enemies.ENEMY, CreatureType.SPIDER),
    SQUID(Enemies.FRIENDLY, CreatureType.SQUID),
    ZOMBIE(Enemies.ENEMY, CreatureType.ZOMBIE),
    SHEEP(Enemies.FRIENDLY, CreatureType.SHEEP) {
        @Override
        public void setData(LivingEntity what, String data) throws MobException {
            if(data == null) return;
            if(!(what instanceof Sheep))
                throw new MobException("Tried to set sheep data on a non-sheep; please report this!");
            CraftEntity ce = (CraftEntity) what;
            EntitySheep entity = (EntitySheep) ce.getHandle();
            if(Toolbox.equalsOne(data, "sheared", "nude", "naked", "bald")) {
                entity.a(true);
            } else {
                try {
                    ItemID wool = Items.validate("35:" + data);
                    if(!wool.isValid()) throw new MobException("Invalid colour.");
                    entity.a_(wool.getData());
                } catch (Exception e) {
                    throw new MobException("Malformed colour.",e);
                }
            }
        }
    },
    SLIME(Enemies.ENEMY, CreatureType.SLIME) {
        @Override
        public void setData(LivingEntity what, String data) throws MobException {
            if(data == null) return;
            if(!(what instanceof Slime))
                throw new MobException("Tried to set slime data on a non-slime; please report this!");
            CraftEntity ce = (CraftEntity) what;
            EntitySlime entity = (EntitySlime) ce.getHandle();
            if(data.equalsIgnoreCase("tiny")) entity.e(1);
            else if(data.equalsIgnoreCase("small")) entity.e(2);
            else if(data.equalsIgnoreCase("medium")) entity.e(3);
            else if(data.equalsIgnoreCase("large")) entity.e(4);
            else if(data.equalsIgnoreCase("huge")) entity.e(8);
            else try {
                entity.e(Integer.parseInt(data));
            } catch (Exception e) {
                throw new MobException("Malformed size.",e);
            }
        }
        @Override // TODO: Eliminate the need for this override.
        public LivingEntity spawn(Player byWhom, General plugin, Location loc) throws MobException {
            try {
                WorldServer world = ((CraftWorld) byWhom.getWorld()).getHandle();
                EntitySlime entity = (EntitySlime) EntityTypes.a("Slime", world);
                CraftSlime mob = new CraftSlime((CraftServer) plugin.getServer(), entity);
                mob.teleportTo(loc);
                world.a(mob.getHandle());
                return mob;
            } catch (Exception e) {
                General.logger.error("Unable to spawn mob Slime; internal error.");
                throw new MobException(e);
            }
        }
    },
    GIANT_ZOMBIE(Enemies.ENEMY, null, "Giant") {
        @Override // TODO: Eliminate the need for this override.
        public LivingEntity spawn(Player byWhom, General plugin, Location loc) throws MobException {
            try {
                WorldServer world = ((CraftWorld) byWhom.getWorld()).getHandle();
                EntityGiantZombie entity = (EntityGiantZombie) EntityTypes.a("Giant", world);
                CraftGiant mob = new CraftGiant((CraftServer) plugin.getServer(), entity);
                mob.teleportTo(loc);
                world.a(mob.getHandle());
                return mob;
            } catch (Exception e) {
                General.logger.error("Unable to spawn mob GiantZombie; internal error.");
                throw new MobException(e);
            }
        }
    },
    MONSTER(Enemies.ENEMY, null, "Human") {
        @Override // TODO: Eliminate the need for this override.
        public LivingEntity spawn(Player byWhom, General plugin, Location loc) throws MobException {
            try {
                WorldServer world = ((CraftWorld) byWhom.getWorld()).getHandle();
                EntityMonster entity = (EntityMonster) EntityTypes.a("Monster", world);
                CraftMonster mob = new CraftMonster((CraftServer) plugin.getServer(), entity);
                mob.teleportTo(loc);
                world.a(mob.getHandle());
                return mob;
            } catch (Exception e) {
                General.logger.error("Unable to spawn mob Human; internal error.");
                throw new MobException(e);
            }
        }
    };
    
    private MobType(Enemies cat, CreatureType t) {
        this.category = cat;
        this.alt = null;
        this.type = t;
    }
    private MobType(Enemies cat, CreatureType t, String altName) {
        this.category = cat;
        this.alt = altName;
        this.type = t;
    }
    
//    private MobType(boolean b, String n, Enemies en){
//        this.s = "";
//        this.name = n;
//        this.craftClass = n;
//        this.entityClass = n;
//        this.type = en;
//    }
//    private MobType(String n, Enemies en){
//        this.name = n;
//        this.craftClass = n;
//        this.entityClass = n;
//        this.type = en;
//    }
//    private MobType(String n, String ec, Enemies en){
//        this.name = n;
//        this.craftClass = n;
//        this.entityClass = ec;
//        this.type = en;
//    }
//    private MobType(String n, String ec, String cc, Enemies en){
//        this.name = n;
//        this.entityClass = ec;
//        this.craftClass = cc;
//        this.type = en;
//    }
    
    //public String s = "s";
    //public String name;
    public Enemies category;
    //private String entityClass;
    //private String craftClass;
    private String alt;
    private CreatureType type; // if null, it's a Giant Zombie or Monster
    
    private static HashMap<String, MobType> hashMap = new HashMap<String, MobType>();
    
    static{
        for(MobType mob : MobType.values()){
            hashMap.put(mob.toString().toLowerCase(), mob);
            if(mob.alt != null) hashMap.put(mob.alt.toLowerCase(), mob);
        }
    }
    
//    @SuppressWarnings("unchecked")
//    public CraftEntity spawn(Player player, General plugin) throws MobException {
//        try {
//            WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
//            Constructor<CraftEntity> craft = (Constructor<CraftEntity>) ClassLoader.getSystemClassLoader().loadClass("org.bukkit.craftbukkit.entity.Craft" + craftClass).getConstructors()[0];
//            Constructor<Entity> entity = (Constructor<Entity>) ClassLoader.getSystemClassLoader().loadClass("net.minecraft.server.Entity" + entityClass).getConstructors()[0];
//            return craft.newInstance((CraftServer) plugin.getServer(), entity.newInstance( world ) );
//        } catch (Exception e) {
//            General.logger.error("Unable to spawn mob. Error: ");
//            e.printStackTrace();
//            throw new MobException();
//        }
//    }
    public LivingEntity spawn(Player byWhom, General plugin, Location loc) throws MobException {
        try {
            World world = byWhom.getWorld();
            LivingEntity mob = world.spawnCreature(loc, this.type);
            return mob;
        } catch(Exception e) {
            General.logger.error("Unable to spawn mob " + this.getName() + "; internal error.");
            throw new MobException(e);
        }
    }
    
    public void setData(LivingEntity what, String data) throws MobException {
        if(data == null) return;
        throw new MobException("Can't specify extra data for " + this.toString());
    }
    
    public enum Enemies{
        FRIENDLY("friendly"),
        NEUTRAL("neutral"),
        ENEMY("enemy");
        
        private Enemies(String t){
            this.type = t;
        }
        
        protected String type;
    }
    
    public class MobException extends Exception {
        public MobException(Throwable e) {
            super(e);
        }

        public MobException(String string) {
            super(string);
        }

        public MobException(String string, Exception e) {
            super(string,e);
        }

        private static final long serialVersionUID = 1L;
    }
    
    public static MobType fromName(String n){
        return hashMap.get(n.toLowerCase());
    }
    
    public String getName() {
        StringBuilder s = new StringBuilder(this.toString());
        s.setCharAt(0, Character.toUpperCase(s.charAt(0)));
        int u = s.indexOf("_");
        if(u >= 0)
            s.setCharAt(u+1, Character.toUpperCase(s.charAt(u+1)));
        return s.toString().replace("_","");
    }
}