package net.craftstars.general.mobs;

import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;

public class PigState extends MobData {
	private boolean saddled = false;
	private final static String[] values = new String[] {"regular","saddled"};
	
	public PigState() {
		super(MobType.PIG);
	}
	
	@Override
	public boolean hasPermission(CommandSender byWhom) {
		if(saddled) return byWhom.hasPermission("general.mobspawn.pig.saddled");
		return byWhom.hasPermission("general.mobspawn.pig.regular");
	}
	
	@Override
	public void setForMob(LivingEntity mob) {
		if(!(mob instanceof Pig)) return;
		Pig swine = (Pig) mob;
		swine.setSaddle(saddled);
	}
	
	@Override
	public void parse(CommandSender setter, String data) {
		if(Toolbox.equalsOne(data, MobType.PIG.getDataList("saddle")))
			saddled = true;
		else if(Toolbox.equalsOne(data, MobType.PIG.getDataList("free")))
			saddled = false;
		else invalidate();
	}
	
	@Override
	public String getCostNode(String baseNode) {
		if(saddled) return baseNode + ".saddled";
		return baseNode + ".regular";
	}
	
	@Override
	public void lacksPermission(CommandSender fromWhom) {
		if(saddled) Messaging.lacksPermission(fromWhom, "general.mobspawn.pig.saddled");
		else super.lacksPermission(fromWhom);
	}

	@Override
	public String[] getValues() {
		return values.clone();
	}
}
