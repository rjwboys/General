package net.craftstars.general.mobs;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

public final class NoData extends MobData {
	private final static String[] noneValues = new String[] {"regular"};
	
	NoData(MobType creature) {
		super(creature);
	}
	
	@Override
	public boolean hasPermission(CommandSender byWhom) {
		return true;
	}
	
	@Override
	public void setForMob(LivingEntity mob) {}
	
	@Override
	public void parse(CommandSender setter, String data) {}
	
	@Override
	public String getCostNode(String node) {
		return node + ".regular";
	}
	
	@Override
	public String[] getValues() {
		return noneValues.clone();
	}
}