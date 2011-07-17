package net.craftstars.general.mobs;

import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CreeperState extends MobData {
	private boolean powered = false;
	private final static String[] values = new String[] {"regular","powered"};
	
	@Override
	public boolean hasPermission(Player byWhom) {
		if(powered)
			return Toolbox.hasPermission(byWhom, "general.mobspawn.variants", "general.mobspawn.creeper.powered");
		return true;
	}
	
	@Override
	public void setForMob(LivingEntity mob) {
		if(!(mob instanceof Creeper)) return;
		Creeper creep = (Creeper) mob;
		creep.setPowered(powered);
	}
	
	@Override
	public void parse(Player setter, String data) {
		if(Toolbox.equalsOne(data, "powered", "power", "zapped", "zap", "on", "high"))
			powered = true;
		else if(Toolbox.equalsOne(data, "weak", "off", "low"))
			powered = false;
		else invalidate();
	}
	
	@Override
	public String getCostNode(String baseNode) {
		if(powered) return baseNode + ".powered";
		return baseNode + ".regular";
	}
	
	@Override
	public void lacksPermission(Player fromWhom) {
		if(powered) Messaging.lacksPermission(fromWhom, "spawn powered creepers");
	}

	@Override
	public String[] getValues() {
		return values.clone();
	}
	
}
