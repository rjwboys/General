package net.craftstars.general.mobs;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;

public class CreeperState extends MobData {
	private boolean powered = false;
	private final static String[] values = new String[] {"regular","powered"};
	
	public CreeperState() {
		super(MobType.CREEPER);
	}
	
	@Override
	public String getPermission(String base) {
		return base + (powered ? "powered" : "regular");
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
		else invalidate(data);
	}
	
	@Override
	public String getCostNode(String baseNode) {
		if(powered) return baseNode + ".powered";
		return baseNode + ".regular";
	}
	
	@Override
	protected LanguageText getLangKey() {
		if(powered) return null;
		return super.getLangKey();
	}

	@Override
	public String[] getValues() {
		return values.clone();
	}
	
}
