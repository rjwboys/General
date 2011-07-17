package net.craftstars.general.mobs;

import java.lang.reflect.Field;

import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;
import net.minecraft.server.EntityPigZombie;

import org.bukkit.craftbukkit.entity.CraftPigZombie;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;

public class PigZombieAttitude extends MobData {
	private int anger = 0;
	private final static String[] values = new String[] {"regular","angry"};
	
	@Override
	public boolean hasPermission(Player byWhom) {
		if(anger > 0)
			return Toolbox.hasPermission(byWhom, "general.mobspawn.variants", "general.mobspawn.pig-zombie.angry", "general.mobspawn.neutral.angry");
		return true;
	}
	
	@Override
	public void setForMob(LivingEntity mob) {
		if(anger == 0) return; // No need to set it to 0 when it already is, right?
		if(!(mob instanceof PigZombie)) return;
		PigZombie zom = (PigZombie) mob;
		// Begin section of accessing internal Minecraft code
		// TODO: Rewrite using only Bukkit API
		CraftPigZombie cpz = (CraftPigZombie) zom;
		EntityPigZombie epz = (EntityPigZombie) cpz.getHandle();
		try {
			Field angerLevel = EntityPigZombie.class.getDeclaredField("angerLevel");
			angerLevel.setAccessible(true);
			angerLevel.setInt(epz, anger);
		} catch(SecurityException e) {}
		catch(NoSuchFieldException e) {}
		catch(IllegalArgumentException e) {}
		catch(IllegalAccessException e) {}
		// End section of accessing internal Minecraft code
	}
	
	@Override
	public void parse(Player setter, String data) {
		if(Toolbox.equalsOne(data, "calm", "passive")) anger = 0;
		else if(Toolbox.equalsOne(data, "angry", "mad", "aggressive", "hostile")) anger = 400;
		else {
			try {
				anger = Integer.parseInt(data);
			} catch(NumberFormatException e) {
				invalidate();
			}
		}
	}
	
	@Override
	public String getCostNode(String baseNode) {
		if(anger > 0) return baseNode + ".angry";
		return baseNode + ".regular";
	}
	
	@Override
	public void lacksPermission(Player fromWhom) {
		if(anger > 0) Messaging.lacksPermission(fromWhom, "spawn angry zombie pigmen");
	}

	@Override
	public String[] getValues() {
		return values.clone();
	}
	
}
