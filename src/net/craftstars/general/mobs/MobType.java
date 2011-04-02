package net.craftstars.general.mobs;

import java.util.HashMap;

import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.Toolbox;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.DyeColor;

/**
 * Mob types for /spawnmob
 * @author Celtic Minstrel
 * @author jordanneil23
 * @author xmlns
 */
public enum MobType {
    //And edited by jordaneil23
    // The order of these enums constants MUST NOT BE CHANGED since the ordinal() function
    // needs to return the correct value.
    /*  0 */ PIG(Enemies.FRIENDLY, CreatureType.PIG),
    /*  1 */ CHICKEN(Enemies.FRIENDLY, CreatureType.CHICKEN),
    /*  2 */ COW(Enemies.FRIENDLY, CreatureType.COW),
    /*  3 */ SHEEP(Enemies.FRIENDLY, CreatureType.SHEEP) {
        @Override
        public void setData(LivingEntity what, String data) throws MobException {
            if(data == null) return;
            if(!(what instanceof Sheep))
                throw new MobException("Tried to set sheep data on a non-sheep; please report this!");
            Sheep entity = (Sheep) what;
            if(Toolbox.equalsOne(data, "sheared", "nude", "naked", "bald")) {
                entity.setSheared(true);
            } else {
                try {
                    ItemID wool = Items.validate("35:" + data);
                    if(wool == null || !wool.isValid()) throw new MobException("Invalid colour.");
                    DyeColor clr = DyeColor.getByData((byte)(int)wool.getData());
                    entity.setColor(clr);
                } catch (Exception e) {
                    throw new MobException("Malformed colour.",e);
                }
            }
        }
    },
    /*  4 */ SQUID(Enemies.FRIENDLY, CreatureType.SQUID),
    /*  5 */ CREEPER(Enemies.ENEMY, CreatureType.CREEPER),
    /*  6 */ GHAST(Enemies.ENEMY, CreatureType.GHAST, "NetherSquid"),
    /*  7 */ PIG_ZOMBIE(Enemies.NEUTRAL, CreatureType.PIG_ZOMBIE, "ZombiePigman"),
    /*  8 */ SKELETON(Enemies.ENEMY, CreatureType.SKELETON),
    /*  9 */ SPIDER(Enemies.ENEMY, CreatureType.SPIDER),
    /* 10 */ ZOMBIE(Enemies.ENEMY, CreatureType.ZOMBIE),
    /* 11 */ SLIME(Enemies.ENEMY, CreatureType.SLIME) {
        @Override
        public void setData(LivingEntity what, String data) throws MobException {
            if(data == null) return;
            if(!(what instanceof Slime))
                throw new MobException("Tried to set slime data on a non-slime; please report this!");
            Slime entity = (Slime) what;
            if(data.equalsIgnoreCase("tiny")) entity.setSize(1);
            else if(data.equalsIgnoreCase("small")) entity.setSize(2);
            else if(data.equalsIgnoreCase("medium")) entity.setSize(3);
            else if(data.equalsIgnoreCase("large")) entity.setSize(4);
            else if(data.equalsIgnoreCase("huge")) entity.setSize(8);
            else try {
                entity.setSize(Integer.parseInt(data));
            } catch (Exception e) {
                throw new MobException("Malformed size.",e);
            }
        }
    },
    /* 12 */ MONSTER(Enemies.ENEMY, CreatureType.MONSTER, "Human"),
    /* 13 */ GIANT_ZOMBIE(Enemies.ENEMY, CreatureType.GIANT, "Giant"),
    /* 14 */ WOLF(Enemies.NEUTRAL, CreatureType.WOLF, "Wolf");
    
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
    
    public Enemies category;
    private String alt;
    private CreatureType type;
    
    private static HashMap<String, MobType> hashMap = new HashMap<String, MobType>();
    
    static{
        for(MobType mob : MobType.values()){
            hashMap.put(mob.toString().toLowerCase(), mob);
            if(mob.alt != null) hashMap.put(mob.alt.toLowerCase(), mob);
        }
    }
    
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
        try {
            int i = Integer.valueOf(n);
            for(MobType m : hashMap.values()) {
                if(i == m.ordinal()) return m;
            }
            return null;
        } catch(NumberFormatException x) {
            return hashMap.get(n.toLowerCase());
        }
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