package net.craftstars.general.mobs;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;

public class PigState extends AnimalData {
	private boolean saddled = false;
	private final static String[] values = new String[] {"regular","saddled"};
	
	public PigState() {
		super(MobType.PIG);
	}
	
	@Override
	public String getPermission(String base) {
		return super.getPermission(base) + "." + values[saddled?1:0];
	}
	
	@Override
	public void setForMob(LivingEntity mob) {
		super.setForMob(mob);
		if(!(mob instanceof Pig)) return;
		Pig swine = (Pig) mob;
		swine.setSaddle(saddled);
	}
	
	@Override
	public void parse(CommandSender setter, String data) {
		for(String component : data.split("[.,:/\\|]", 2)) {
			if(Toolbox.equalsOne(component, MobType.PIG.getDataList("saddle")))
				saddled = true;
			else if(Toolbox.equalsOne(component, MobType.PIG.getDataList("free")))
				saddled = false;
			else super.parse(setter, component);
		}
	}
	
	@Override
	public String getCostNode(String baseNode) {
		return super.getCostNode(baseNode) + "." + values[saddled?1:0];
	}

	@Override
	public String[] getValues() {
		return Toolbox.cartesianProduct(super.getValues(), values.clone(), '.');
	}
	
	@Override
	protected LanguageText getLangKey() {
		if(saddled) return null;
		return super.getLangKey();
	}
}
