package net.craftstars.general.mobs;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;

public class PigZombieAttitude extends MobData {
	private int anger = 0;
	private final static String[] values = new String[] {"regular","angry"};
	
	public PigZombieAttitude() {
		super(MobType.PIG_ZOMBIE);
	}
	
	@Override
	public String getPermission(String base) {
		if(anger > 0) return base + ".angry";
		return base + ".regular";
	}
	
	@Override
	public void setForMob(LivingEntity mob) {
		if(anger == 0) return; // No need to set it to 0 when it already is, right?
		if(!(mob instanceof PigZombie)) return;
		PigZombie zom = (PigZombie) mob;
		zom.setAnger(anger);
	}
	
	@Override
	public void parse(CommandSender setter, String data) {
		if(Toolbox.equalsOne(data, MobType.PIG_ZOMBIE.getDataList("calm"))) anger = 0;
		else if(Toolbox.equalsOne(data, MobType.PIG_ZOMBIE.getDataList("angry"))) anger = 400;
		else {
			try {
				anger = Integer.parseInt(data);
			} catch(NumberFormatException e) {
				invalidate(data);
			}
		}
	}
	
	@Override
	public String getCostNode(String baseNode) {
		if(anger > 0) return baseNode + ".angry";
		return baseNode + ".regular";
	}
	
	@Override
	protected LanguageText getLangKey() {
		if(anger > 0) return null;
		return super.getLangKey();
	}

	@Override
	public String[] getValues() {
		return values.clone();
	}
	
}
