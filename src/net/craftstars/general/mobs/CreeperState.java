package net.craftstars.general.mobs;

import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;

public class CreeperState extends MobData {
	private boolean powered = false;
	private final static String[] values = new String[] {"regular","powered"};
	
	@Override
	public boolean hasPermission(CommandSender byWhom) {
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
	public void parse(CommandSender setter, String data) {
		if(Toolbox.equalsOne(data, MobType.CREEPER.getDataList("powered")))
			powered = true;
		else if(Toolbox.equalsOne(data, MobType.CREEPER.getDataList("unpowered")))
			powered = false;
		else invalidate();
	}
	
	@Override
	public String getCostNode(String baseNode) {
		if(powered) return baseNode + ".powered";
		return baseNode + ".regular";
	}
	
	@Override
	public void lacksPermission(CommandSender fromWhom) {
		if(powered) Messaging.lacksPermission(fromWhom, "general.mobspawn.creeper.powered");
	}

	@Override
	public String[] getValues() {
		return values.clone();
	}
	
}
