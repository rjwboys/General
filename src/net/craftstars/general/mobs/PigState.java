package net.craftstars.general.mobs;

import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;

public class PigState extends MobData {
	private boolean saddled = false;
	
	@Override
	public boolean hasPermission(Player byWhom) {
		if(saddled)
			return Toolbox.hasPermission(byWhom, "general.mobspawn.variants", "general.mobspawn.pig.saddled");
		return true;
	}
	
	@Override
	public void setForMob(LivingEntity mob) {
		if(!(mob instanceof Pig)) return;
		Pig swine = (Pig) mob;
		swine.setSaddle(saddled);
	}
	
	@Override
	public void parse(Player setter, String data) {
		if(Toolbox.equalsOne(data, "tame", "saddle", "saddled"))
			saddled = true;
		else if(Toolbox.equalsOne(data, "wild", "unsaddled", "free"))
			saddled = false;
		else invalidate();
	}
	
	@Override
	public String getCostNode(String baseNode) {
		if(saddled) return baseNode + ".saddled";
		return baseNode + ".regular";
	}
	
	@Override
	public void lacksPermission(Player fromWhom) {
		if(saddled) Messaging.lacksPermission(fromWhom, "spawn saddled pigs");
	}
	
}
